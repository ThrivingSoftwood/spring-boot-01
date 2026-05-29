package thriving.softwood.customer.first.biz.api.support;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;

import cn.hutool.v7.crypto.digest.BCrypt;
import cn.hutool.v7.json.JSONUtil;
import cn.hutool.v7.poi.excel.writer.ExcelWriter;
import jakarta.servlet.http.HttpServletResponse;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysRole;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysUser;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysUserRole;
import thriving.softwood.common.auth.infrastructure.db.master.repo.SysRoleRepo;
import thriving.softwood.common.auth.infrastructure.db.master.repo.SysUserRepo;
import thriving.softwood.common.auth.infrastructure.db.master.repo.SysUserRoleRepo;
import thriving.softwood.common.core.util.Sm4Util;
import thriving.softwood.customer.first.biz.pojo.record.ProvisionSupplierUserReq;
import thriving.softwood.customer.first.infrastructure.db.quotation.entity.base.SupplierInfo;
import thriving.softwood.customer.first.infrastructure.db.quotation.repo.SupplierInfoRepo;

@Service
public class SupplierInfoSvc {

    private final SupplierInfoRepo supplierInfoRepo;
    // 注入底层权限系统的 Repo (体现了我们架构模块间的高内聚、低耦合设计)
    private final SysUserRepo sysUserRepo;
    private final SysRoleRepo sysRoleRepo;
    private final SysUserRoleRepo sysUserRoleRepo;

    public SupplierInfoSvc(SupplierInfoRepo supplierInfoRepo, SysUserRepo sysUserRepo, SysRoleRepo sysRoleRepo,
        SysUserRoleRepo sysUserRoleRepo) {
        this.supplierInfoRepo = supplierInfoRepo;
        this.sysUserRepo = sysUserRepo;
        this.sysRoleRepo = sysRoleRepo;
        this.sysUserRoleRepo = sysUserRoleRepo;
    }

    /**
     * F-18: 导出供应商列表为 Excel (完全适配 Hutool v7 源码架构)
     */
    public void exportSuppliers(HttpServletResponse response) {
        // 1. 获取数据
        List<SupplierInfo> list = supplierInfoRepo.lambdaQuery().eq(SupplierInfo::getDeleted, 0).list();

        // 2. 构造 ExcelWriter (true 代表 xlsx 格式)
        // 源码构造: public ExcelWriter(final boolean isXlsx)
        try (ExcelWriter writer = new ExcelWriter(true)) {

            // 3. 访问配置对象 (ExcelWriteConfig)
            // 在 v7 中，别名管理由 config 对象持有
            var config = writer.getConfig();

            config.addHeaderAlias("supplierCode", "供应商编码");
            config.addHeaderAlias("supplierName", "供应商名称");
            config.addHeaderAlias("region", "所在地区");
            config.addHeaderAlias("contactName", "联系人");
            config.addHeaderAlias("contactPhone", "联系电话");
            config.addHeaderAlias("taxId", "开票税号");

            // 设置仅导出已定义别名的字段
            config.setOnlyAlias(true);

            // 4. 写出数据
            // 源码方法: public ExcelWriter write(final Iterable<?> data, final boolean isWriteKeyAsHead)
            writer.write(list, true);

            // 5. 自动调整列宽 (可选)
            // 源码方法: public ExcelWriter autoSizeColumnAll(final boolean useMergedCells, final float widthRatio)
            writer.autoSizeColumnAll(false, 1.5f);

            // 6. 配置 Jakarta Servlet 响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("供应商名录", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

            // 7. 刷出到输出流
            // 源码方法: public ExcelWriter flush(final OutputStream out)
            writer.flush(response.getOutputStream());

        } catch (Exception e) {
            // 统一异常处理，建议记录 Log 后抛出
            throw new RuntimeException("Excel 导出系统异常: " + e.getMessage(), e);
        }
    }

    /**
     * F-01~F-04: 为外部供应商开通系统账户
     */
    @DSTransactional(rollbackFor = Exception.class)
    public void provisionSupplierUser(ProvisionSupplierUserReq req) {
        SupplierInfo supplier = supplierInfoRepo.getById(req.supplierId());
        if (supplier == null) {
            throw new RuntimeException("目标供应商不存在");
        }

        // 1. 检查账号是否被占用
        if (sysUserRepo.getByLoginAccount(req.loginAccount()) != null) {
            throw new RuntimeException("账号 [" + req.loginAccount() + "] 已被注册");
        }

        // 2. 检查系统是否存在 "SUPPLIER" 角色，不存在则自动初始化创建 (鲁棒性设计)
        SysRole supplierRole = sysRoleRepo.lambdaQuery().eq(SysRole::getRoleCode, "SUPPLIER").one();
        if (supplierRole == null) {
            supplierRole = new SysRole();
            supplierRole.setRoleCode("SUPPLIER");
            supplierRole.setRoleName("外部供应商");
            supplierRole.setStatus((byte)1);
            sysRoleRepo.save(supplierRole);
        }

        // 3. 创建账号，并利用 ext_info 字段绑定 supplierCode (后续 ABAC 数据隔离的关键凭证！)
        SysUser user = new SysUser();
        user.setLoginAccount(req.loginAccount());
        user.setUsername(supplier.getSupplierName() + "-代表");
        // 前端传来的SM4密文解密后，做BCrypt强加密入库
        String plainPwd = Sm4Util.decWeb(req.plainPassword());
        user.setPassword(BCrypt.hashpw(plainPwd, BCrypt.gensalt()));
        user.setStatus((byte)1);

        // 🌟 核心防线：将外部身份标记打入 ext_info
        Map<String, String> extMap = Map.of("supplierCode", supplier.getSupplierCode());
        user.setExtInfo(JSONUtil.toJsonStr(extMap));
        sysUserRepo.save(user);

        // 4. 绑定角色关系
        SysUserRole relation = new SysUserRole();
        relation.setUserId(user.getId());
        relation.setRoleId(supplierRole.getId());
        sysUserRoleRepo.save(relation);
    }
}