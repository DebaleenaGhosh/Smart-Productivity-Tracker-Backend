package com.task.TaskService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreationRequest
{
    private String title;
    private String description;
    private LocalDate dueDate;
}