package io.hhplus.tdd.point;

import io.hhplus.tdd.exception.LockException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Service
public class LockService {

    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public <T> T lock(long id, Supplier<T> supplier) {
        ReentrantLock lock = lockMap.computeIfAbsent(id, k -> new ReentrantLock());
        boolean isLock;
        try {
            isLock = lock.tryLock(1, TimeUnit.MINUTES);
            if (!isLock) {
                throw new LockException("lock acquisition failed");
            }
            return supplier.get();
        } catch (InterruptedException e) {
            throw new LockException(e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
