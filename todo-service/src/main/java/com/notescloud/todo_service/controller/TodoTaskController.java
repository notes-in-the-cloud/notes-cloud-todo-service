package com.notescloud.todo_service.controller;

import com.notescloud.todo_service.dto.CreateTodoTaskRequest;
import com.notescloud.todo_service.dto.TodoTaskResponse;
import com.notescloud.todo_service.dto.UpdateTodoTaskRequest;
import com.notescloud.todo_service.service.TodoTaskService;
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
@RequestMapping("/api/todo-tasks")
public class TodoTaskController {
    private final TodoTaskService todoTaskService;

    public TodoTaskController(TodoTaskService todoTaskService) {
        this.todoTaskService = todoTaskService;
    }

    @PostMapping("/create")
    public TodoTaskResponse createTodoTask(@Valid @RequestBody CreateTodoTaskRequest request) {
        return todoTaskService.createTodoTask(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodoTask(@PathVariable UUID id) {
        todoTaskService.deleteTodoTask(id);
    }

    @PutMapping("/{id}/complete")
    public void markDone(@PathVariable UUID id) {
        todoTaskService.markDone(id);
    }

    @PutMapping("/{id}")
    public TodoTaskResponse updateTodoTask(@PathVariable UUID id,
                                           @Valid @RequestBody UpdateTodoTaskRequest request) {
        return todoTaskService.updateTodoTask(id, request);
    }
}
