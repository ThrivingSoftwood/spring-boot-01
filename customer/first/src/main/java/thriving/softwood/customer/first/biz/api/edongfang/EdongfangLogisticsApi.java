package thriving.softwood.customer.first.biz.api.edongfang;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import thriving.softwood.customer.first.biz.pojo.record.EdongfangLogisticQryReq;
import thriving.softwood.customer.first.biz.pojo.vo.EdongfangLogisticsDetailVO;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangLogistics;

/**
 * 物流发货查询服务 (CQRS 读)
 *
 * @author CodeOmni
 */
public interface EdongfangLogisticsApi {

    /**
     * 📊 1. 发货已确认信息分页列表
     */
    Page<EdongfangLogistics> pageLogistics(long pageNo, long pageSize, EdongfangLogisticQryReq logisticsReq);

    /**
     * 🔍 2. 发货信息详情查询 (全景视图) 根据主键 pk 查询，附带商品和轨迹
     */
    EdongfangLogisticsDetailVO getLogisticsDetail(String pk);

    void updateLogistics(EdongfangLogistics req);

    void addLogistics(EdongfangLogistics logistics);
}