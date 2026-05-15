package thriving.softwood.customer.first.infrastructure.db.quotation.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.customer.first.infrastructure.db.quotation.entity.base.ContactInfo;

/**
 * <p>
 * 联系人表 Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-05-15
 */
@DS("quotation")
public interface ContactInfoMapper extends BaseMapper<ContactInfo> {}
