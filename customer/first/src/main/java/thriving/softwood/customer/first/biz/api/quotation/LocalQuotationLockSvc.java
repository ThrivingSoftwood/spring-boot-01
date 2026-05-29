package thriving.softwood.customer.first.biz.api.quotation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;

/**
 * 基于 JVM 的本地并发锁实现
 */
@Service
public class LocalQuotationLockSvc implements QuotationLockApi {

    // 🌟 细粒度分段锁：避免全局锁导致所有商品报价排队，仅针对同一件商品发生争抢
    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Override
    public boolean tryLock(String productCode, long timeout, TimeUnit unit) throws InterruptedException {
        // computeIfAbsent 保证并发下同一商品拿到的是同一个锁对象
        ReentrantLock lock = lockMap.computeIfAbsent(productCode, k -> new ReentrantLock());
        return lock.tryLock(timeout, unit);
    }

    @Override
    public void unlock(String productCode) {
        ReentrantLock lock = lockMap.get(productCode);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
            // 💡 注意：这里不主动 remove，防止并发时别的线程刚拿到引用就被删了
            // 极端情况下若商品极多，可引入定时清理无竞争的锁对象，但一般商品量级下常驻内存影响极小。
        }
    }
}