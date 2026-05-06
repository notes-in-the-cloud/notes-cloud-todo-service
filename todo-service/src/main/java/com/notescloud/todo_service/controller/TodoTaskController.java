package com.notescloud.todo_service.controller;

import com.notescloud.todo_service.dto.CreateTodoTaskRequest;
import com.notescloud.todo_service.dto.TodoTaskResponse;
import com.notescloud.todo_service.dto.UpdateTodoTaskRequest;
import com.notescloud.todo_service.service.TodoTaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{userId}/todo-tasks")
public class TodoTaskController {

    private final TodoTaskService todoTaskService;

    public TodoTaskController(TodoTaskService todoTaskService) {
        this.todoTaskService = todoTaskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoTaskResponse createTodoTask(
        @PathVariable UUID userId,
        @Valid @RequestBody CreateTodoTaskRequest request
    ) {
        return todoTaskService.createTodoTask(userId, request);
    }

    @GetMapping
    public List<TodoTaskResponse> getStandaloneTasks(
        @PathVariable UUID userId
    ) {
        return todoTaskService.getStandaloneTasks(userId);
    }

    @GetMapping("/{taskId}")
    public TodoTaskResponse getTodoTask(
        @PathVariable UUID userId,
        @PathVariable UUID taskId
    ) {
        return todoTaskService.getTodoTask(userId, taskId);
    }

    @PutMapping("/{taskId}")
    public TodoTaskResponse updateTodoTask(
        @PathVariable UUID userId,
        @PathVariable UUID taskId,
        @Valid @RequestBody UpdateTodoTaskRequest request
    ) {
        return todoTaskService.updateTodoTask(userId, taskId, request);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodoTask(
        @PathVariable UUID userId,
        @PathVariable UUID taskId
    ) {
        todoTaskService.deleteTodoTask(userId, taskId);
    }
}