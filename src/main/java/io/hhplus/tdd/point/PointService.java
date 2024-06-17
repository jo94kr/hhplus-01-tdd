package io.hhplus.tdd.point;

import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.point.dto.FindUserPointApiResDto;
import io.hhplus.tdd.repository.PointHistoryRepository;
import io.hhplus.tdd.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserPointRepository userPointRepository;

    public FindUserPointApiResDto findPointById(long id) {
        UserPoint userPoint = userPointRepository.findById(id);

        return FindUserPointApiResDto.of(userPoint);
    }
}
