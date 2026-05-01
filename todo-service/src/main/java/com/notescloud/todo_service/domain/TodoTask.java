package com.notescloud.todo_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class TodoTask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID todoListId;

    private UUID userId;

    private String title;

    private boolean completed;

    public TodoTask() {
        id = UUID.randomUUID();
        todoListId = UUID.randomUUID();
        userId = UUID.randomUUID();
        title = "New Task";
        completed = false;
    }

    public TodoTask(UUID todoListId, UUID userId, String title, boolean completed) {
        this.id = UUID.randomUUID();
        this.todoListId = todoListId;
        this.userId = userId;
        this.title = title;
        this.completed = completed;
    }

    public UUID id() {
        return id;
    }

    public UUID todoListId() {
        return todoListId;
    }

    public UUID userId() {
        return userId;
    }

    public String title() {
        return title;
    }

    public boolean completed() {
        return completed;
    }

    public void markAsComplete() {
        completed = true;
    }
}
