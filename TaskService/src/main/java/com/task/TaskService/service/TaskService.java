package com.task.TaskService.service;

import com.task.TaskService.dto.request.CreateTaskRequest;
import com.task.TaskService.dto.request.UpdateTaskRequest;
import com.task.TaskService.dto.response.TaskServiceResponse;

import java.util.List;

public interface TaskService
{
    TaskServiceResponse createTask(Long userId, CreateTaskRequest createTaskRequest);
    void deleteTask(Long userId, Long taskId);
    TaskServiceResponse getTaskByTaskId(Long userId, Long taskId);
    List<TaskServiceResponse> getAllTasksByUserId(Long userId);
    TaskServiceResponse updateTask(Long userId, UpdateTaskRequest updateTaskRequest);
    TaskServiceResponse createDefaultTaskForUser(Long userId);
    void deleteAllTasksByUserId(Long userId);
    void syncTasksForUser(Long userId);
}
