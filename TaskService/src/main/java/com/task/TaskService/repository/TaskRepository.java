package com.task.TaskService.repository;

import com.task.TaskService.entity.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>
{
    @Query(name="Task.findTasksByUserId")
    List<Task> findTasksByUserId(Long userId);
    @Modifying
    @Transactional
    @Query(name="Task.deleteAllTasksByUserId")
    void deleteAllTasksByUserId(Long userId);
}