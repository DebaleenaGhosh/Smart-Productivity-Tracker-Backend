package com.task.TaskService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Tasks")
@NamedQueries({
        @NamedQuery(name="Task.findTaskByUserId", query="SELECT t FROM Task t WHERE t.userId= ?1")
})
public class Task
{
    @Id
    @GeneratedValue
    private Long taskId;
    private Long userId;
    private String title;
    private String description;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDate lastSynced;
    /// priority

    public enum Status
    {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }
//    @ManyToOne
//    private User user;
}
