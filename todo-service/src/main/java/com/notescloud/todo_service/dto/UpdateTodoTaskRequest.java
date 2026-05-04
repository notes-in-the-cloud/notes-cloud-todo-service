package com.notescloud.todo_service.dto;

import com.notescloud.todo_service.domain.TodoPriority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateTodoTaskRequest(
    @Size(max = 255, message = "Title must be at most 255 characters")
    String title,

    TodoPriority priority,

    @FutureOrPresent(message = "Due date cannot be in the past")
    LocalDateTime dueDate
) {
}