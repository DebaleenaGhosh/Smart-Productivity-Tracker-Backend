package com.task.TaskService.repository;

import com.task.TaskService.entity.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>
{
    @Query(name="SELECT t FROM Task t WHERE t.userId= :userId")
    List<Task> findTasksByUserId(@Param("userId") Long userId);
    @Modifying
    @Transactional
    @Query(name="DELETE FROM Task t WHERE t.userId= :userId")
    void deleteAllTasksByUserId(@Param("userId") Long userId);
}