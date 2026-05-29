package thriving.softwood.customer.first.biz.api.quotation;

import java.util.concurrent.TimeUnit;

/**
 * 报价并发控制锁接口 (SPI)
 * <p>
 * 💡 导师备注：定义此接口是为了解耦具体的锁实现。 初期使用本地 ReentrantLock，未来迁移至集群时，只需增加 Redis 分布式锁实现类即可。
 * </p>
 */
public interface QuotationLockApi {
    /**
     * 尝试获取商品级别的锁
     * 
     * @param productCode 商品编码 (锁的粒度)
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    boolean tryLock(String productCode, long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * 释放商品级别的锁
     */
    void unlock(String productCode);
}