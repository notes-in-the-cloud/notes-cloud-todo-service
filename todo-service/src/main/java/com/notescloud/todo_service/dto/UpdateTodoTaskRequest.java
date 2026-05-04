package com.notescloud.todo_service.dto;

import com.notescloud.todo_service.domain.TodoPriority;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateTodoTaskRequest(
    @Size(max = 255)
    String title,

    TodoPriority priority,

    LocalDateTime dueDate
) {
}