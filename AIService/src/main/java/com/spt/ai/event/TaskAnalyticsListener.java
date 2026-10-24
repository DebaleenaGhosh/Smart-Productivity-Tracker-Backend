package com.spt.ai.event;

import com.spt.ai.entity.TaskAnalytics;
import com.spt.ai.repository.SuggestionRepository;
import com.spt.ai.repository.TaskAnalyticsRepository;
import com.spt.ai.service.SuggestionService;
import com.spt.events.TaskEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskAnalyticsListener {

    private final TaskAnalyticsRepository taskRepository;
    private final SuggestionRepository suggestionRepository;
    private final SuggestionService suggestionService;

    @KafkaListener(topics = "task.events", groupId = "ai-service")
    @Transactional
    public void onTaskEvent(TaskEvent event) {
        if (event == null || event.getMetadata() == null || event.getMetadata().getEventType() == null) {
            log.warn("Ignoring malformed task event");
            return;
        }

        String eventType = event.getMetadata().getEventType();
        switch (eventType) {
            case "TASK_CREATED", "TASK_UPDATED" -> upsert(event);
            case "TASK_DELETED" -> deleteTask(event.getTaskId());
            case "ALL_TASKS_DELETED" -> deleteUserData(event.getUserId());
            default -> log.debug("Ignoring unsupported task event type: {}", eventType);
        }
    }

    private void upsert(TaskEvent event) {
        if (event.getTaskId() == null || event.getUserId() == null) {
            log.warn("Ignoring task snapshot without taskId/userId. eventId={}", event.getMetadata().getEventId());
            return;
        }

        TaskAnalytics analytics = taskRepository.findById(event.getTaskId()).orElseGet(TaskAnalytics::new);
        if (event.getMetadata().getEventId().equals(analytics.getLastEventId())) {
            return;
        }
        boolean becameCompleted = !"COMPLETED".equalsIgnoreCase(analytics.getStatus())
                && "COMPLETED".equalsIgnoreCase(event.getStatus());

        analytics.setTaskId(event.getTaskId());
        analytics.setUserId(event.getUserId());
        analytics.setTitle(event.getTitle());
        analytics.setCategory(defaultString(event.getCategory(), "GENERAL"));
        analytics.setPriority(event.getPriority());
        analytics.setStatus(event.getStatus());
        analytics.setEstimatedMinutes(defaultInteger(event.getEstimatedMinutes(), 0));
        analytics.setActualMinutes(defaultInteger(event.getActualMinutes(), 0));
        analytics.setDueDate(event.getDueDate());
        analytics.setCreatedAt(event.getCreatedAt());
        analytics.setStartedAt(event.getStartedAt());
        analytics.setCompletedAt(event.getCompletedAt());
        analytics.setUpdatedAt(event.getUpdatedAt() == null ? LocalDateTime.now() : event.getUpdatedAt());
        analytics.setLastEventId(event.getMetadata().getEventId());
        taskRepository.save(analytics);

        if (becameCompleted) {
            suggestionService.createCompletionCelebration(event.getUserId(), event.getTitle());
        }
        log.info("Analytics updated. eventType={}, taskId={}, userId={}",
                event.getMetadata().getEventType(), event.getTaskId(), event.getUserId());
    }

    private void deleteTask(Long taskId) {
        if (taskId != null && taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
        }
    }

    private void deleteUserData(Long userId) {
        if (userId != null) {
            taskRepository.deleteByUserId(userId);
            suggestionRepository.deleteByUserId(userId);
        }
    }

    private String defaultString(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private Integer defaultInteger(Integer value, Integer fallback) {
        return value == null ? fallback : value;
    }
}
