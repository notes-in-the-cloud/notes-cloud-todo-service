package com.notescloud.todo_service.repository;

import com.notescloud.todo_service.domain.TodoList;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTodoListRepository {
    private final ConcurrentHashMap<UUID, TodoList> todoLists = new ConcurrentHashMap<>();

    public TodoList save(TodoList todoList) {
        todoLists.put(todoList.id(), todoList);
        return todoList;
    }

    public Optional<TodoList> findById(UUID id) {
        return Optional.ofNullable(todoLists.get(id));
    }

    public List<TodoList> findAllByUserId(UUID userId) {
        return todoLists.values()
            .stream()
            .filter(todoList -> todoList.userId().equals(userId))
            .toList();
    }

    public void deleteById(UUID id) {
        todoLists.remove(id);
    }

    public boolean existsById(UUID id) {
        return todoLists.containsKey(id);
    }

    public List<TodoList> findAll() {
        return new ArrayList<>(todoLists.values());
    }
}