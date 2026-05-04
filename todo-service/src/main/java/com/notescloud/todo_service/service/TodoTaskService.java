package com.notescloud.todo_service.service;

import com.notescloud.todo_service.domain.TodoTask;
import com.notescloud.todo_service.dto.CreateTodoTaskRequest;
import com.notescloud.todo_service.dto.TodoTaskResponse;
import com.notescloud.todo_service.dto.UpdateTodoTaskRequest;
import com.notescloud.todo_service.repository.TodoListRepository;
import com.notescloud.todo_service.repository.TodoTaskRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TodoTaskService {
    private final TodoTaskRepository todoTaskRepository;
    private final TodoListRepository todoListRepository;

    public TodoTaskService(TodoTaskRepository todoTaskRepository,
                           TodoListRepository todoListRepository) {
        this.todoTaskRepository = todoTaskRepository;
        this.todoListRepository = todoListRepository;
    }

    public TodoTaskResponse createTodoTask(CreateTodoTaskRequest request) {
        if (request.listId() != null && !todoListRepository.existsById(request.listId())) {
            throw new IllegalArgumentException("Todo list not found with id: " + request.listId());
        }

        TodoTask task = new TodoTask(
            request.listId(),
            request.userId(),
            request.title(),
            request.priority(),
            request.dueDate()
        );

        TodoTask savedTask = todoTaskRepository.save(task);

        return TodoTaskResponse.from(savedTask);
    }

    public void deleteTodoTask(UUID id) {
        if (!todoTaskRepository.existsById(id)) {
            throw new IllegalArgumentException("Task with id: " + id + " not found");
        }
        todoTaskRepository.deleteById(id);
    }

    public TodoTaskResponse updateTodoTask(UUID id, UpdateTodoTaskRequest request) {
        TodoTask task = todoTaskRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Todo task not found with id: " + id));

        task.update(
            request.title(),
            request.priority(),
            request.dueDate()
        );
        TodoTask savedTask = todoTaskRepository.save(task);

        return TodoTaskResponse.from(savedTask);
    }

    public void markDone(UUID id) {
        TodoTask todoTask = todoTaskRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Task with id: " + id + " not found")
        );
        todoTask.markDone();
        todoTaskRepository.save(todoTask);
    }
}
