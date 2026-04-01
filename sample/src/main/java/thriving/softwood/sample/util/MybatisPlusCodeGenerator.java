package thriving.softwood.sample.util;

import static thriving.softwood.common.database.consts.BaseConst.*;

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

public class MybatisPlusCodeGenerator {

    private static final Map<String, DataSourceConfig.Builder> DS_BUILDER_MAP = new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 1L;
        {
            put("mac-master", new DataSourceConfig.Builder("jdbc:postgresql://localhost:5432/splash-inkflow",
                "postgres", decrypt("ENC(aa3411511790ce0c0c9e1ecc81f8b9f8)")));
            put("mac-novel", new DataSourceConfig.Builder("jdbc:postgresql://localhost:5432/weaving-stars", "postgres",
                decrypt("ENC(aa3411511790ce0c0c9e1ecc81f8b9f8)")));
            put("mac-material", new DataSourceConfig.Builder("jdbc:postgresql://localhost:5432/source-material",
                "postgres", decrypt("ENC(aa3411511790ce0c0c9e1ecc81f8b9f8)")));
            put("mac-embedding", new DataSourceConfig.Builder("jdbc:postgresql://localhost:5432/embedding-libraries",
                "postgres", decrypt("ENC(aa3411511790ce0c0c9e1ecc81f8b9f8)")));

        }
    };

    private static final String[] TABLE_PREFIXES = {"sys_", "base_", "t_"};

    public static void main(String[] args) {
        // generateCode("master", MASTER_TABLE_NAMES);
        // generateCode("novel", NOVEL_TABLE_NAMES);
        // generateCode("material", MATERIAL_TABLE_NAMES);
        generateCode("master", MASTER_TABLE_NAMES);
        // generateCode("mac-novel", MAC_NOVEL_TABLE_NAMES);
        generateCode("material", MATERIAL_TABLE_NAMES);
        // generateCode("mac-embedding", MAC_EMBEDDING_TABLE_NAMES);
    }

    private static void generateCode(String dsName, String[] tables) {
        String basePackage = "thriving.softwood.sample.infrastructure.db";
        String actualDsName = dsName;

        FastAutoGenerator.create(DS_BUILDER_MAP.get(dsName)).globalConfig(builder -> {
            builder.author("meta-thriving").disableOpenDir()
                .outputDir(System.getProperty("user.dir") + "/crud/src/main/java");
        }).packageConfig(builder -> {
            builder.parent(basePackage).moduleName(actualDsName).entity(ENTITY_PKG_NAME).mapper(MAPPER_PKG_NAME)
                .controller(CONTROLLER_PKG_NAME).serviceImpl(REPO_PKG_NAME)
                .pathInfo(Collections.singletonMap(OutputFile.xml, getXmlPath(actualDsName)));
        }).strategyConfig(builder -> {
            builder.addInclude(Arrays.asList(tables)).addTablePrefix(TABLE_PREFIXES).controllerBuilder().disable()
                .entityBuilder().formatFileName("%s").enableSerialAnnotation().naming(NamingStrategy.underline_to_camel)
                .enableLombok(new ClassAnnotationAttributes("@Data", "lombok.Data"),
                    new ClassAnnotationAttributes("@NoArgsConstructor", "lombok.NoArgsConstructor"))
                .enableTableFieldAnnotation().enableFileOverride().serviceBuilder().disableService()
                .superServiceImplClass(AncestorServiceImpl.class).formatServiceImplFileName("%sRepo")
                .serviceImplTemplate("/templates/freemarker/serviceImpl.java").enableFileOverride().mapperBuilder()
                .mapperTemplate("/templates/freemarker/mapper.java").formatXmlFileName("%sMapper")
                .formatMapperFileName("%sMapper").enableBaseResultMap().enableBaseColumnList().enableFileOverride();
        }).injectionConfig(builder -> {
            Map<String, Object> customMap = new HashMap<>();
            // 此处为使用 @DS 注解切换数据源的配置, dsName 要按照配置文件内容来
            customMap.put("dsName", dsName);
            customMap.put("svcBeanName", basePackage);
            builder.customMap(customMap);
        }).templateEngine(new FreemarkerTemplateEngine()).execute();

        System.out.println("代码生成完成！");
    }

    private static String getXmlPath(String actualDsName) {
        return System.getProperty("user.dir") + "/crud/src/main/resources/mapper/" + actualDsName;
    }

    private static String decrypt(String ciphertext) {
        Sm4Util.init("b9cf15a5e53b4b85f5c59c0ac0887999", "3daf03771f64933bef3139d077b2e8c2");
        return Sm4Util.decrypt(ciphertext.replace("ENC(", "").replace(")", ""));
    }
}