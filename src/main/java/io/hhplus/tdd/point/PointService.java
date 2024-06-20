package io.hhplus.tdd.point;

import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.repository.PointHistoryRepository;
import io.hhplus.tdd.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserPointRepository userPointRepository;

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
     * @return List<FindPointHistoryApiResDto>
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
        // 기존 포인트 조회
        UserPoint userPoint = userPointRepository.findById(id);

        // 포인트 충전
        UserPoint result = userPointRepository.save(userPoint.charge(amount));

        // 충전이력 등록
        pointHistoryRepository.save(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

        return result;
    }

    /**
     * 포인트 사용
     *
     * @param id     유저 ID
     * @param amount 사용 포인트
     * @return UserPoint
     */
    public UserPoint use(long id, long amount) {
        // 기존 포인트 조회
        UserPoint userPoint = userPointRepository.findById(id);

        // 포인트 차감
        UserPoint result = userPointRepository.save(userPoint.use(amount));

        // 차감이력 등록
        pointHistoryRepository.save(id, amount, TransactionType.USE, result.updateMillis());

        return result;
    }
}
