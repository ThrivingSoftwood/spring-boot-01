package thriving.softwood.common.message.infrastructure.db.master.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.common.message.infrastructure.db.master.entity.base.SysMessage;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-26
 */
@DS("master")
public interface SysMessageMapper extends BaseMapper<SysMessage> {}
