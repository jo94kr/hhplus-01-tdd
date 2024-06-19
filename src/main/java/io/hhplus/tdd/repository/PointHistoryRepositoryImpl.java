package io.hhplus.tdd.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryTable pointHistoryTable;

    @Override
    public List<PointHistory> findAllPointById(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("id is an invalid.");
        }
        return pointHistoryTable.selectAllByUserId(id);
    }

    @Override
    public PointHistory save(long userId, long amount, TransactionType type, long updateMillis) {
        if (userId < 0) {
            throw new IllegalArgumentException("id is an invalid.");
        }
        return pointHistoryTable.insert(userId, amount, type, updateMillis);
    }
}
