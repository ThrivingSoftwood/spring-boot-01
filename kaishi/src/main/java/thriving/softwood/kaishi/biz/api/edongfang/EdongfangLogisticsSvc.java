package thriving.softwood.kaishi.biz.api.edongfang;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.v7.core.data.id.IdUtil;
import cn.hutool.v7.core.text.StrUtil;
import thriving.softwood.kaishi.biz.pojo.record.EdongfangLogisticQryReq;
import thriving.softwood.kaishi.biz.pojo.vo.EdongfangLogisticsDetailVO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangLogistics;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangLogisticsItems;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangLogisticsTracks;
import thriving.softwood.kaishi.infrastructure.db.edongfang.repo.EdongfangLogisticsItemsRepo;
import thriving.softwood.kaishi.infrastructure.db.edongfang.repo.EdongfangLogisticsRepo;
import thriving.softwood.kaishi.infrastructure.db.edongfang.repo.EdongfangLogisticsTracksRepo;

/**
 * 物流发货查询服务 (CQRS 读)
 *
 * @author CodeOmni
 */
@Service
@DS("edongfang")
public class EdongfangLogisticsSvc implements EdongfangLogisticsApi {

    private final EdongfangLogisticsRepo logisticsRepo;
    private final EdongfangLogisticsItemsRepo logisticsItemsRepo;
    private final EdongfangLogisticsTracksRepo logisticsTracksRepo;

    public EdongfangLogisticsSvc(EdongfangLogisticsRepo logisticsRepo, EdongfangLogisticsItemsRepo logisticsItemsRepo,
        EdongfangLogisticsTracksRepo logisticsTracksRepo) {
        this.logisticsRepo = logisticsRepo;
        this.logisticsItemsRepo = logisticsItemsRepo;
        this.logisticsTracksRepo = logisticsTracksRepo;
    }

    /**
     * 📊 1. 发货已确认信息分页列表
     */
    @Override
    public Page<EdongfangLogistics> pageLogistics(long pageNo, long pageSize, EdongfangLogisticQryReq logisticsReq) {
        Page<EdongfangLogistics> page = new Page<>(pageNo, pageSize);

        return logisticsRepo.lambdaQuery()
            // 支持按 E采平台订单编号 精准查询
            .eq(StrUtil.isNotBlank(logisticsReq.eOrderId()), EdongfangLogistics::getEOrderId, logisticsReq.eOrderId())
            // 支持按 物流号 模糊查询
            .like(StrUtil.isNotBlank(logisticsReq.expressNo()), EdongfangLogistics::getExpressNo,
                logisticsReq.expressNo())
            // 默认按更新时间倒序
            .orderByDesc(EdongfangLogistics::getUpdateTime).page(page);
    }

    /**
     * 🔍 2. 发货信息详情查询 (全景视图) 根据主键 pk 查询，附带商品和轨迹
     */
    @Override
    public EdongfangLogisticsDetailVO getLogisticsDetail(String pk) {
        // 1. 查主表
        EdongfangLogistics logistics = logisticsRepo.getById(pk);
        if (logistics == null) {
            throw new RuntimeException("物流发货信息不存在，主键: " + pk);
        }

        // 2. 查该包裹发了哪些商品 (走 parent 索引)
        List<EdongfangLogisticsItems> items =
            logisticsItemsRepo.lambdaQuery().eq(EdongfangLogisticsItems::getParent, pk).list();

        // 3. 查该包裹的物流轨迹 (走 parent 索引，按发生时间倒序排)
        List<EdongfangLogisticsTracks> tracks = logisticsTracksRepo.lambdaQuery()
            .eq(EdongfangLogisticsTracks::getParent, pk).orderByDesc(EdongfangLogisticsTracks::getOperateTime).list();

        return new EdongfangLogisticsDetailVO(logistics, items, tracks);
    }

    /**
     * ✍️ 修改发货已确认主信息
     */
    @Override
    public void updateLogistics(EdongfangLogistics req) {
        // 安全校验：更新操作必须有主键
        if (StrUtil.isBlank(req.getPk())) {
            throw new IllegalArgumentException("修改物流发货信息失败：主键 pk 不能为空");
        }

        // 检查记录是否存在
        EdongfangLogistics existRecord = logisticsRepo.getById(req.getPk());
        if (existRecord == null) {
            throw new RuntimeException("目标物流记录不存在，可能已被删除！");
        }

        // 执行更新 (MyBatis-Plus 的 updateById 会自动忽略 null 字段，进行局部更新)
        // 并且配置的 MetaObjectHandler 会自动刷新 update_time 字段
        logisticsRepo.updateById(req);

        // 💡 导师提示：如果物流信息的修改（如修改了物流单号 express_no）需要同步触发 E采平台消息，
        // 可以在这里注入 EdongfangMessagesRepo 并向其中插入一条对应 type 的消息。
        // 根据你目前的需求文档，此处暂时只做数据库状态的更新。
    }

    @Override
    public void addLogistics(EdongfangLogistics logistics) {
        logistics.setPk(IdUtil.fastSimpleUUID());
        logisticsRepo.save(logistics);
    }
}