package io.hhplus.tdd.point;

import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.repository.PointHistoryRepository;
import io.hhplus.tdd.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserPointRepository userPointRepository;

    /**
     * 동시성 보장용 lock
     * fair -> true 사용시 가장 오래 기다린 쓰레드 부터 lock 을 획득
     * 다만, 가장 오래된 쓰레드를 확인하는 과정이 추가되어 성능은 떨어짐
     */
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     * 포인트 조회
     *
     * @param id 유저 ID
     * @return UserPoint
     */
    public UserPoint findPointById(long id) {
        // 포인트 조회
        return userPointRepository.findById(id);
    }

    /**
     * 포인트 내역 조회
     *
     * @param id 유저 ID
     * @return List<PointHistory>
     */
    public List<PointHistory> findAllPointById(long id) {
        // 포인트 내역 조회
        return pointHistoryRepository.findAllPointById(id);
    }

    /**
     * 포인트 충전
     *
     * @param id     유저 ID
     * @param amount 충전 포인트
     * @return UserPoint
     */
    public UserPoint charge(long id, long amount) {
        lock.lock();
        try {
            // 기존 포인트 조회
            UserPoint userPoint = userPointRepository.findById(id);

            // 포인트 충전
            UserPoint result = userPointRepository.save(userPoint.charge(amount));

            // 충전이력 등록
            pointHistoryRepository.save(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

            return result;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 포인트 사용
     *
     * @param id     유저 ID
     * @param amount 사용 포인트
     * @return UserPoint
     */
    public UserPoint use(long id, long amount) {
        lock.lock();
        try {
            // 기존 포인트 조회
            UserPoint userPoint = userPointRepository.findById(id);

            // 포인트 차감
            UserPoint result = userPointRepository.save(userPoint.use(amount));

            // 차감이력 등록
            pointHistoryRepository.save(id, amount, TransactionType.USE, result.updateMillis());

            return result;
        } finally {
            lock.unlock();
        }
    }
}
