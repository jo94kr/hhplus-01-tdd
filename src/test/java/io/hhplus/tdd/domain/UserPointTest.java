package io.hhplus.tdd.domain;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("UserPoint 도메인 테스트")
class UserPointTest {

    @Test
    @DisplayName("음수 포인트 충전시 예외 발생")
    void chargeNegativePoint() {
        // given
        UserPoint userPoint = UserPoint.empty(1L);
        long point = -100;

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> userPoint.charge(point);

        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(throwingCallable);
    }

    @Test
    @DisplayName("0 포인트 충전시 예외 발생")
    void chargeZeroPoint() {
        // given
        UserPoint userPoint = UserPoint.empty(1L);
        long point = 0;

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> userPoint.charge(point);

        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(throwingCallable);
    }

    @Test
    @DisplayName("포인트 정상 충전")
    void charge() {
        // given
        UserPoint userPoint = UserPoint.empty(1L);
        long point = 100L;

        // when
        UserPoint chargedPoint = userPoint.charge(point);

        //then
        assertThat(chargedPoint.point()).isEqualTo(100L);
    }

    @Test
    @DisplayName("음수 포인트 사용시 예외 발생")
    void useNegativePoint() {
        // given
        UserPoint userPoint = UserPoint.empty(1L);
        long point = -100;

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> userPoint.use(point);

        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(throwingCallable);
    }

    @Test
    @DisplayName("0 포인트 사용시 예외 발생")
    void useZeroPoint() {
        // given
        UserPoint userPoint = UserPoint.empty(1L);
        long point = 0L;

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> userPoint.use(point);

        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(throwingCallable);
    }

    @Test
    @DisplayName("보유 포인트보다 많은 포인트 사용시 예외 발생")
    void useMoreThanCurrentPoint() {
        // given
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());
        long point = 500L;

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> userPoint.use(point);

        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(throwingCallable);
    }

    @Test
    @DisplayName("포인트 정상 사용")
    void use() {
        // given
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());
        long point = 100L;

        // when
        UserPoint chargedPoint = userPoint.use(point);

        //then
        assertThat(chargedPoint.point()).isZero();
    }
}
