package com.task.TaskService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskServiceRequest
{
    private Long taskId;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
}
