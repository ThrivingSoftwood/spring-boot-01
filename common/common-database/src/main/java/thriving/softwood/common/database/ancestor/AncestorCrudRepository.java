package thriving.softwood.common.database.ancestor;

import java.util.Collection;

import javax.sql.DataSource;

import org.apache.ibatis.binding.MapperMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.repository.AbstractRepository;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;

/**
 * IService 实现类（ 泛型：M 是 mapper 对象，T 是实体 ）
 *
 * @author hubin
 * @since 2018-06-23
 */
public abstract class AncestorCrudRepository<M extends BaseMapper<T>, T> extends AbstractRepository<M, T> {

    @Autowired
    protected M baseMapper;

    @Override
    public M getBaseMapper() {
        Assert.notNull(baseMapper, "baseMapper can not be null");
        return baseMapper;
    }

    /**
     * 批量插入
     *
     * @param entityList ignore
     * @param batchSize ignore
     * @return ignore
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            return true;
        }

        // 🌟 2. 如果是 SQL Server，执行循环插入逻辑以回填 ID
        if (underSqlServer()) {
            for (T entity : entityList) {
                baseMapper.insert(entity);
            }
            return true;
        }

        // 🌟 4. 其他数据库类型（如 MySQL），继续使用高性能批量模式
        String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
    }

    /**
     * 私有辅助：判断当前数据源是否为 SQL Server
     */
    private boolean underSqlServer() {
        try {

            // 从当前的 SqlSessionFactory 获取数据源
            DataSource dataSource = getSqlSessionFactory().getConfiguration().getEnvironment().getDataSource();
            // 💡 在多数据源下，JdbcUtils.getDbType(dataSource) 是最可靠的，
            // 它会临时获取一个连接来判断 URL，从而识别出当前是 master 还是 ksplus
            DbType dbType = JdbcUtils.getDbType(dataSource.getConnection().getMetaData().getURL());
            return DbType.SQL_SERVER == dbType || DbType.SQL_SERVER2005 == dbType;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取mapperStatementId
     *
     * @param sqlMethod 方法名
     * @return 命名id
     * @since 3.4.0
     */
    private String getSqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.getSqlStatement(getMapperClass(), sqlMethod);
    }

    /**
     * 🌟 批量保存或更新
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            return true;
        }

        TableInfo tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
        Assert.notNull(tableInfo, "error: can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();

        if (underSqlServer()) {
            for (T entity : entityList) {
                Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
                // 🌟 修正点：使用 checkValNull 判断，使用 (Serializable) 强转传参
                if (StringUtils.checkValNull(idVal) || baseMapper.selectById((java.io.Serializable)idVal) == null) {
                    baseMapper.insert(entity);
                } else {
                    baseMapper.updateById(entity);
                }
            }
            return true;
        }

        // 标准模式：继续使用 MP 原生批量逻辑
        return SqlHelper.saveOrUpdateBatch(getSqlSessionFactory(), getMapperClass(), log, entityList, batchSize,
            (sqlSession, entity) -> {
                Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
                return StringUtils.checkValNull(idVal)
                    || CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
            }, (sqlSession, entity) -> {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                param.put(Constants.ENTITY, entity);
                sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
            });
    }

    /**
     * 🌟 批量按 ID 更新
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            return true;
        }

        if (underSqlServer()) {
            // SQL Server 模式：循环更新
            for (T entity : entityList) {
                baseMapper.updateById(entity);
            }
            return true;
        }

        // 标准模式
        String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);
        });
    }
}
