package com.spt.ai.repository;

import com.spt.ai.entity.TaskAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskAnalyticsRepository extends JpaRepository<TaskAnalytics, Long> {

    List<TaskAnalytics> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
