package com.notescloud.todo_service.service;

import com.notescloud.todo_service.domain.TodoTask;
import com.notescloud.todo_service.dto.CreateTodoTaskRequest;
import com.notescloud.todo_service.dto.TodoTaskResponse;
import com.notescloud.todo_service.dto.UpdateTodoTaskRequest;
import com.notescloud.todo_service.exception.ResourceNotFoundException;
import com.notescloud.todo_service.repository.TodoListRepository;
import com.notescloud.todo_service.repository.TodoTaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    public TodoTaskResponse createTodoTask(UUID userId, CreateTodoTaskRequest request) {
        if (request.listId() != null && !todoListRepository.existsById(request.listId())) {
            throw new ResourceNotFoundException("Todo list not found with id: " + request.listId());
        }

        TodoTask task = new TodoTask(
            request.listId(),
            userId,
            request.title(),
            request.priority(),
            request.dueDate()
        );

        TodoTask savedTask = todoTaskRepository.save(task);

        return TodoTaskResponse.from(savedTask);
    }

    public void deleteTodoTask(UUID userId, UUID taskId) {
        TodoTask task = getTaskForUser(userId, taskId);
        todoTaskRepository.delete(task);
    }

    public TodoTaskResponse updateTodoTask(UUID userId, UUID taskId, UpdateTodoTaskRequest request) {
        TodoTask task = getTaskForUser(userId, taskId);

        task.update(
            request.title(),
            request.priority(),
            request.dueDate()
        );

        TodoTask savedTask = todoTaskRepository.save(task);

        return TodoTaskResponse.from(savedTask);
    }

    public void markDone(UUID userId, UUID taskId) {
        TodoTask task = getTaskForUser(userId, taskId);
        task.markDone();
        todoTaskRepository.save(task);
    }

    public TodoTaskResponse getTodoTask(UUID userId, UUID taskId) {
        return TodoTaskResponse.from(getTaskForUser(userId, taskId));
    }

    public List<TodoTaskResponse> getStandaloneTasks(UUID userId) {
        LocalDateTime now = LocalDateTime.now();

        return todoTaskRepository.findAllByUserIdAndDoneFalseAndListIdIsNull(userId)
            .stream()
            .filter(task -> task.dueDate() == null || !task.dueDate().isBefore(now))
            .map(TodoTaskResponse::from)
            .toList();
    }

    private TodoTask getTaskForUser(UUID userId, UUID taskId) {
        return todoTaskRepository.findByIdAndUserId(taskId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Todo task not found with id: " + taskId));
    }
}