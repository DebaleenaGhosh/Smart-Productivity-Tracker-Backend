package com.task.TaskService.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto
{
    private Long taskId;
    private Long userId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String status;
    private LocalDate lastSynced;

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

    public TaskDto setStatus(String status) {
        this.status = status;
        return this;
    }

}

