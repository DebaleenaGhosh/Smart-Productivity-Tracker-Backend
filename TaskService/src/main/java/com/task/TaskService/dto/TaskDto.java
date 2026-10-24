package com.task.TaskService.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto
{
    private Long taskId;
    private Long userId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String priority;
    private String status;
    private LocalDate lastSynced;
    private String category;
    private Integer estimatedMinutes;
    private Integer actualMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;

    public Long getTaskId()
    {
        return taskId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public LocalDate getDueDate()
    {
        return dueDate;
    }

    public String getPriority()
    {
        return priority;
    }

    public String getStatus()
    {
        return status;
    }

    public LocalDate getLastSynced()
    {
        return lastSynced;
    }

    public String getCategory()
    {
        return category;
    }

    public Integer getEstimatedMinutes()
    {
        return estimatedMinutes;
    }

    public Integer getActualMinutes()
    {
        return actualMinutes;
    }

    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public LocalDateTime getStartedAt()
    {
        return startedAt;
    }

    public LocalDateTime getCompletedAt()
    {
        return completedAt;
    }

    public LocalDateTime getUpdatedAt()
    {
        return updatedAt;
    }

    public TaskDto setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public TaskDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public TaskDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public TaskDto setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public TaskDto setPriority(String priority) {
        this.priority = priority;
        return this;
    }

    public TaskDto setStatus(String status) {
        this.status = status;
        return this;
    }

    public TaskDto setTaskId( Long taskId )
    {
        this.taskId = taskId;
        return this;
    }

    public TaskDto setLastSynced( LocalDate lastSynced )
    {
        this.lastSynced = lastSynced;
        return this;
    }

    public TaskDto setCategory( String category )
    {
        this.category = category;
        return this;
    }

    public TaskDto setEstimatedMinutes( Integer estimatedMinutes )
    {
        this.estimatedMinutes = estimatedMinutes;
        return this;
    }

    public TaskDto setActualMinutes( Integer actualMinutes )
    {
        this.actualMinutes = actualMinutes;
        return this;
    }

    public TaskDto setCreatedAt( LocalDateTime createdAt )
    {
        this.createdAt = createdAt;
        return this;
    }

    public TaskDto setStartedAt( LocalDateTime startedAt )
    {
        this.startedAt = startedAt;
        return this;
    }

    public TaskDto setCompletedAt( LocalDateTime completedAt )
    {
        this.completedAt = completedAt;
        return this;
    }

    public TaskDto setUpdatedAt( LocalDateTime updatedAt )
    {
        this.updatedAt = updatedAt;
        return this;
    }
}

