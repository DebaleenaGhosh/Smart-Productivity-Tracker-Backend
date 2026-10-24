package com.spt.ai.repository;

import com.spt.ai.entity.Suggestion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    List<Suggestion> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<Suggestion> findBySuggestionIdAndUserId(Long suggestionId, Long userId);

    void deleteByUserId(Long userId);
}
