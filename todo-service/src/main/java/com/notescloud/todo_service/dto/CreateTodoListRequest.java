package com.notescloud.todo_service.dto;

import java.util.UUID;

public record CreateTodoListRequest(
    UUID userId,
    String title
) { }
