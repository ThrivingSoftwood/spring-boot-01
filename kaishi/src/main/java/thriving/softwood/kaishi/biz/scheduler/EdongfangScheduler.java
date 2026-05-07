package thriving.softwood.kaishi.biz.scheduler;

import static thriving.softwood.common.message.enums.MessageTypeEnum.SYSTEM_NOTIFICATION;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.common.message.infrastructure.db.master.entity.base.SysMessage;
import thriving.softwood.common.message.spi.MessageProvider;
import thriving.softwood.common.security.util.SecurityUtil;
import thriving.softwood.kaishi.biz.pojo.dto.EdongfangLogisticDTO;
import thriving.softwood.kaishi.biz.pojo.dto.EdongfangOrderDTO;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrderStub;
import thriving.softwood.kaishi.infrastructure.db.edongfang.repo.EdongfangLogisticsRepo;
import thriving.softwood.kaishi.infrastructure.db.edongfang.repo.EdongfangOrderStubRepo;
import thriving.softwood.kaishi.infrastructure.db.edongfang.repo.EdongfangOrdersRepo;

/**
 * 订单生命周期监控与自动化预警引擎
 * 
 * @author CodeOmni (Refactored)
 */
@Component
@DS("edongfang")
public class EdongfangScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EdongfangScheduler.class);

    private final EdongfangOrdersRepo ordersRepo;
    private final EdongfangLogisticsRepo logisticsRepo;
    private final EdongfangOrderStubRepo stubRepo;
    private final MessageProvider messageProvider;

    public EdongfangScheduler(EdongfangOrdersRepo ordersRepo, EdongfangLogisticsRepo logisticsRepo,
        EdongfangOrderStubRepo stubRepo, MessageProvider messageProvider) {
        this.ordersRepo = ordersRepo;
        this.logisticsRepo = logisticsRepo;
        this.stubRepo = stubRepo;
        this.messageProvider = messageProvider;
    }

    @Scheduled(fixedDelay = 15000)
    public void scanAndAlert() {
        SecurityUtil.runAsSystem(() -> {
            try {
                // todo 暂时硬编码给管理员发,后续要设置权限
                Long receiverId = 1L;
                String maxOrderId = ordersRepo.getMaxOrderId();

                scanOrders(maxOrderId, receiverId);
                scanLogistics(maxOrderId, receiverId);

            } catch (Exception e) {
                logger.error("🚨 订单轮询引擎执行异常!", e);
            }
        });
    }

    private void scanOrders(String maxOrderId, Long receiverId) {
        List<EdongfangOrderDTO> dtos = ordersRepo.listUnnotified(maxOrderId);
        if (dtos.isEmpty()) {
            return;
        }

        // 🌟 优化：收集所有待更新的存根，最后批量处理
        List<EdongfangOrderStub> stubsToSave = new ArrayList<>();

        for (EdongfangOrderDTO dto : dtos) {
            // 🌟 修复关键漏洞：每次必须 new 一个干净的实体
            EdongfangOrderStub currentStub = stubRepo.loadStub(dto);

            // 1. 处理新订单
            if (!dto.getPreorderNotified()) {
                String content = String.format("E采平台有一笔新增订单【%s】收货人【%s】采购人【%s】金额【%s】，待发货处理。", dto.getEOrderId(),
                    dto.getName(), dto.getPurchaser(), dto.getOrderPrice());
                loadAndSendMsg(receiverId, "新预购订单提醒", content, dto.getEOrderId());
                currentStub.setPreorderNotified(true);
            }

            // 2. 处理客户确认/取消状态
            if (dto.getSubmitState() != null && dto.getSubmitState() != 0
                && !currentStub.getLastSubmitState().equals(dto.getSubmitState())) {
                String action = dto.getSubmitState() > 0 ? "已确认采购" : "已取消采购";
                String content = String.format("订单【%s】客户 %s，请及时跟进。", dto.getEOrderId(), action);
                loadAndSendMsg(receiverId, "预购订单状态变更", content, dto.getEOrderId());
                currentStub.setConsultResultNotified(true);
                currentStub.setLastSubmitState(dto.getSubmitState());
            }

            // 3. 处理客户确认收货
            if (dto.getStatus() != null && dto.getStatus() == 1 && !currentStub.getConfirmReceiptNotified()) {
                String content = String.format("订单【%s】客户已确认收货。", dto.getEOrderId());
                loadAndSendMsg(receiverId, "订单已确认收货提醒", content, dto.getEOrderId());
                currentStub.setConfirmReceiptNotified(true);
                currentStub.setLastStatus(dto.getStatus());
            }

            currentStub.setOrderPrice(dto.getOrderPrice());
            currentStub.setPurchaser(dto.getPurchaser());
            currentStub.setName(dto.getName());

            stubsToSave.add(currentStub);
        }

        // 🌟 批量保存存根表，提升数据库 I/O 性能
        stubRepo.saveOrUpdateBatch(stubsToSave);
        logger.info("✅ 成功处理并发送了 {} 笔订单的业务状态变更通知", dtos.size());
    }

    private void scanLogistics(String maxOrderId, Long receiverId) {
        List<EdongfangLogisticDTO> dtos = logisticsRepo.listUnnotified(maxOrderId);
        if (dtos.isEmpty()) {
            return;
        }

        List<EdongfangOrderStub> stubsToSave = new ArrayList<>();

        for (EdongfangLogisticDTO dto : dtos) {
            // 这里同理：独立发送每笔物流的签收提醒，精准关联 E 采订单号
            String content = String.format("订单【%s】已有平台签收记录。物流公司：【%s】，运单编号：【%s】", dto.getEOrderId(),
                dto.getExpressCompany(), dto.getExpressNo());
            loadAndSendMsg(receiverId, "平台物流签收提醒", content, dto.getEOrderId());

            // 保证独立干净的 Stub
            EdongfangOrderStub stub = new EdongfangOrderStub();
            stub.setId(dto.getStubId()); // 这里应该是你 SQL 里联查出来的 stub.id
            // 由于是更新存根表的物流字段，必须保证能对得上存根
            if (stub.getId() != null) {
                stub.setLastReceiveTime(dto.getReceiveTime());
                stubsToSave.add(stub);
            }
        }

        if (!stubsToSave.isEmpty()) {
            stubRepo.updateBatchById(stubsToSave);
        }
        logger.info("✅ 成功处理并发送了 {} 笔订单的物流签收通知", dtos.size());
    }

    /**
     * 封装独立的单条发送逻辑
     */
    private void loadAndSendMsg(Long receiverId, String title, String content, String bizRefId) {
        SysMessage msg = new SysMessage();
        msg.setReceiverId(receiverId);
        msg.setMsgType(SYSTEM_NOTIFICATION.typeCode());
        msg.setBizRefId(bizRefId); // 🌟 这里传入具体的订单编号，前端点击该消息可直接跳到订单详情页！
        msg.setTitle(title);
        msg.setContent(content);

        messageProvider.saveAndPushAsync(msg);
    }
}