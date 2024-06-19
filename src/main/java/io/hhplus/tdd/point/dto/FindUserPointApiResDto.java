package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.domain.UserPoint;

public record FindUserPointApiResDto(
        Long id,
        Long point,
        Long updateMillis
) {

    public static FindUserPointApiResDto from(UserPoint userPoint) {
        return new FindUserPointApiResDto(
                userPoint.id(),
                userPoint.point(),
                userPoint.updateMillis()
        );
    }
}
