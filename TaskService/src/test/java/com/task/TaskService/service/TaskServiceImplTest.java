package com.task.TaskService.service;

import com.task.TaskService.dto.*;
import com.task.TaskService.entity.Task;
import com.task.TaskService.exception.TaskNotFoundException;
import com.task.TaskService.repository.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest
{
    @Mock
    TaskEntityConverter taskEntityConverter;

    @Mock
    TaskRepository taskRepository;

    @Mock
    TaskEventPublisher taskEventPublisher;

    @InjectMocks
    TaskServiceImpl taskService;

    @Test
    void testCreateTaskSuccess() {
        TaskCreationRequest taskCreationRequest = new TaskCreationRequest();
        taskCreationRequest.setTitle("Title");
        taskCreationRequest.setDescription("Description");
        taskCreationRequest.setPriority("High");
        taskCreationRequest.setDueDate(LocalDate.now());

        TaskDto taskDto = new TaskDto();
        Task taskEntity = new Task();

        when(taskRepository.findTasksByUserId(1L)).thenReturn(List.of(taskEntity));
        when(taskRepository.save(any())).thenReturn(taskEntity);
        when(taskEntityConverter.convertEntityToDto(any())).thenReturn(taskDto);
        when(taskEntityConverter.convertDtoToEntity(any())).thenReturn(taskEntity);

        TaskServiceResponse response = taskService.createTask(1L, taskCreationRequest);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.CREATED, response.getHttpStatus());
        Assertions.assertTrue(response.getHttpMessage().contains("Task created successfully"));
        //Assertions.assertNotNull(response.getLastSynced());
        //Assertions.assertEquals("COMPLETED", response.getStatus(), "Status not correct");
        verify( taskEventPublisher, times(1)).publishTaskCreated(any());
    }

    @Test
    void testCreateTaskFailure() {
        when(taskRepository.findTasksByUserId(1L)).thenReturn(null);
        TaskServiceResponse taskServiceResponse = taskService.createTask(1L, new TaskCreationRequest());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, taskServiceResponse.getHttpStatus());
        Assertions.assertTrue(taskServiceResponse.getHttpMessage().contains("User with ID 1 does not exist"));
    }

    @Test
    void testDeleteTaskSuccess() {
        Long userId = 1L;
        Long taskId = 2L;

        Task taskEntity = new Task();
        taskEntity.setUserId(userId);
        taskEntity.setTaskId(taskId);

        when(taskRepository.findById(any())).thenReturn(Optional.of(taskEntity));
        TaskServiceResponse taskServiceResponse = taskService.deleteTask(userId, taskId);
        verify(taskRepository).delete(taskEntity);
        verify(taskEventPublisher, times(1)).publishTaskDeleted(taskId,userId);
        Assertions.assertEquals(HttpStatus.OK, taskServiceResponse.getHttpStatus());
        Assertions.assertTrue(taskServiceResponse.getHttpMessage().contains("Task deleted successfully"));
    }

    @Test
    void testDeleteTaskFailure() {
        Long userId = 1L;
        Long taskId = 2L;

        Task taskEntity = new Task();
        taskEntity.setUserId(99L);
        taskEntity.setTaskId(taskId);

        TaskServiceResponse taskServiceResponse1 = taskService.deleteTask(userId,taskId);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, taskServiceResponse1.getHttpStatus());
        Assertions.assertTrue(taskServiceResponse1.getHttpMessage().contains("Task Not found"));
    }

    @Test
    void testTaskUpdateSuccess() {
        Long userId = 1L;
        Task task = new Task();
        task.setUserId(userId);
        task.setTaskId(1L);
        task.setTitle("Title");
        task.setDescription("Description");
        task.setPriority("High");
        task.setStatus(Task.Status.valueOf("PENDING"));
        task.setDueDate(LocalDate.now());

        TaskServiceRequest taskServiceRequest = new TaskServiceRequest();
        taskServiceRequest.setTaskId(1L);
        taskServiceRequest.setTitle("Updated Title");
        taskServiceRequest.setDescription("Updated Description");
        taskServiceRequest.setPriority("Low");
        taskServiceRequest.setStatus("IN_PROGRESS");
        taskServiceRequest.setDueDate(LocalDate.of(2026, 3, 14));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);  //Why

        TaskServiceResponse taskServiceResponse = taskService.updateTask(userId,taskServiceRequest);

        Assertions.assertEquals(HttpStatus.OK, taskServiceResponse.getHttpStatus());
        Assertions.assertEquals("Task updated successfully", taskServiceResponse.getHttpMessage());
        verify(taskEventPublisher).publishTaskUpdated(any());
    }

    @Test
    void testTaskUpdateFailure() {
        when( taskRepository.findById(1L) ).thenThrow(new TaskNotFoundException("Task Not found"));
        TaskServiceResponse taskServiceResponse = taskService.getTaskByTaskId(1L);
        Assertions.assertEquals("Task Not found", taskServiceResponse.getHttpMessage());
        Assertions.assertTrue(taskServiceResponse.getHttpMessage().contains("Task Not found"));
    }
}
