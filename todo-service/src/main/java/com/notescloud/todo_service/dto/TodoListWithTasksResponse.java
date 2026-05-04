package com.notescloud.todo_service.dto;

import com.notescloud.todo_service.domain.TodoList;
import com.notescloud.todo_service.domain.TodoTask;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TodoListWithTasksResponse(UUID id,
                                        String title,
                                        LocalDateTime createdAt,
                                        LocalDateTime updatedAt,
                                        List<TodoTaskResponse> tasks
) {
    public static TodoListWithTasksResponse from(
        TodoList todoList,
        List<TodoTask> tasks
    ) {
        return new TodoListWithTasksResponse(
            todoList.id(),
            todoList.title(),
            todoList.createdAt(),
            todoList.updatedAt(),
            tasks.stream()
                .map(TodoTaskResponse::from)
                .toList()
        );
    }
}
