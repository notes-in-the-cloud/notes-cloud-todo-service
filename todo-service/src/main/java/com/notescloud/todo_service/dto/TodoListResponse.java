package com.notescloud.todo_service.dto;

import com.notescloud.todo_service.domain.TodoList;

import java.time.LocalDateTime;
import java.util.UUID;

public record TodoListResponse(UUID id,
                               UUID userId,
                               String title,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt
) {
    public static TodoListResponse from(TodoList todoList) {
        return new TodoListResponse(
            todoList.id(),
            todoList.userId(),
            todoList.title(),
            todoList.createdAt(),
            todoList.updatedAt()
        );
    }


}