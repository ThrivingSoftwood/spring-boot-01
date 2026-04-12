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

public class KaishiMybatisPlusCodeGenerator {

    public static final String JAVA_DIR = "/kaishi/src/main/java/";

    private static final Map<String, DataSourceConfig.Builder> DS_BUILDER_MAP = new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 1L;
        {
            put("master", new DataSourceConfig.Builder(
                "jdbc:sqlserver://localhost:1433;DatabaseName=kaishi-plus;encrypt=false;trustServerCertificate=true;",
                "sa", decrypt("ENC(aa3411511790ce0c0c9e1ecc81f8b9f8)")));
            put("kaishi-2026",
                new DataSourceConfig.Builder(
                    "jdbc:sqlserver://localhost:1433;DatabaseName=凯诗防护2026;encrypt=false;trustServerCertificate=true;",
                    "sa", decrypt("ENC(aa3411511790ce0c0c9e1ecc81f8b9f8)")));

        }
    };

    private static final String[] TABLE_PREFIXES = {"base_", "t_"};

    public static void main(String[] args) {
        // generateCode("kaishi-2026", KAISHI_2026_TABLE_NAMES);
        generateCode("master", KAISHI_PLUS_TABLE_NAMES);
    }

    private static void generateCode(String dsName, String[] tables) {
        String dsPkgName = dsName.replace("-", "");

        FastAutoGenerator.create(DS_BUILDER_MAP.get(dsName)).globalConfig(builder -> {
            builder.author("meta-thriving").disableOpenDir().outputDir(System.getProperty("user.dir") + JAVA_DIR);
        }).packageConfig(builder -> {
            builder.parent(KAISHI_BASE_PACKAGE_NAME).moduleName(dsPkgName).entity(ENTITY_PKG_NAME)
                .mapper(MAPPER_PKG_NAME).controller(CONTROLLER_PKG_NAME).serviceImpl(REPO_PKG_NAME)
                .pathInfo(Collections.singletonMap(OutputFile.xml, getXmlPath(KAISHI_BASE_PACKAGE_NAME, dsPkgName)));
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
                .formatMapperFileName("%sMapper").enableBaseResultMap().enableBaseColumnList().enableFileOverride();
        }).injectionConfig(builder -> {
            Map<String, Object> customMap = new HashMap<>();
            // 此处为使用 @DS 注解切换数据源的配置, dsName 要按照配置文件内容来
            customMap.put("dsName", dsName);
            customMap.put("svcBeanName", KAISHI_BASE_PACKAGE_NAME);
            builder.customMap(customMap);
        }).templateEngine(new FreemarkerTemplateEngine()).execute();

        System.out.println("代码生成完成！");
    }

    private static String getXmlPath(String basePackage, String dsPkgName) {
        return System.getProperty("user.dir") + JAVA_DIR + basePackage.replace(".", "/") + "/" + dsPkgName + "/"
            + MAPPER_PKG_NAME.replace(".", "/");
    }

    private static String decrypt(String ciphertext) {
        Sm4Util.initLocal("b9cf15a5e53b4b85f5c59c0ac0887999", "3daf03771f64933bef3139d077b2e8c2");
        return Sm4Util.decLocal(ciphertext.replace("ENC(", "").replace(")", ""));
    }
}