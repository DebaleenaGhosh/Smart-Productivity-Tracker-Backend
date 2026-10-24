package com.spt.ai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_analytics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAnalytics {

    @Id
    private Long taskId;

    @Column(nullable = false)
    private Long userId;

    private String title;
    private String category;
    private String priority;
    private String status;
    private Integer estimatedMinutes;
    private Integer actualMinutes;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;
    private String lastEventId;
}
