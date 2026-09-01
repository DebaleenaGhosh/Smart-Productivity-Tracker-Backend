package com.task.TaskService.repository;

import com.task.TaskService.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String>
{

}
