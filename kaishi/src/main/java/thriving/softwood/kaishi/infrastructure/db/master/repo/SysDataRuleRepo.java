package thriving.softwood.kaishi.infrastructure.db.master.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysDataRule;
import thriving.softwood.kaishi.infrastructure.db.master.mapper.base.SysDataRuleMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author meta-thriving
 * @since 2026-04-15
 */
@DS("master")
@Service
public class SysDataRuleRepo extends AncestorServiceImpl<SysDataRuleMapper, SysDataRule> {
    public List<SysDataRule> listAll() {
        return lambdaQuery().eq(SysDataRule::getDeleted, 0).orderByAsc(SysDataRule::getId).list();
    }

    public void logicDeleteById(Long id) {
        lambdaUpdate().eq(SysDataRule::getId, id).set(SysDataRule::getDeleted, 1).update();
    }
}
