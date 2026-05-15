package thriving.softwood.customer.first.infrastructure.db.kaishi2026.mapper.extend;

import java.util.List;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import thriving.softwood.common.security.annotation.ReplacePlaceholder;
import thriving.softwood.customer.first.biz.pojo.dto.DlyBuyDTO;
import thriving.softwood.customer.first.biz.pojo.dto.PurchaseOrderTraceDTO;
import thriving.softwood.customer.first.infrastructure.db.kaishi2026.entity.extend.PurchaseTraceOrder;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-06
 */
@DS("cust0001-2026")
public interface DlyBuyExtendMapper {

    @ReplacePlaceholder
    @InterceptorIgnore(dataPermission = "1")
    Long countTraceInfo(PurchaseOrderTraceDTO dto);

    @ReplacePlaceholder
    @InterceptorIgnore(dataPermission = "1")
    List<DlyBuyDTO> listTraceInfo(PurchaseOrderTraceDTO dto);

    @ReplacePlaceholder
    @InterceptorIgnore(dataPermission = "1")
    List<PurchaseTraceOrder> listTraceDetail(PurchaseOrderTraceDTO dto);

}