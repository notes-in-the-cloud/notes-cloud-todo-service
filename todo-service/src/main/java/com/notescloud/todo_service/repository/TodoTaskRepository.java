package com.notescloud.todo_service.repository;

import com.notescloud.todo_service.domain.TodoTask;
import com.notescloud.todo_service.domain.TodoPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TodoTaskRepository extends JpaRepository<TodoTask, UUID> {
    List<TodoTask> findAllByListIdAndDoneFalse(UUID listId);

    List<TodoTask> findAllByUserIdAndDoneFalseAndListIdNotNull(UUID userId);

    List<TodoTask> findAllById(UUID id);

    List<TodoTask> findAllByUserIdAndDoneFalseAndListIdIsNull(UUID userId);

    void deleteAllByListId(UUID listId);

    boolean existsByUserId(UUID userId);
}