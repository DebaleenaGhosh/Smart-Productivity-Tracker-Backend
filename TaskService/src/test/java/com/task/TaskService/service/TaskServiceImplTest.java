package com.task.TaskService.service;

import com.task.TaskService.dto.TaskCreationRequest;
import com.task.TaskService.dto.TaskDto;
import com.task.TaskService.dto.TaskEntityConverter;
import com.task.TaskService.dto.TaskServiceResponse;
import com.task.TaskService.entity.Task;
import com.task.TaskService.repository.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


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
    void testCreateTask() {

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
        Assertions.assertEquals("Task created successfully", response.getHttpMessage());
        //Assertions.assertNotNull(response.getLastSynced());
        //Assertions.assertEquals("COMPLETED", response.getStatus(), "Status not correct");

        verify( taskEventPublisher, times(1)).publishTaskCreated(any());

    }
}
