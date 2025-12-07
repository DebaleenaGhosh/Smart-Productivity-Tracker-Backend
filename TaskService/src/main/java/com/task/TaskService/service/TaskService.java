package com.task.TaskService.service;

import com.task.TaskService.dto.TaskCreationRequest;
import com.task.TaskService.dto.TaskServiceRequest;
import com.task.TaskService.dto.TaskServiceResponse;
import com.task.TaskService.entity.Task;

import java.util.List;

public interface TaskService
{
    TaskServiceResponse createTask(Long userId, TaskCreationRequest taskCreationRequest);
    TaskServiceResponse deleteTask(Long userId, Long taskId);
    TaskServiceResponse getTaskByTaskId(Long taskId);
    List<TaskServiceResponse> getAllTasksByUserId(Long userId);
    TaskServiceResponse updateTask(Long userId, TaskServiceRequest taskServiceRequest);
    TaskServiceResponse createDefaultTaskForUser(Long userId);
    void syncTasksForUser(Long userId);
}
