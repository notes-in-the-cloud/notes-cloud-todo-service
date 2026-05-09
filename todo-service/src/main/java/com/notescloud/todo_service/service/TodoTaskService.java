package com.notescloud.todo_service.service;

import com.notescloud.todo_service.domain.TodoPriority;
import com.notescloud.todo_service.domain.TodoTask;
import com.notescloud.todo_service.dto.CreateTodoTaskRequest;
import com.notescloud.todo_service.dto.TodoTaskResponse;
import com.notescloud.todo_service.dto.UpdateTodoTaskRequest;
import com.notescloud.todo_service.exception.ResourceNotFoundException;
import com.notescloud.todo_service.repository.TodoListRepository;
import com.notescloud.todo_service.repository.TodoTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TodoTaskService {
    private static final TodoPriority DEFAULT_PRIORITY = TodoPriority.MEDIUM;

    private final TodoTaskRepository todoTaskRepository;
    private final TodoListRepository todoListRepository;

    public TodoTaskService(TodoTaskRepository todoTaskRepository,
                           TodoListRepository todoListRepository) {
        this.todoTaskRepository = todoTaskRepository;
        this.todoListRepository = todoListRepository;
    }

    @Transactional
    public TodoTaskResponse createTodoTask(UUID userId, CreateTodoTaskRequest request) {
        validateTodoListBelongsToUser(userId, request.listId());

        TodoTask task = new TodoTask(
            request.listId(),
            userId,
            request.title(),
            getPriorityOrDefault(request.priority()),
            request.dueDate()
        );

        TodoTask savedTask = todoTaskRepository.save(task);

        return TodoTaskResponse.from(savedTask);
    }

    @Transactional
    public void deleteTodoTask(UUID userId, UUID taskId) {
        TodoTask task = getTaskForUser(userId, taskId);
        todoTaskRepository.delete(task);
    }

    @Transactional
    public TodoTaskResponse updateTodoTask(UUID userId, UUID taskId, UpdateTodoTaskRequest request) {
        TodoTask task = getTaskForUser(userId, taskId);

        task.update(
            request.title(),
            request.priority(),
            request.dueDate(),
            request.done()
        );

        TodoTask savedTask = todoTaskRepository.save(task);

        return TodoTaskResponse.from(savedTask);
    }

    @Transactional(readOnly = true)
    public TodoTaskResponse getTodoTask(UUID userId, UUID taskId) {
        return TodoTaskResponse.from(getTaskForUser(userId, taskId));
    }

    @Transactional(readOnly = true)
    public List<TodoTaskResponse> getStandaloneTasks(UUID userId) {
        LocalDateTime now = LocalDateTime.now();

        return todoTaskRepository.findAllByUserIdAndDoneFalseAndListIdIsNull(userId)
            .stream()
            .filter(task -> task.dueDate() == null || !task.dueDate().isBefore(now))
            .map(TodoTaskResponse::from)
            .toList();
    }

    private void validateTodoListBelongsToUser(UUID userId, UUID listId) {
        if (listId == null) {
            return;
        }

        boolean listExistsForUser = todoListRepository.existsByIdAndUserId(listId, userId);

        if (!listExistsForUser) {
            throw new ResourceNotFoundException("Todo list not found with id: " + listId);
        }
    }

    private TodoPriority getPriorityOrDefault(TodoPriority priority) {
        return priority != null ? priority : DEFAULT_PRIORITY;
    }

    private TodoTask getTaskForUser(UUID userId, UUID taskId) {
        return todoTaskRepository.findByIdAndUserId(taskId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Todo task not found with id: " + taskId));
    }
}