package io.hhplus.tdd.point;

import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.point.dto.FindUserPointApiResDto;
import io.hhplus.tdd.repository.UserPointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private UserPointRepository userPointRepository;

    @Test
    @DisplayName("아이디로 포인트 조회")
    void findPointById() {
        // given
        long id = 1L;
        given(userPointRepository.findById(id)).willReturn(UserPoint.empty(id));

        // when
        FindUserPointApiResDto result = pointService.findPointById(id);

        // then
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.point()).isZero();
    }
}
