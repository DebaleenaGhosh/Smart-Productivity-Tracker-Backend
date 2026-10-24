package com.task.TaskService.entity;

import com.task.TaskService.event.TaskEventPublisher;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Tasks")
public class Task
{
    @Id
    @GeneratedValue
    private Long taskId;
    private Long userId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String priority;
    private String category;
    private Integer estimatedMinutes;
    private Integer actualMinutes;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDate lastSynced;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate()
    {
        LocalDateTime now = LocalDateTime.now();
        createdAt = createdAt == null ? now : createdAt;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate()
    {
        updatedAt = LocalDateTime.now();
    }

    public enum Status
    {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }

//    public Long getTaskId()
//    {
//        return taskId;
//    }
//
//    public Task setTaskId( Long taskId )
//    {
//        this.taskId = taskId;
//        return this;
//    }
//
//    public Long getUserId()
//    {
//        return userId;
//    }
//
//    public Task setUserId( Long userId )
//    {
//        this.userId = userId;
//        return this;
//    }
//
//    public String getTitle()
//    {
//        return title;
//    }
//
//    public Task setTitle( String title )
//    {
//        this.title = title;
//        return this;
//    }
//
//    public String getDescription()
//    {
//        return description;
//    }
//
//    public Task setDescription( String description )
//    {
//        this.description = description;
//        return this;
//    }
//
//    public LocalDate getDueDate()
//    {
//        return dueDate;
//    }
//
//    public Task setDueDate( LocalDate dueDate )
//    {
//        this.dueDate = dueDate;
//        return this;
//    }
//
//    public String getPriority()
//    {
//        return priority;
//    }
//
//    public Task setPriority( String priority )
//    {
//        this.priority = priority;
//        return this;
//    }
//
//    public String getCategory()
//    {
//        return category;
//    }
//
//    public Task setCategory( String category )
//    {
//        this.category = category;
//        return this;
//    }
//
//    public Integer getEstimatedMinutes()
//    {
//        return estimatedMinutes;
//    }
//
//    public Task setEstimatedMinutes( Integer estimatedMinutes )
//    {
//        this.estimatedMinutes = estimatedMinutes;
//        return this;
//    }
//
//    public Integer getActualMinutes()
//    {
//        return actualMinutes;
//    }
//
//    public Task setActualMinutes( Integer actualMinutes )
//    {
//        this.actualMinutes = actualMinutes;
//        return this;
//    }
//
//    public Status getStatus()
//    {
//        return status;
//    }
//
//    public Task setStatus( Status status )
//    {
//        this.status = status;
//        return this;
//    }
//
//    public LocalDate getLastSynced()
//    {
//        return lastSynced;
//    }
//
//    public Task setLastSynced( LocalDate lastSynced )
//    {
//        this.lastSynced = lastSynced;
//        return this;
//    }
//
//    public LocalDateTime getCreatedAt()
//    {
//        return createdAt;
//    }
//
//    public Task setCreatedAt( LocalDateTime createdAt )
//    {
//        this.createdAt = createdAt;
//        return this;
//    }
//
//    public LocalDateTime getStartedAt()
//    {
//        return startedAt;
//    }
//
//    public Task setStartedAt( LocalDateTime startedAt )
//    {
//        this.startedAt = startedAt;
//        return this;
//    }
//
//    public LocalDateTime getCompletedAt()
//    {
//        return completedAt;
//    }
//
//    public Task setCompletedAt( LocalDateTime completedAt )
//    {
//        this.completedAt = completedAt;
//        return this;
//    }
//
//    public LocalDateTime getUpdatedAt()
//    {
//        return updatedAt;
//    }
//
//    public Task setUpdatedAt( LocalDateTime updatedAt )
//    {
//        this.updatedAt = updatedAt;
//        return this;
//    }
}
