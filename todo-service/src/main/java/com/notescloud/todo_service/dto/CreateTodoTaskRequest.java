package com.notescloud.todo_service.dto;

import com.notescloud.todo_service.domain.TodoPriority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTodoTaskRequest(
    UUID listId,

    @NotNull(message = "User id is required")
    UUID userId,

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    String title,

    TodoPriority priority,

    @FutureOrPresent(message = "Due date cannot be in the past")
    LocalDateTime dueDate
) {
}