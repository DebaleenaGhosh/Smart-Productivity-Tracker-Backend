package com.task.TaskService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskServiceResponse
{
    private Long taskId;
    private String title;
    private String description;
    private String priority;
    private String status;
    private LocalDate dueDate;
    private LocalDate lastSynced;
}
