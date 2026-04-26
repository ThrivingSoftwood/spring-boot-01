package thriving.softwood.common.auth.infrastructure.db.master.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysDataRule;
import thriving.softwood.common.auth.infrastructure.db.master.mapper.base.SysDataRuleMapper;
import thriving.softwood.common.database.ancestor.AncestorServiceImpl;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-15
 */
@DS("master")
@Service
public class SysDataRuleRepo extends AncestorServiceImpl<SysDataRuleMapper, SysDataRule> {
    public List<SysDataRule> listAll() {
        return lambdaQuery().orderByAsc(SysDataRule::getId).list();
    }

    public void logicDeleteById(Long id) {
        lambdaUpdate().eq(SysDataRule::getId, id).set(SysDataRule::getDeleted, 1).update();
    }
}
