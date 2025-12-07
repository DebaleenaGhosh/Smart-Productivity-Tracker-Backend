package com.task.TaskService.dto;

import com.task.TaskService.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskEntityConverter
{
    public TaskDto convertEntityToDto(Task task)
    {
        TaskDto taskDto = new TaskDto();
        taskDto.setTaskId(task.getTaskId());
        taskDto.setUserId(task.getUserId());
        taskDto.setDescription(task.getDescription());
        taskDto.setTitle(task.getTitle());
        taskDto.setStatus(String.valueOf(task.getStatus()));
        taskDto.setDueDate(task.getDueDate());
        taskDto.setLastSynced(task.getLastSynced());
        return taskDto;
    }

    public Task convertDtoToEntity(TaskDto taskDto)
    {
        Task task = new Task();
        task.setTaskId(taskDto.getTaskId());
        task.setUserId(taskDto.getUserId());
        task.setDescription(taskDto.getDescription());
        task.setTitle(taskDto.getTitle());
        task.setStatus(Task.Status.valueOf(taskDto.getStatus()));
        task.setDueDate(taskDto.getDueDate());
        task.setLastSynced(taskDto.getLastSynced());
        return task;
    }
}

