package ${package.Mapper};

import com.baomidou.dynamic.datasource.annotation.DS;
<#list importMapperFrameworkPackages as pkg>
    import ${pkg};
</#list>
<#if importMapperJavaPackages?size !=0>

    <#list importMapperJavaPackages as pkg>
        import ${pkg};
    </#list>
</#if>

/**
* <p>
    * ${table.comment!} Mapper 接口
    * </p>
*
* @author ${author}
* @since ${date}
*/
<#if mapperAnnotationClass??>
    @${mapperAnnotationClass.simpleName}
</#if>
@DS("${dsName}")
public interface ${table.mapperName} extends ${superMapperClass}<${entity}> {
<#list mapperMethodList as m>
    /**
    * generate by ${m.indexName}
    *
    <#list m.tableFieldList as f>
        * @param ${f.propertyName} ${f.comment}
    </#list>
    */
    ${m.method}
</#list>
}

