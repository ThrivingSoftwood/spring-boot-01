package thriving.softwood.customer.first.infrastructure.db.quotation.mapper.extend;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;

import thriving.softwood.customer.first.biz.pojo.vo.QuotationRankVO;

@DS("master")
public interface QuotationExtendMapper {

    /**
     * F-17: 获取指定商品的所有有效报价及动态排名 (供管理员大盘使用) 排序规则：价格从低到高，价格相同则按更新时间从早到晚
     */
    List<QuotationRankVO> listValidQuotationsWithRank(@Param("productCode") String productCode);

    /**
     * F-16: 获取指定供应商的所有有效报价及真实全网排名 (供“我的报价”使用) 🌟 核心：使用 @InterceptorIgnore 绕过动态数据权限拦截，保证排名基数是全网商品！
     */
    @InterceptorIgnore(dataPermission = "1")
    List<QuotationRankVO> listMyQuotationsWithRank(@Param("supplierCode") String supplierCode);
}