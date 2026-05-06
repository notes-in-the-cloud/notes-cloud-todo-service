package com.notescloud.todo_service.controller;

import com.notescloud.todo_service.dto.CreateTodoListRequest;
import com.notescloud.todo_service.dto.TodoListResponse;
import com.notescloud.todo_service.dto.TodoListWithTasksResponse;
import com.notescloud.todo_service.dto.UpdateTodoListRequest;
import com.notescloud.todo_service.service.TodoListService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{userId}/todo-lists")
public class TodoListController {

    private final TodoListService todoListService;

    public TodoListController(TodoListService todoListService) {
        this.todoListService = todoListService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoListResponse createTodoList(
        @PathVariable UUID userId,
        @Valid @RequestBody CreateTodoListRequest request
    ) {
        return todoListService.createTodoList(userId, request);
    }

    @GetMapping
    public List<TodoListWithTasksResponse> getTodoListsWithTasks(
        @PathVariable UUID userId
    ) {
        return todoListService.getTodoListsWithTasks(userId);
    }

    @GetMapping("/{listId}")
    public TodoListWithTasksResponse getTodoList(
        @PathVariable UUID userId,
        @PathVariable UUID listId
    ) {
        return todoListService.getTodoList(userId, listId);
    }

    @PutMapping("/{listId}")
    public TodoListResponse updateTodoList(
        @PathVariable UUID userId,
        @PathVariable UUID listId,
        @Valid @RequestBody UpdateTodoListRequest request
    ) {
        return todoListService.updateTodoList(userId, listId, request);
    }

    @DeleteMapping("/{listId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodoList(
        @PathVariable UUID userId,
        @PathVariable UUID listId
    ) {
        todoListService.deleteTodoList(userId, listId);
    }
}