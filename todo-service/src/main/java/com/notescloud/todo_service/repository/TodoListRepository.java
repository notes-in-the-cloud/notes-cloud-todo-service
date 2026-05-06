package com.notescloud.todo_service.repository;

import com.notescloud.todo_service.domain.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodoListRepository extends JpaRepository<TodoList, UUID> {
    List<TodoList> findAllByUserId(UUID userId);

    Optional<TodoList> findByIdAndUserId(UUID userId, UUID id);

    TodoList getTodoListById(UUID id);
}