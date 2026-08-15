package com.task.TaskService.controller;

import com.task.TaskService.dto.request.CreateTaskRequest;
import com.task.TaskService.dto.request.UpdateTaskRequest;
import com.task.TaskService.dto.response.TaskServiceResponse;
import com.task.TaskService.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController
{
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping()
    public ResponseEntity<TaskServiceResponse> createTask(@RequestHeader("X-User-Id") Long userId,
                                                          @Valid @RequestBody CreateTaskRequest createTaskRequest)
    {
        TaskServiceResponse createdTask = taskService.createTask(userId, createTaskRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<TaskServiceResponse> deleteTask(@RequestHeader("X-User-Id") Long userId, @PathVariable Long taskId)
    {
        taskService.deleteTask(userId, taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<TaskServiceResponse>> getAllListOfTasksByUserId(@RequestHeader("X-User-Id") Long userId)
    {
        List<TaskServiceResponse> listTasks = taskService.getAllTasksByUserId(userId);
        return ResponseEntity.ok(listTasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskServiceResponse> getTaskByTaskId(@RequestHeader("X-User-Id") Long userId, @PathVariable Long taskId)
    {
        TaskServiceResponse task = taskService.getTaskByTaskId(userId, taskId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskServiceResponse> updateTask(@RequestHeader("X-User-Id") Long userId, @RequestBody UpdateTaskRequest updateTaskRequest)
    {
        TaskServiceResponse updatedTask = taskService.updateTask(userId, updateTaskRequest);
        return ResponseEntity.ok(updatedTask);
    }
}

