package thriving.softwood.kaishi.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

import thriving.softwood.kaishi.component.handler.DataPermissionSqlBuilder;
import thriving.softwood.kaishi.component.handler.UserDataPermissionHandler;
import thriving.softwood.kaishi.component.interceptor.PlaceholderPermissionInterceptor;

@AutoConfiguration(before = MybatisPlusAutoConfiguration.class)
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(DataPermissionSqlBuilder sqlBuilder) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 🌟 1. 自定义占位符替换（必须在分页前面，修改了 beforeQuery 以支持 count 语句的安全生成）
        interceptor.addInnerInterceptor(new PlaceholderPermissionInterceptor(sqlBuilder));

        // 🌟 2. 标准 AST 数据权限拦截器（必须在分页前面！）
        DataPermissionInterceptor dataPermissionInterceptor = new DataPermissionInterceptor();
        dataPermissionInterceptor.setDataPermissionHandler(new UserDataPermissionHandler(sqlBuilder));
        interceptor.addInnerInterceptor(dataPermissionInterceptor);

        // 🌟 3. 分页插件（必须在权限拦截器之后！）
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        paginationInterceptor.setMaxLimit(1000L);
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 🌟 4. 防全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }
}