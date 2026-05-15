package thriving.softwood.customer.first.infrastructure.db.master.repo;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysDictionary;
import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.customer.first.infrastructure.db.master.mapper.base.SysDictionaryMapper;

/**
 * <p>
 * 系统字典表 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-05-15
 */
@DS("master")
@Service
public class SysDictionaryRepo extends AncestorServiceImpl<SysDictionaryMapper, SysDictionary> {

}
