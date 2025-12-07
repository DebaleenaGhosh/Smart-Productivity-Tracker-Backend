package com.task.TaskService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TaskServiceResponse
{
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
    private LocalDate lastSynced;
    private HttpStatus httpStatus;
    private String httpMessage;

    public TaskServiceResponse(String title, String description, String status, LocalDate dueDate,
                               LocalDate lastSynced, HttpStatus httpStatus, String httpMessage) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.lastSynced = lastSynced;
        this.httpStatus = httpStatus;
        this.httpMessage = httpMessage;
    }

    public TaskServiceResponse setTitle(String title) {
        this.title = title;
        return this;
    }

    public TaskServiceResponse setDescription(String description) {
        this.description = description;
        return this;
    }

    public TaskServiceResponse setStatus(String status) {
        this.status = status;
        return this;
    }

    public TaskServiceResponse setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public TaskServiceResponse setLastSynced(LocalDate lastSynced) {
        this.lastSynced = lastSynced;
        return this;
    }

    public TaskServiceResponse setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }
}
