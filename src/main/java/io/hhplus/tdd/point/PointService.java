package io.hhplus.tdd.point;

import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.point.dto.FindPointHistoryApiResDto;
import io.hhplus.tdd.point.dto.FindUserPointApiResDto;
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

    public FindUserPointApiResDto findPointById(long id) {
        PointValidation.idValid(id);

        // 포인트 조회
        UserPoint userPoint = userPointRepository.findById(id);
        PointValidation.userPointValid(userPoint);

        return FindUserPointApiResDto.of(userPoint);
    }

    public List<FindPointHistoryApiResDto> findAllPointById(long id) {
        PointValidation.idValid(id);

        // 포인트 내역 조회
        List<PointHistory> pointHistoryList = pointHistoryRepository.findAllPointById(id);

        return pointHistoryList.stream()
                .map(FindPointHistoryApiResDto::new)
                .toList();
    }

    public FindUserPointApiResDto charge(long id, long amount) {
        PointValidation.idValid(id);
        PointValidation.amountValid(amount);

        // 포인트 충전
        UserPoint userPoint = userPointRepository.save(id, amount);

        // 충전이력 등록
        pointHistoryRepository.save(id, amount, TransactionType.CHARGE, userPoint.updateMillis());

        return FindUserPointApiResDto.of(userPoint);
    }

    public FindUserPointApiResDto use(long id, long amount) {
        PointValidation.idValid(id);
        PointValidation.amountValid(amount);

        // 기존 포인트 조회
        UserPoint userPoint = userPointRepository.findById(id);
        PointValidation.userPointValid(userPoint);

        // 유효성 검사 - 보유 포인트 보다 많은 포인트 사용 불가
        PointValidation.usePointValid(userPoint.point(), amount);

        // 포인트 차감
        UserPoint result = userPointRepository.save(id, userPoint.point() - amount);

        // 차감이력 등록
        pointHistoryRepository.save(id, amount, TransactionType.USE, result.updateMillis());

        return FindUserPointApiResDto.of(result);
    }
}
