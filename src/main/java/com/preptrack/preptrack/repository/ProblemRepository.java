package com.preptrack.preptrack.repository;

import com.preptrack.preptrack.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    
    List<Problem> findByDifficulty(String difficulty);

    List<Problem> findByStatus(String status);

    List<Problem> findByConfidence(String confidence);

    List<Problem> findByRevisionNeeded(boolean revisionNeeded);

    List<Problem> findBySolvedDateBefore(LocalDate date);
    List<Problem> findByTitleContainingIgnoreCase(String title);
    List<Problem> findByConfidencePercentageLessThan(Integer confidence);

List<Problem> findByConfidencePercentageGreaterThanEqual(Integer confidence);

    
    long countByStatus(String status);

    long countByDifficulty(String difficulty);
}