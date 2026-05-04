package com.notescloud.todo_service.repository;

import com.notescloud.todo_service.domain.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TodoListRepository extends JpaRepository<TodoList, UUID> {
    List<TodoList> findAllByUserId(UUID userId);

    TodoList getTodoListById(UUID id);

    boolean existsByUserId(UUID userId);

    List<TodoList> findAllById(UUID id);
}