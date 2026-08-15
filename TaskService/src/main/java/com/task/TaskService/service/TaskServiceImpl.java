package com.task.TaskService.service;

import com.task.TaskService.dto.*;
import com.task.TaskService.dto.request.CreateTaskRequest;
import com.task.TaskService.dto.request.UpdateTaskRequest;
import com.task.TaskService.dto.response.TaskServiceResponse;
import com.task.TaskService.entity.Task;
import com.task.TaskService.exception.TaskAccessDeniedException;
import com.task.TaskService.exception.TaskNotFoundException;
import com.task.TaskService.mapper.TaskMapper;
import com.task.TaskService.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService
{
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskEventPublisher publisher;

    @Override
    public TaskServiceResponse createTask(Long userId,CreateTaskRequest request) {

        Task task = new Task();

        task.setUserId(userId);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setStatus(Task.Status.PENDING);
        task.setLastSynced(LocalDate.now());

        Task savedTask = taskRepository.save(task);

        publisher.publishTaskCreated(
                taskMapper.convertEntityToDto(savedTask)
        );

        log.info(
                "Task created. taskId={}, userId={}",
                savedTask.getTaskId(),
                userId
        );
        return taskMapper.toResponse(savedTask);
    }

    @Override
    public void deleteTask(Long userId, Long taskId)
    {
        Task task = getTaskForUser(userId, taskId);
        taskRepository.delete(task);
        /*Publishing the task event after successful deletion*/
        publisher.publishTaskDeleted(taskId, userId);

        log.info("Task deleted successfully. taskId = {}, userId = {}",taskId, userId);
    }

    @Override
    public TaskServiceResponse updateTask(Long userId, UpdateTaskRequest updateTaskRequest)
    {
        TaskServiceResponse taskServiceResponse = new TaskServiceResponse();
        Task existingTask = getTaskForUser(userId, updateTaskRequest.getTaskId());

        existingTask.setTitle(updateTaskRequest.getTitle());
        existingTask.setDescription(updateTaskRequest.getDescription());
        existingTask.setPriority(updateTaskRequest.getPriority());
        existingTask.setDueDate(updateTaskRequest.getDueDate());
        existingTask.setStatus(Task.Status.valueOf(updateTaskRequest.getStatus()));
        existingTask.setLastSynced(LocalDate.now());

        Task updatedTask = taskRepository.save(existingTask);
        /*Publishing the task event after successful update*/
        publisher.publishTaskUpdated(taskMapper.convertEntityToDto(updatedTask));

        log.info("Task updated. taskId = {}, userId = {}", updateTaskRequest.getTaskId(), userId);

        return taskMapper.toResponse(updatedTask);
    }

    @Override
    public List<TaskServiceResponse> getAllTasksByUserId(Long userId)
    {
        List<Task> tasks = taskRepository.findTasksByUserId(userId);
        return tasks
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    public TaskServiceResponse getTaskByTaskId(Long userId, Long taskId)
    {
        Task task = getTaskForUser(userId, taskId);
        return taskMapper.toResponse(task);
    }

    @Override
    public TaskServiceResponse createDefaultTaskForUser(Long userId)
    {
        TaskServiceResponse taskServiceResponse = new TaskServiceResponse();
        // Create a default task for the user
        Task defaultTask = new Task();
        defaultTask.setUserId(userId);
        defaultTask.setTitle("Let's get started by adding a new task");
        defaultTask.setDescription("This is your first task. Get started!");
        defaultTask.setPriority("Low");
        defaultTask.setDueDate(LocalDate.now());
        defaultTask.setStatus(Task.Status.PENDING);
        defaultTask.setLastSynced(LocalDate.now());
        taskRepository.save(defaultTask);

        log.info("Default task created. userId={}, taskId={}", userId, defaultTask.getTaskId());

        return taskMapper.toResponse(defaultTask);
    }

    @Override
    @Transactional
    public void deleteAllTasksByUserId(Long userId)
    {
        if (null == taskRepository.findTasksByUserId(userId))
        {
            throw new RuntimeException("No tasks found to delete!");
        }
        taskRepository.deleteAllTasksByUserId(userId);
        /*Publishing the task event after successful deletion*/
        publisher.publishAllTasksDeleted(userId);

        log.info("All tasks deleted. userId = {}", userId);
    }

    private Task getTaskForUser( Long userId, Long taskId )
    {
        Task task = taskRepository.findById(taskId)
                .orElseThrow( () -> new TaskNotFoundException("Task not found :"+taskId));

        if( !userId.equals(task.getUserId()) ){
            throw new TaskAccessDeniedException("You are not authorized to access this task");
        }
        return task;
    }

    @Override
    @Transactional
    public void syncTasksForUser(Long userId)
    {
        // Fetch all tasks for the user
        List<Task> userTasks = taskRepository.findTasksByUserId(userId);
        for (Task task : userTasks)
        {
            task.setLastSynced(LocalDate.now());
        }
        taskRepository.saveAll(userTasks);

        log.info("Tasks synchronized. userId = {}, taskCount = {}", userId, userTasks.size());
    }
}