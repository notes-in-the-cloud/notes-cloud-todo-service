package com.notescloud.todo_service.repository;

import com.notescloud.todo_service.domain.TodoTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TodoTaskRepository extends JpaRepository<TodoTask, UUID> {
    List<TodoTask> findAllByListIdAndDoneFalse(UUID listId);

    List<TodoTask> findAllByUserIdAndDoneFalseAndListIdNotNull(UUID userId);

    List<TodoTask> findAllByUserIdAndDoneFalseAndListIdIsNull(UUID userId);

    @Modifying
    @Query("update TodoTask task set task.listId = null where task.listId = :listId")
    void detachAllByListId(UUID listId);
}