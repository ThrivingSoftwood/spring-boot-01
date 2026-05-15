package thriving.softwood.customer.first.biz.pojo.dto;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangOrderStub;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangOrders;

@Data
@NoArgsConstructor
public class EdongfangOrderDTO extends EdongfangOrders implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public EdongfangOrderDTO(EdongfangOrders order, EdongfangOrderStub stub) {
        setPk(order.getPk());
        setEOrderId(order.getEOrderId());
        setName(order.getName());
        setProvince(order.getProvince());
        setProvinceName(order.getProvinceName());
        setCity(order.getCity());
        setCityName(order.getCityName());
        setCounty(order.getCounty());
        setCountyName(order.getCountyName());
        setPurchaser(order.getPurchaser());
        setAddress(order.getAddress());
        setZip(order.getZip());
        setPhone(order.getPhone());
        setMobile(order.getMobile());
        setEmail(order.getEmail());
        setRemark(order.getRemark());
        setInvoiceTitle(order.getInvoiceTitle());
        setInvoiceType(order.getInvoiceType());
        setInvoiceTaxNum(order.getInvoiceTaxNum());
        setInvoiceBank(order.getInvoiceBank());
        setInvoiceBankAccount(order.getInvoiceBankAccount());
        setInvoiceAddress(order.getInvoiceAddress());
        setInvoicePhone(order.getInvoicePhone());
        setPayment(order.getPayment());
        setOrderPrice(order.getOrderPrice());
        setFreight(order.getFreight());
        setDepName(order.getDepName());
        setPurchaserPhone(order.getPurchaserPhone());
        setPurchaserMobile(order.getPurchaserMobile());
        setPurchaserEmail(order.getPurchaserEmail());
        setCreateBy(order.getCreateBy());
        setCreateTime(order.getCreateTime());
        setUpdateBy(order.getUpdateBy());
        setUpdateTime(order.getUpdateTime());
        setDeleted(order.getDeleted());
        setTenantId(order.getTenantId());
        setCu(order.getCu());
        setSubmitState(order.getSubmitState());
        setRefundStatus(order.getRefundStatus());
        setStatus(order.getStatus());

        if (null == stub) {
            return;
        }
        stubId = stub.getId();
        preorderNotified = stub.getPreorderNotified();
        pendingActionStatus = stub.getPendingActionStatus();
    }

    private Long stubId;
    private Boolean preorderNotified;
    private Integer pendingActionStatus;
}
