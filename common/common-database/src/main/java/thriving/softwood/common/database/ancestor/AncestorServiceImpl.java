package thriving.softwood.common.database.ancestor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * IService 实现类（ 泛型：M 是 mapper 对象，T 是实体 ）
 *
 * @author hubin
 * @since 2018-06-23
 */
public class AncestorServiceImpl<M extends BaseMapper<T>, T> extends AncestorCrudRepository<M, T>
    implements IService<T> {

}
