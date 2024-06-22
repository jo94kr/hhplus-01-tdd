package io.hhplus.tdd.domain;

import java.util.Objects;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    public UserPoint {
        if (this.id() < 0) {
            throw new IllegalArgumentException("id is an invalid.");
        }
    }

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    /**
     * 포인트 충전
     *
     * @param point 충전 포인트
     * @return UserPoint
     */
    public UserPoint charge(long point) {
        if (point <= 0) {
            throw new IllegalArgumentException("amount is an invalid.");
        }
        return new UserPoint(this.id, this.point + point, System.currentTimeMillis());
    }

    /**
     * 포인트 사용
     *
     * @param point 사용 포인트
     * @return UserPoint
     */
    public UserPoint use(long point) {
        if (point <= 0) {
            throw new IllegalArgumentException("amount is an invalid.");
        }
        long currentPoint = this.point;
        if (currentPoint < point) {
            throw new IllegalArgumentException("cannot use more points than you have.");
        }
        return new UserPoint(this.id, currentPoint - point, System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPoint userPoint = (UserPoint) o;
        return id == userPoint.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
