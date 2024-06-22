package io.hhplus.tdd.repository;

import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointHistoryRepository {

    List<PointHistory> findAllPointById(long id);

    PointHistory save(long userId, long amount, TransactionType type, long updateMillis);
}
