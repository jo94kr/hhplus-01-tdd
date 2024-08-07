package io.hhplus.tdd.point;

import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.exception.NotFoundException;
import io.hhplus.tdd.point.dto.FindPointHistoryApiResDto;
import io.hhplus.tdd.repository.PointHistoryRepository;
import io.hhplus.tdd.repository.UserPointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Test
    @DisplayName("아이디로 포인트 조회")
    void findPointById() {
        // given
        long id = 1L;
        given(userPointRepository.findById(id)).willReturn(UserPoint.empty(id));

        // when
        UserPoint result = pointService.findPointById(id);

        // then
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.point()).isZero();
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 포인트를 조회시 예외 발생")
    void findPointByNonExistentId() {
        // given
        long id = 100L;

        // when
        when(userPointRepository.findById(id)).thenThrow(NotFoundException.class);

        // then
        assertThrows(NotFoundException.class, () -> pointService.findPointById(id));
    }

    @Test
    @DisplayName("유효하지 않은 아이디로 사용자의 포인트 조회시 예외 발생")
    void findPointByInvalidId() {
        // given
        long id = 0L;

        // when
        when(userPointRepository.findById(id)).thenThrow(IllegalArgumentException.class);

        // then
        assertThrows(IllegalArgumentException.class, () -> pointService.findPointById(id));
    }

    @Test
    @DisplayName("음수값으로 포인트 충전시 예외 발생")
    void chargingNegativeValue() {
        // given
        long id = 1L;
        long amount = -100L;
        UserPoint userPoint = UserPoint.empty(id);

        // when
        when(userPointRepository.findById(id)).thenReturn(userPoint);

        // then
        assertThrows(IllegalArgumentException.class, () -> pointService.charge(id, amount));
    }

    @Test
    @DisplayName("정상적인 값으로 포인트 충전")
    void charging() {
        // given
        long id = 1L;
        long amount = 100L;
        UserPoint userPoint = UserPoint.empty(id);

        // when
        when(userPointRepository.findById(id)).thenReturn(userPoint);
        when(userPointRepository.save(userPoint.charge(amount))).thenReturn(new UserPoint(id, amount, anyLong()));
        UserPoint result = pointService.charge(id, amount);

        // then
        verify(pointHistoryRepository).save(eq(id), eq(amount), eq(TransactionType.CHARGE), anyLong());
        assertThat(result.point()).isEqualTo(amount);
    }

    @Test
    @DisplayName("음수값으로 포인트 사용시 예외 발생")
    void useNegativeValue() {
        // given
        long id = 1L;
        long amount = -100L;
        UserPoint userPoint = new UserPoint(id, 1000L, anyLong());

        // when
        when(userPointRepository.findById(id)).thenReturn(userPoint);

        // then
        assertThrows(IllegalArgumentException.class, () -> pointService.use(id, amount));
    }

    @Test
    @DisplayName("보유한 포인트 보다 많은 포인트를 사용할 경우 예외 발생")
    void useMoreThanCurrentPoint() {
        // given
        long id = 1L;
        long amount = 100L;
        UserPoint currentUserPoint = new UserPoint(id, 50L, System.currentTimeMillis());

        // when
        when(userPointRepository.findById(id)).thenReturn(currentUserPoint);

        // then
        assertThrows(IllegalArgumentException.class, () -> pointService.use(id, amount));
    }

    @Test
    @DisplayName("포인트 정상 사용")
    void usePoint() {
        // given
        long id = 1L;
        long amount = 100L;
        UserPoint currentUserPoint = new UserPoint(id, 100L, System.currentTimeMillis());

        // when
        when(userPointRepository.findById(id)).thenReturn(currentUserPoint);
        when(userPointRepository.save(currentUserPoint.use(amount))).thenReturn(new UserPoint(id, 0L, anyLong()));
        UserPoint result = pointService.use(id, amount);

        // then
        verify(pointHistoryRepository).save(eq(id), eq(amount), eq(TransactionType.USE), anyLong());
        assertThat(result.point()).isZero();
    }

    @Test
    @DisplayName("포인트 내역 조회")
    void findAllHistoryById() {
        // given
        long id = 1L;

        // when
        List<PointHistory> pointHistoryList = List.of(new PointHistory(
                        1L,
                        id,
                        100L,
                        TransactionType.CHARGE,
                        System.currentTimeMillis()
                ),
                new PointHistory(
                        2L,
                        id,
                        100L,
                        TransactionType.CHARGE,
                        System.currentTimeMillis()
                ),
                new PointHistory(
                        3L,
                        id,
                        50,
                        TransactionType.USE,
                        System.currentTimeMillis()
                ));
        when(pointHistoryRepository.findAllPointById(id)).thenReturn(pointHistoryList);
        List<PointHistory> result = pointService.findAllPointById(id);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).amount()).isEqualTo(100L);
    }
}
