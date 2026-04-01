package thriving.softwood.common.database.component.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

@Configuration
// todo 指定 mapper 类的具体位置
@MapperScan("thriving.softwood.common.mapper")
public class MybatisPlusConfig {

    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 防全表更新与删除插件 (生产环境最后一道防线：拦截无 WHERE 条件的 update/delete)
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        // 2. 分页插件
        // 如果配置多个插件, 切记分页最后添加
        // 如果有多数据源可以不配具体类型, 否则都建议配上具体的 DbType
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 设置单页最大 1000 条，防止恶意参数打爆 JVM 内存
        paginationInterceptor.setMaxLimit(1000L);
        interceptor.addInnerInterceptor(paginationInterceptor);
        return interceptor;
    }
}