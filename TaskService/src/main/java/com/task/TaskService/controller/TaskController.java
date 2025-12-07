package com.task.TaskService.controller;

import com.task.TaskService.dto.TaskCreationRequest;
import com.task.TaskService.dto.TaskServiceRequest;
import com.task.TaskService.dto.TaskServiceResponse;
import com.task.TaskService.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController
{
    @Autowired
    private TaskService taskService;

    @PostMapping("/createTask")
    public ResponseEntity<TaskServiceResponse> createTask(@RequestHeader("X-User-Id") Long userId, @RequestBody TaskCreationRequest taskCreationRequest)
    {
        TaskServiceResponse createdTask = taskService.createTask(userId, taskCreationRequest);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<TaskServiceResponse> deleteTask(@RequestHeader("X-User-Id") Long userId, @PathVariable Long taskId)
    {
        TaskServiceResponse taskServiceResponse = taskService.deleteTask(userId, taskId);
        return new ResponseEntity<>(taskServiceResponse.getHttpStatus());
    }

    @GetMapping()
    public ResponseEntity<List<TaskServiceResponse>> getAllListOfTasksByUserId(@RequestHeader("X-User-Id") Long userId)
    {
        List<TaskServiceResponse> listTasks = taskService.getAllTasksByUserId(userId);
        return new ResponseEntity<>(listTasks, HttpStatus.OK);
    }

    @GetMapping("/taskId")
    public ResponseEntity<TaskServiceResponse> getTaskByTaskId(@RequestHeader("X-User-Id") Long userId, @PathVariable Long taskId)
    {
        TaskServiceResponse task = taskService.getTaskByTaskId(taskId);
        return new ResponseEntity<>(task, task.getHttpStatus());
    }

    @PutMapping()
    public ResponseEntity<TaskServiceResponse> updateTask(@RequestHeader("X-User-Id") Long userId, @RequestBody TaskServiceRequest taskServiceRequest)
    {
        TaskServiceResponse updatedTask = taskService.updateTask(userId, taskServiceRequest);
        return new ResponseEntity<>(updatedTask, updatedTask.getHttpStatus());
    }
}

