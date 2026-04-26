// File: provider/ResourceExtractor.java
package thriving.softwood.common.security.spi;

/**
 * 资源名称提取器接口 (SPI) 由各个业务模块（如 kaishi）实现，告诉安全底座当前执行的 Mapper 属于哪个业务资源
 */
public interface ResourceExtractor {
    /**
     * 解析 MappedStatementId (如 com.xxx.DlyBuyExtendMapper.selectList)
     * 
     * @return 资源名称 (如 DlyBuy)，如果不匹配则返回 null
     */
    String extract(String mappedStatementId);
}