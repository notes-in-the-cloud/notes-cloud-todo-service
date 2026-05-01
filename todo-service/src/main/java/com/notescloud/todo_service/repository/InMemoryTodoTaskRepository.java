package com.notescloud.todo_service.repository;

import com.notescloud.todo_service.domain.TodoTask;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTodoTaskRepository {
    private final ConcurrentHashMap<UUID, TodoTask> tasks = new ConcurrentHashMap<>();

    public TodoTask save(TodoTask task) {
        tasks.put(task.id(), task);
        return task;
    }

    public Optional<TodoTask> findById(UUID id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public List<TodoTask> findAllByListId(UUID listId) {
        return tasks.values()
            .stream()
            .filter(task -> task.todoListId().equals(listId))
            .toList();
    }

    public List<TodoTask> findAllByUserId(UUID userId) {
        return tasks.values()
            .stream()
            .filter(task -> task.userId().equals(userId))
            .toList();
    }

    public void deleteById(UUID id) {
        tasks.remove(id);
    }

    public void deleteAllByListId(UUID listId) {
        tasks.values().removeIf(task -> task.todoListId().equals(listId));
    }

    public boolean existsById(UUID id) {
        return tasks.containsKey(id);
    }

    public List<TodoTask> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public TodoTask markAsComplete(UUID taskId) {
        TodoTask task = tasks.get(taskId);
        task.markAsComplete();
        return task;
    }
}