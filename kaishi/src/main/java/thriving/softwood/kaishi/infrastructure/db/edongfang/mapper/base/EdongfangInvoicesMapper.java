package thriving.softwood.kaishi.infrastructure.db.edongfang.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangInvoices;

/**
 * <p>
 * 开票申请表 Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("edongfang")
public interface EdongfangInvoicesMapper extends BaseMapper<EdongfangInvoices> {}
