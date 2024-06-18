package io.hhplus.tdd.point;

import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.exception.NotFoundException;

public class PointValidation {

    public static void idValid(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("id is an invalid.");
        }
    }

    public static void amountValid(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount is an invalid.");
        }
    }

    public static void userPointValid(UserPoint userPoint) {
        if (userPoint == null) {
            throw new NotFoundException("user not found");
        }
    }

    public static void usePointValid(long currentPoint, long pointsToUse) {
        if (currentPoint < pointsToUse) {
            throw new IllegalArgumentException("cannot use more points than you have.");
        }
    }
}
