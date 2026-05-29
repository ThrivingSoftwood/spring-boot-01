// File: component/handler/DataPermissionSqlBuilder.java
package thriving.softwood.kaishi.component.handler;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import cn.hutool.v7.core.text.StrUtil;
import thriving.softwood.common.security.context.UserContext;
import thriving.softwood.common.security.handler.AbstractDataPermissionSqlBuilder;
import thriving.softwood.common.security.pojo.dto.DataRuleDTO;
import thriving.softwood.common.security.spi.ResourceExtractor;

@Component
public abstract class DataPermissionSqlBuilder extends AbstractDataPermissionSqlBuilder {

    public DataPermissionSqlBuilder(ObjectProvider<ResourceExtractor> extractorProvider) {
        super(extractorProvider);
    }

    @Override
    protected String buildFragmentForRule(DataRuleDTO rule) {
        String employeeTypeId = UserContext.employeeTypeId();
        String departmentTypeId = UserContext.departmentTypeId();

        switch (rule.getScopeType()) {
            case 2:
                return "a.etypeid = '" + employeeTypeId + "'";
            case 3:
                return "a.projectid = '" + departmentTypeId + "'";
            case 4:
                return "a.projectid IN (SELECT typeid FROM department WHERE parid ='" + departmentTypeId
                    + "' OR typeid = '" + departmentTypeId + "')";
            case 5:
                if (StrUtil.isNotBlank(rule.getCustomSqlJson())) {
                    return getSqlFragmentBuilder(rule).toString();
                }
        }
        return null;
    }
}