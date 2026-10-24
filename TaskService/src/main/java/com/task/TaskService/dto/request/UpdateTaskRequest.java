package com.task.TaskService.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest
{
    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotBlank(message = "Task title is required")
    @Size(max = 150)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotBlank(message = "Priority is required")
    private String priority;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @Size(max = 60, message = "Category cannot exceed 60 characters")
    private String category;

    @Min(value = 1, message = "Estimated time must be at least one minute")
    @Max(value = 10080, message = "Estimated time cannot exceed one week")
    private Integer estimatedMinutes;

    @Min(value = 0, message = "Actual time cannot be negative")
    @Max(value = 10080, message = "Actual time cannot exceed one week")
    private Integer actualMinutes;
}
