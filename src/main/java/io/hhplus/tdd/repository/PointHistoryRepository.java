package io.hhplus.tdd.repository;

import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointHistoryRepository {

    PointHistory save(long userId, long amount, TransactionType type, long updateMillis);

    List<PointHistory> findAllPointById(long id);
}
