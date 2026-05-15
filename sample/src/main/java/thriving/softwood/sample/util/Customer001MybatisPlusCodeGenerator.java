package thriving.softwood.sample.util;

import static thriving.softwood.common.database.consts.MybatisPlusGenConst.*;

import java.io.Serial;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.model.ClassAnnotationAttributes;

import thriving.softwood.common.core.util.Sm4Util;
import thriving.softwood.common.database.ancestor.AncestorServiceImpl;

public class Customer001MybatisPlusCodeGenerator {

    private static final Map<String, DataSourceConfig.Builder> DS_BUILDER_MAP = new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 1L;
        {
            put("master", new DataSourceConfig.Builder("jdbc:postgresql://localhost:5432/ts_auth", "postgres",
                decrypt("ENC(d7307bc6a2f004d5a7ab5693a8e88b75)")));
            put("quotation", new DataSourceConfig.Builder("jdbc:postgresql://localhost:5432/ts_c1", "postgres",
                decrypt("ENC(d7307bc6a2f004d5a7ab5693a8e88b75)")));

        }
    };

    private static final String[] TABLE_PREFIXES = {"base_", "t_"};

    static void main(String[] args) {
        generateCode("master", new String[] {"sys_dictionary"});
        generateCode("quotation", new String[] {"product_info", "supplier_info", "contact_info", "quotation"});

    }

    private static void generateCode(String dsName, String[] tables) {
        String dsPkgName = dsName.replace("-", "");

        FastAutoGenerator.create(DS_BUILDER_MAP.get(dsName)).globalConfig(builder -> {
            builder.author("ThrivingSoftwood").disableOpenDir().outputDir(System.getProperty("user.dir") + JAVA_DIR);
        }).packageConfig(builder -> {
            builder.parent(BASE_PACKAGE_NAME).moduleName(dsPkgName).entity(ENTITY_PKG_NAME).mapper(MAPPER_PKG_NAME)
                .controller(CONTROLLER_PKG_NAME).serviceImpl(REPO_PKG_NAME)
                .pathInfo(Collections.singletonMap(OutputFile.xml, getXmlPath(BASE_PACKAGE_NAME, dsPkgName)));
        }).strategyConfig(builder -> {
            builder.addInclude(Arrays.asList(tables)).addTablePrefix(TABLE_PREFIXES).controllerBuilder().disable()
                .entityBuilder()
                // .superClass(AncestorDbEntity.class)
                .formatFileName("%s").enableSerialAnnotation().naming(NamingStrategy.underline_to_camel)
                .enableLombok(new ClassAnnotationAttributes("@Data", "lombok.Data"),
                    new ClassAnnotationAttributes("@NoArgsConstructor", "lombok.NoArgsConstructor")
            // ,new ClassAnnotationAttributes("@EqualsAndHashCode(callSuper = true)", "lombok.EqualsAndHashCode")
            ).enableTableFieldAnnotation().enableFileOverride().serviceBuilder().disableService()
                .superServiceImplClass(AncestorServiceImpl.class).formatServiceImplFileName("%sRepo")
                .serviceImplTemplate("/templates/freemarker/serviceImpl.java").enableFileOverride().mapperBuilder()
                .mapperTemplate("/templates/freemarker/mapper.java").formatXmlFileName("%sMapper")
                .formatMapperFileName("%sMapper").enableBaseResultMap().enableBaseColumnList().enableFileOverride()
            // 当只需要重新生成 Mapper 和 Entity 时放开注释 begin
            // .serviceBuilder().disableServiceImpl()
            // 当只需要重新生成 Mapper 和 Entity 时放开注释 end
            ;
        }).injectionConfig(builder -> {
            Map<String, Object> customMap = new HashMap<>();
            // 此处为使用 @DS 注解切换数据源的配置, dsName 要按照配置文件内容来
            customMap.put("dsName", dsName);
            customMap.put("svcBeanName", BASE_PACKAGE_NAME);
            builder.customMap(customMap);
        }).templateEngine(new FreemarkerTemplateEngine()).execute();

        System.out.println("代码生成完成！");
    }

    private static String getXmlPath(String basePackage, String dsPkgName) {
        return System.getProperty("user.dir") + JAVA_DIR + basePackage.replace(".", "/") + "/" + dsPkgName + "/"
            + MAPPER_PKG_NAME.replace(".", "/");
    }

    private static String decrypt(String ciphertext) {
        Sm4Util.initLocal("a1cc48bd9096b98daf8e369e1be05c77", "0e3096f2cf810363bc19c400b062750a");
        return Sm4Util.decLocal(ciphertext.replace("ENC(", "").replace(")", ""));
    }
}