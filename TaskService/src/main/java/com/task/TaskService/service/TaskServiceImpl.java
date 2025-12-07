package com.task.TaskService.service;

import com.task.TaskService.dto.*;
import com.task.TaskService.entity.Task;
import com.task.TaskService.exception.TaskNotFoundException;
import com.task.TaskService.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService
{
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskEntityConverter converter;
    @Autowired
    private TaskEventPublisher publisher;

    @Override
    public TaskServiceResponse createTask(Long userId, TaskCreationRequest taskCreationRequest)
    {
        TaskServiceResponse taskServiceResponse = new TaskServiceResponse();
        try{
            if (null == taskRepository.findTasksByUserId(userId))
            {
                throw new IllegalArgumentException("User with ID " + userId + " does not exist");
            }
            TaskDto taskDto = new TaskDto();
            taskDto.setUserId(userId)
                .setTitle(taskCreationRequest.getTitle())
                .setDescription(taskCreationRequest.getDescription())
                .setDueDate(taskCreationRequest.getDueDate())
                .setStatus("PENDING")
                .setLastSynced(LocalDate.now());

            Task savedTask = taskRepository.save(converter.convertDtoToEntity(taskDto));
            /*Publishing the task event after successful save*/
            publisher.publishTaskCreated(converter.convertEntityToDto(savedTask));

            taskServiceResponse.setTitle(taskDto.getTitle())
                            .setDescription(taskDto.getDescription())
                            .setStatus(taskDto.getStatus())
                            .setDueDate(taskDto.getDueDate())
                            .setLastSynced(taskDto.getLastSynced())
                            .setHttpStatus(HttpStatus.CREATED)
                            .setHttpMessage("Task created successfully");
        }
        catch (IllegalArgumentException exception)
        {
            taskServiceResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
            taskServiceResponse.setHttpMessage(exception.getMessage());
        }
        return taskServiceResponse;
    }

    @Override
    public TaskServiceResponse deleteTask(Long userId, Long taskId)
    {
        TaskServiceResponse taskServiceResponse = new TaskServiceResponse();
        try{
            Task task = taskRepository.findById(taskId)
                    .orElseThrow( () -> new TaskNotFoundException("Task Not found"));
            if (!userId.equals(task.getUserId()))
            {
                throw new RuntimeException("Unauthorized to modify this task");
            }
            taskRepository.delete(task);
            /*Publishing the task event after successful deletion*/
            publisher.publishTaskDeleted(taskId, userId);

            taskServiceResponse.setHttpStatus(HttpStatus.OK);
            taskServiceResponse.setHttpMessage("Task deleted successfully");
        }
        catch (RuntimeException exception)
        {
            taskServiceResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
            taskServiceResponse.setHttpMessage(exception.getMessage());
        }
        return taskServiceResponse;
    }

    @Override
    public TaskServiceResponse updateTask(Long userId, TaskServiceRequest taskServiceRequest)
    {
        TaskServiceResponse taskServiceResponse = new TaskServiceResponse();
        try {
            Task existingTask = taskRepository.findById(taskServiceRequest.getTaskId())
                    .orElseThrow(() -> new TaskNotFoundException("Task Not found"));
            if (!userId.equals(existingTask.getUserId())) {
                throw new RuntimeException("Unauthorized to modify this task");
            }
            existingTask.setTitle(taskServiceRequest.getTitle());
            existingTask.setDescription(taskServiceRequest.getDescription());
            existingTask.setDueDate(taskServiceRequest.getDueDate());
            existingTask.setStatus(Task.Status.valueOf(taskServiceRequest.getStatus()));
            existingTask.setLastSynced(LocalDate.now());

            Task updatedTask = taskRepository.save(existingTask);
            /*Publishing the task event after successful update*/
            publisher.publishTaskUpdated(converter.convertEntityToDto(existingTask));

            taskServiceResponse.setTitle(existingTask.getTitle())
                    .setDescription(existingTask.getDescription())
                    .setStatus(String.valueOf(existingTask.getStatus()))
                    .setDueDate(existingTask.getDueDate())
                    .setLastSynced(existingTask.getLastSynced())
                    .setHttpStatus(HttpStatus.CREATED)
                    .setHttpMessage("Task updated successfully");
        }
        catch (RuntimeException exception)
        {
            taskServiceResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
            taskServiceResponse.setHttpMessage(exception.getMessage());
        }
        return taskServiceResponse;
    }

    @Override
    public List<TaskServiceResponse> getAllTasksByUserId(Long userId)
    {
        TaskServiceResponse taskServiceResponse = new TaskServiceResponse();
        try {
            if (null == taskRepository.findTasksByUserId(userId)) {
                throw new IllegalArgumentException("User with ID " + userId + " does not exist");
            }
        }
        catch (IllegalArgumentException exception)
        {
            taskServiceResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
            taskServiceResponse.setHttpMessage(exception.getMessage());
        }
        List<Task> tasks = taskRepository.findTasksByUserId(userId);
        return tasks.stream()
                .map(task -> new TaskServiceResponse(
                        task.getTitle(),
                        task.getDescription(),
                        String.valueOf(task.getStatus()),
                        task.getDueDate(),
                        task.getLastSynced(),
                        HttpStatus.OK,
                        "Tasks list fetched successfully"))
                .toList();
    }

    @Override
    public TaskServiceResponse getTaskByTaskId(Long taskId)
    {
        TaskServiceResponse taskServiceResponse = new TaskServiceResponse();
        try {
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task Not found"));
            taskServiceResponse.setTitle(task.getTitle())
                    .setDescription(task.getDescription())
                    .setStatus(String.valueOf(task.getStatus()))
                    .setDueDate(task.getDueDate())
                    .setLastSynced(task.getLastSynced())
                    .setHttpStatus(HttpStatus.FOUND)
                    .setHttpMessage("Task fetched successfully");
        }
        catch (IllegalArgumentException exception)
        {
            taskServiceResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
            taskServiceResponse.setHttpMessage(exception.getMessage());
        }
        return taskServiceResponse;
    }

    @Override
    public TaskServiceResponse createDefaultTaskForUser(Long userId)
    {
        TaskServiceResponse taskServiceResponse = new TaskServiceResponse();
        try {
            if (null == taskRepository.findTasksByUserId(userId))
            {
                throw new IllegalArgumentException("User with ID " + userId + " does not exist");
            }
            // Create a default task for the user
            TaskDto defaultTask = new TaskDto();
            defaultTask.setUserId(userId)
                .setTitle("Let's get started by adding a new task")
                .setDescription("This is your first task. Get started!")
                .setDueDate(LocalDate.now())
                .setStatus("PENDING")
                .setLastSynced(LocalDate.now());
            taskRepository.save(converter.convertDtoToEntity(defaultTask));

            taskServiceResponse.setHttpStatus(HttpStatus.CREATED)
                    .setHttpMessage("Default task created successfully");
        }
        catch (IllegalArgumentException exception)
        {
            taskServiceResponse.setHttpStatus(HttpStatus.BAD_REQUEST)
                            .setHttpMessage(exception.getMessage());
        }
        return taskServiceResponse;
    }

    @Override
    public void syncTasksForUser(Long userId)
    {
        try{
            // Verify user exists
            if (null == taskRepository.findTasksByUserId(userId))
            {
                throw new IllegalArgumentException("User with ID " + userId + " does not exist");
            }
            // Fetch all tasks for the user
            List<Task> userTasks = taskRepository.findTasksByUserId(userId);
            for (Task task : userTasks)
            {
                task.setLastSynced(LocalDate.now());
            }
            taskRepository.saveAll(userTasks);
        }
        catch (IllegalArgumentException exception)
        {
            System.out.println(exception.getMessage());
        }
    }
}

