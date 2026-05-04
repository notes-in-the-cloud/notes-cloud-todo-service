package com.notescloud.todo_service.repository;

import com.notescloud.todo_service.domain.TodoTask;
import com.notescloud.todo_service.domain.TodoPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TodoTaskRepository extends JpaRepository<TodoTask, UUID> {
    List<TodoTask> findAllByListId(UUID listId);

    List<TodoTask> findAllByUserId(UUID userId);

    List<TodoTask> findAllByUserIdAndDone(UUID userId, boolean done);

    List<TodoTask> findAllByUserIdAndPriority(UUID userId, TodoPriority priority);

    List<TodoTask> findAllByUserIdAndDueDateBeforeAndDoneFalse(
        UUID userId,
        LocalDateTime dateTime
    );

    void deleteAllByListId(UUID listId);
}