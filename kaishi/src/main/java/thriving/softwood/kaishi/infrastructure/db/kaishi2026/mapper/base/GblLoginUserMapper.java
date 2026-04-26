package thriving.softwood.kaishi.infrastructure.db.kaishi2026.mapper.base;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.GblLoginUser;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-17
 */
@DS("kaishi-2026")
public interface GblLoginUserMapper extends BaseMapper<GblLoginUser> {}
