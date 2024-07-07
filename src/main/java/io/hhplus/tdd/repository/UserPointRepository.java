package io.hhplus.tdd.repository;

import io.hhplus.tdd.domain.UserPoint;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPointRepository {

    UserPoint findById(long id);

    UserPoint save(UserPoint userPoint);
}
