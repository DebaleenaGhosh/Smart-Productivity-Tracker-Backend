package com.task.TaskService.mapper;

import com.task.TaskService.dto.TaskDto;
import com.task.TaskService.dto.response.TaskServiceResponse;
import com.task.TaskService.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper
{
    public TaskServiceResponse toResponse ( Task task )
    {
        return TaskServiceResponse.builder()
                        .taskId(task.getTaskId())
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .priority(task.getPriority())
                        .status(task.getStatus().name())
                        .dueDate(task.getDueDate())
                        .lastSynced(task.getLastSynced())
                        .category(task.getCategory())
                        .estimatedMinutes(task.getEstimatedMinutes())
                        .actualMinutes(task.getActualMinutes())
                        .createdAt(task.getCreatedAt())
                        .startedAt(task.getStartedAt())
                        .completedAt(task.getCompletedAt())
                        .updatedAt(task.getUpdatedAt())
                        .build();
    }

    public TaskDto convertEntityToDto(Task task)
    {
        TaskDto taskDto = new TaskDto();
        taskDto.setTaskId(task.getTaskId());
        taskDto.setUserId(task.getUserId());
        taskDto.setDescription(task.getDescription());
        taskDto.setTitle(task.getTitle());
        taskDto.setPriority(task.getPriority());
        taskDto.setStatus(String.valueOf(task.getStatus()));
        taskDto.setDueDate(task.getDueDate());
        taskDto.setLastSynced(task.getLastSynced());
        taskDto.setCategory(task.getCategory());
        taskDto.setEstimatedMinutes(task.getEstimatedMinutes());
        taskDto.setActualMinutes(task.getActualMinutes());
        taskDto.setCreatedAt(task.getCreatedAt());
        taskDto.setStartedAt(task.getStartedAt());
        taskDto.setCompletedAt(task.getCompletedAt());
        taskDto.setUpdatedAt(task.getUpdatedAt());
        return taskDto;
    }

    public Task convertDtoToEntity(TaskDto taskDto)
    {
        Task task = new Task();
        task.setTaskId(taskDto.getTaskId());
        task.setUserId(taskDto.getUserId());
        task.setDescription(taskDto.getDescription());
        task.setTitle(taskDto.getTitle());
        task.setPriority(taskDto.getPriority());
        task.setStatus(Task.Status.valueOf(taskDto.getStatus()));
        task.setDueDate(taskDto.getDueDate());
        task.setLastSynced(taskDto.getLastSynced());
        task.setCategory(taskDto.getCategory());
        task.setEstimatedMinutes(taskDto.getEstimatedMinutes());
        task.setActualMinutes(taskDto.getActualMinutes());
        task.setCreatedAt(taskDto.getCreatedAt());
        task.setStartedAt(taskDto.getStartedAt());
        task.setCompletedAt(taskDto.getCompletedAt());
        task.setUpdatedAt(taskDto.getUpdatedAt());
        return task;
    }
}

