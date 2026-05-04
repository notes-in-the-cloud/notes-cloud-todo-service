package com.notescloud.todo_service.service;

import com.notescloud.todo_service.domain.TodoList;
import com.notescloud.todo_service.dto.CreateTodoListRequest;
import com.notescloud.todo_service.dto.TodoListResponse;
import com.notescloud.todo_service.dto.UpdateTodoListRequest;
import com.notescloud.todo_service.repository.TodoListRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TodoListService {
    private final TodoListRepository todoListRepository;

    public TodoListService(TodoListRepository todoListRepository) {
        this.todoListRepository = todoListRepository;
    }

    public TodoListResponse createTodoList(CreateTodoListRequest request) {
        TodoList todoList = new TodoList(request.userId(), request.title());
        todoListRepository.save(todoList);
        return TodoListResponse.from(todoList);
    }

    @Transactional
    public void deleteTodoList(UUID id) {
        if (!todoListRepository.existsById(id)) {
            throw new IllegalArgumentException("Todo list not found with id: " + id);
        }
        todoListRepository.deleteById(id);
    }

    public TodoListResponse updateTodoList(UUID id, UpdateTodoListRequest request) {
        TodoList todoList = todoListRepository.findById(id).orElseThrow(()
            -> new IllegalArgumentException("Todo list not found with id: " + id));
        todoList.updateTitle(request.title());
        TodoList saved = todoListRepository.save(todoList);
        return TodoListResponse.from(saved);
    }
}
