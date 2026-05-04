package com.notescloud.todo_service.dto;

import com.notescloud.todo_service.domain.TodoPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTodoTaskRequest(
    UUID listId,

    @NotNull
    UUID userId,

    @NotBlank
    @Size(max = 255)
    String title,

    TodoPriority priority,

    LocalDateTime dueDate
) {
}