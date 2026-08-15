package com.task.TaskService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
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
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDate lastSynced;

    public enum Status
    {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }
}
