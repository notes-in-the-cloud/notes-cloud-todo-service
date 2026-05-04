package com.notescloud.todo_service.controller;

import com.notescloud.todo_service.dto.CreateTodoListRequest;
import com.notescloud.todo_service.dto.TodoListResponse;
import com.notescloud.todo_service.dto.UpdateTodoListRequest;
import com.notescloud.todo_service.service.TodoListService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/todo-lists")
public class TodoListController {
    private final TodoListService todoListService;

    public TodoListController(TodoListService todoListService) {
        this.todoListService = todoListService;
    }

    @PostMapping("/create")
    public TodoListResponse createTodoList(@Valid @RequestBody CreateTodoListRequest request) {
        return todoListService.createTodoList(request);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodoList(@PathVariable UUID id) {
        todoListService.deleteTodoList(id);
    }

    @PutMapping("/{id}")
    public TodoListResponse updateTodoList(@PathVariable UUID id,
                                           @Valid @RequestBody UpdateTodoListRequest request) {
        return todoListService.updateTodoList(id, request);
    }
}
