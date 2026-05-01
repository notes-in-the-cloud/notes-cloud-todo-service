package com.notescloud.todo_service.service;

import com.notescloud.todo_service.domain.TodoList;
import com.notescloud.todo_service.dto.CreateTodoListRequest;
import com.notescloud.todo_service.dto.TodoListResponse;
import com.notescloud.todo_service.repository.InMemoryTodoListRepository;
import org.springframework.stereotype.Service;

@Service
public class TodoListService {
    private final InMemoryTodoListRepository todoListRepository;

    public TodoListService(InMemoryTodoListRepository todoListRepository) {
        this.todoListRepository = todoListRepository;
    }

    public TodoListResponse createTodoList(CreateTodoListRequest request) {

        todoListRepository.save(new TodoList(
            request.userId(),
            request.title()
        ));
        return new TodoListResponse();
    }
}
