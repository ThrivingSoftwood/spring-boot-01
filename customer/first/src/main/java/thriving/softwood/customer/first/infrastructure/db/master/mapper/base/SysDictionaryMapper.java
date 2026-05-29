package thriving.softwood.customer.first.infrastructure.db.master.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.customer.first.infrastructure.db.master.entity.base.SysDictionary;

/**
 * <p>
 * 系统字典表 Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-05-15
 */
@DS("master")
public interface SysDictionaryMapper extends BaseMapper<SysDictionary> {}
