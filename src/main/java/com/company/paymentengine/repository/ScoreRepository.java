package com.company.paymentengine.repository;

import com.company.paymentengine.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScoreRepository extends JpaRepository<Score, UUID> {
    Optional<Score> findByUserAIdAndUserBId(UUID userAId, UUID userBId);
}
