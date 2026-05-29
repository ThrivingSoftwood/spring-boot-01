package thriving.softwood.customer.first.biz.api.quotation;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.hutool.v7.json.JSONUtil;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysUser;
import thriving.softwood.common.auth.infrastructure.db.master.repo.SysUserRepo;
import thriving.softwood.common.core.exception.DetailException;
import thriving.softwood.common.message.enums.MessageTypeEnum;
import thriving.softwood.common.message.spi.MessageProvider;
import thriving.softwood.common.security.context.UserContext;
import thriving.softwood.customer.first.biz.api.support.QuotationTxHelper;
import thriving.softwood.customer.first.biz.pojo.record.QuotationSubmitReq;
import thriving.softwood.customer.first.biz.pojo.vo.QuotationRankVO;
import thriving.softwood.customer.first.infrastructure.db.quotation.mapper.extend.QuotationExtendMapper;

/**
 * 报价核心引擎：负责调度锁、执行事务、计算排名及下发通知
 */
@Service
public class QuotationSvc {
    private static final Logger logger = LoggerFactory.getLogger(QuotationSvc.class);

    private final QuotationLockApi quotationLockApi;
    private final QuotationTxHelper txHelper;
    private final QuotationExtendMapper extendMapper;
    private final MessageProvider messageProvider;
    private final SysUserRepo sysUserRepo;

    public QuotationSvc(QuotationLockApi quotationLockApi, QuotationTxHelper txHelper,
        QuotationExtendMapper extendMapper, MessageProvider messageProvider, SysUserRepo sysUserRepo) {
        this.quotationLockApi = quotationLockApi;
        this.txHelper = txHelper;
        this.extendMapper = extendMapper;
        this.messageProvider = messageProvider;
        this.sysUserRepo = sysUserRepo;
    }

    /**
     * 解析当前登录用户的 supplierCode
     */
    private String getCurrentSupplierCode() {
        SysUser user = sysUserRepo.getById(UserContext.userId());
        if (user == null || user.getExtInfo() == null) {
            throw new DetailException("当前账号未绑定供应商身份，无法发起报价！");
        }
        String code = JSONUtil.parseObj(user.getExtInfo()).getStr("supplierCode");
        if (code == null) {
            throw new RuntimeException("供应商身份凭证丢失");
        }
        return code;
    }

    /**
     * 🚀 核心：提交报价 (加锁 -> 查旧排名 -> 事务写入 -> 查新排名 -> 触发通知)
     */
    public void submitQuotation(QuotationSubmitReq req) {
        String supplierCode = getCurrentSupplierCode();
        String productCode = req.productCode();

        // 1. 获取商品级分布式/本地锁 (等待最多 5 秒)
        boolean locked;
        try {
            locked = quotationLockApi.tryLock(productCode, 5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("系统繁忙，请稍后再试");
        }

        if (!locked) {
            throw new RuntimeException("当前商品报价人数过多，请稍后重试");
        }

        try {
            // 2. 拍下操作前的数据快照
            List<QuotationRankVO> oldRanks = extendMapper.listValidQuotationsWithRank(productCode);

            // 3. 🛡️ 执行数据库事务 (保证 Commit 后再释放锁)
            txHelper.executeSubmitQuotation(supplierCode, req);

            // 4. 获取操作后的新数据快照
            List<QuotationRankVO> newRanks = extendMapper.listValidQuotationsWithRank(productCode);

            // 5. 🔔 触发智能通知引擎
            triggerRankDiffNotifications(productCode, req.productName(), oldRanks, newRanks);

        } finally {
            // 6. 绝对安全的锁释放
            quotationLockApi.unlock(productCode);
        }
    }

    /**
     * 🧠 智能排名 Diff 与通知引擎
     */
    private void triggerRankDiffNotifications(String productCode, String productName, List<QuotationRankVO> oldRanks,
        List<QuotationRankVO> newRanks) {

        // --- 逻辑 1: 判断排名前三是否发生人员变动 (F-05 给管理员发) ---
        List<String> oldTop3 = oldRanks.stream().limit(3).map(QuotationRankVO::getSupplierCode).toList();
        List<String> newTop3 = newRanks.stream().limit(3).map(QuotationRankVO::getSupplierCode).toList();

        if (!oldTop3.equals(newTop3)) {
            // 发送给系统内部采购员工 (这里假设 receiverId = 1L 为默认接收人)
            messageProvider.saveAndPushAsync(1L, MessageTypeEnum.SYSTEM_NOTIFICATION,
                String.format("商品【%s】的报价前三名发生变动，请及时关注大盘。", productName), "RANK_TOP3_" + productCode);
        }

        // --- 逻辑 2: 检测各个供应商自身排名是否变动 (F-06 给对应供应商发) ---
        Map<String, Integer> oldRankMap =
            oldRanks.stream().collect(Collectors.toMap(QuotationRankVO::getSupplierCode, QuotationRankVO::getRankNum));

        // 获取所有供应商绑定的外部账户 (模拟反向查询，实际可加缓存)
        List<SysUser> allSuppliers = sysUserRepo.lambdaQuery().like(SysUser::getExtInfo, "supplierCode").list();

        for (QuotationRankVO newRank : newRanks) {
            Integer oldRankNum = oldRankMap.get(newRank.getSupplierCode());

            // 如果旧排名为空(新入局)，或者排名发生了变化
            if (!Objects.equals(oldRankNum, newRank.getRankNum())) {

                // 找出该供应商对应的 userId
                allSuppliers.stream()
                    .filter(
                        u -> JSONUtil.parseObj(u.getExtInfo()).getStr("supplierCode").equals(newRank.getSupplierCode()))
                    .findFirst().ifPresent(user -> {
                        String msg =
                            String.format("商品【%s】最新的排名测算完成，您当前的报价排名为第 %d 名。", productName, newRank.getRankNum());
                        messageProvider.saveAndPushAsync(user.getId(), MessageTypeEnum.SYSTEM_NOTIFICATION, msg,
                            "RANK_SUPPLIER_" + productCode);
                    });
            }
        }
    }

    /**
     * 🚀 F-14: 删除/撤销当前有效报价 (加锁 -> 查旧排名 -> 事务更新 -> 查新排名 -> 触发通知)
     */
    public void deleteQuotation(Long quotationId, String productCode) {
        String supplierCode = getCurrentSupplierCode();

        boolean locked;
        try {
            locked = quotationLockApi.tryLock(productCode, 5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("系统繁忙，请稍后再试");
        }

        if (!locked) {
            throw new RuntimeException("当前商品正有其他供应商报价，请稍后重试");
        }

        try {
            // 拍下操作前快照
            List<QuotationRankVO> oldRanks = extendMapper.listValidQuotationsWithRank(productCode);

            // 执行逻辑删除与失效
            txHelper.executeDeleteQuotation(supplierCode, quotationId);

            // 获取新快照
            List<QuotationRankVO> newRanks = extendMapper.listValidQuotationsWithRank(productCode);

            // 触发排名重算通知
            // 💡 productName 这里可以用 productInfoRepo 查询，为了减少依赖，可按需补充查询
            triggerRankDiffNotifications(productCode, "该商品", oldRanks, newRanks);
        } finally {
            quotationLockApi.unlock(productCode);
        }
    }

    /**
     * F-16: 供应商获取“我的报价”列表 (自带全网真实排名)
     */
    public List<QuotationRankVO> listMyQuotations() {
        return extendMapper.listMyQuotationsWithRank(getCurrentSupplierCode());
    }

    /**
     * F-17: 管理员获取某商品报价大盘
     */
    public List<QuotationRankVO> listDashboard(String productCode) {
        return extendMapper.listValidQuotationsWithRank(productCode);
    }
}