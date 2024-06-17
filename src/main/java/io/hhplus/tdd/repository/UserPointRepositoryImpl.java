package io.hhplus.tdd.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.domain.UserPoint;

public class UserPointRepositoryImpl implements UserPointRepository {

    UserPointTable userPointTable = new UserPointTable();

    @Override
    public UserPoint findById(Long id) {
        return userPointTable.selectById(id);
    }
}
