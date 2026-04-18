package thriving.softwood.kaishi.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import thriving.softwood.kaishi.component.handler.UserDataPermissionHandler;

@AutoConfiguration(before = MybatisPlusAutoConfiguration.class)
public class MybatisPlusConfig {

    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 创建数据权限拦截器
        DataPermissionInterceptor dataPermissionInterceptor = new DataPermissionInterceptor();
        // 2. 将你的自定义 Handler 关联进去
        dataPermissionInterceptor.setDataPermissionHandler(new UserDataPermissionHandler());

        // 3. 将其添加到 MP 拦截器链中
        // 💡 注意：如果你还有分页插件，数据权限插件通常建议放在分页插件之后
        interceptor.addInnerInterceptor(dataPermissionInterceptor);

        // 1. 分页插件
        // 如果有多数据源可以不配具体类型, 否则都建议配上具体的 DbType
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 设置单页最大 1000 条，防止恶意参数打爆 JVM 内存
        paginationInterceptor.setMaxLimit(1000L);
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 2. 防全表更新与删除插件 (生产环境最后一道防线：拦截无 WHERE 条件的 update/delete)
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }
}