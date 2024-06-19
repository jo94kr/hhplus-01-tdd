package io.hhplus.tdd.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserPointTable userPointTable;

    @Override
    public UserPoint findById(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("id is an invalid.");
        }
        UserPoint userPoint = userPointTable.selectById(id);
        if (userPoint == null) {
            throw new NotFoundException("user not found");
        }
        return userPoint;
    }

    @Override
    public UserPoint save(UserPoint userPoint) {
        if (userPoint.id() < 0) {
            throw new IllegalArgumentException("id is an invalid.");
        }
        if (userPoint.point() <= 0) {
            throw new IllegalArgumentException("amount is an invalid.");
        }
        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
    }
}
