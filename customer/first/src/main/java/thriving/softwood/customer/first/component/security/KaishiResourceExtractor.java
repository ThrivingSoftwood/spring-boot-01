package thriving.softwood.customer.first.component.security;

import org.springframework.stereotype.Component;
import thriving.softwood.common.security.spi.ResourceExtractor;

@Component
public class KaishiResourceExtractor implements ResourceExtractor {

    /**
     * 获取目标表 todo 后续有需要扩展成字典表
     *
     * @param mappedStatementId
     * @return
     */
    @Override
    public String extract(String mappedStatementId) {
        if (mappedStatementId.contains("DlyBuyExtendMapper")) {
            return "DlyBuy";
        }
        return null;
    }
}