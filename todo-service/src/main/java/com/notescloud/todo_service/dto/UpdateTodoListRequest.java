package com.notescloud.todo_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTodoListRequest(
    @NotBlank
    @Size(max = 255)
    String title
) {
}