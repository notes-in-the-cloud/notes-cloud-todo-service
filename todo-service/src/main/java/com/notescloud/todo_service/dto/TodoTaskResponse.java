package com.notescloud.todo_service.dto;

import com.notescloud.todo_service.domain.TodoPriority;
import com.notescloud.todo_service.domain.TodoTask;

import java.time.LocalDateTime;
import java.util.UUID;

public record TodoTaskResponse(UUID id,
                               UUID listId,
                               UUID userId,
                               String title,
                               TodoPriority priority,
                               LocalDateTime dueDate) {
    public static TodoTaskResponse from(TodoTask todoTask) {
        return new TodoTaskResponse(
            todoTask.id(),
            todoTask.listId(),
            todoTask.userId(),
            todoTask.title(),
            todoTask.priority(),
            todoTask.dueDate()
        );
    }
}
