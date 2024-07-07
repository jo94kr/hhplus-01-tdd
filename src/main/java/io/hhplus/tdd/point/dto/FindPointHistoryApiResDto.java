package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;

public record FindPointHistoryApiResDto(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {

    public FindPointHistoryApiResDto(PointHistory pointHistory) {
        this(pointHistory.id(),
                pointHistory.userId(),
                pointHistory.amount(),
                pointHistory.type(),
                pointHistory.updateMillis());
    }
}
