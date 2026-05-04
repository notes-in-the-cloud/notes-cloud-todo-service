package com.notescloud.todo_service.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
    String message,
    int status,
    LocalDateTime timestamp
) {
}