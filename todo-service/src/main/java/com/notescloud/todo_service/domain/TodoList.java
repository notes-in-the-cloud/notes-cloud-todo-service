package com.notescloud.todo_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;
import java.util.UUID;

@Entity
public class TodoList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID userId;

    private String title;

    private Instant createdAt;

    public TodoList(UUID userId, String title) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.title = title;
        this.createdAt = Instant.now();
    }

    public TodoList() {
        this.id = UUID.randomUUID();
        this.userId = UUID.randomUUID();
        this.title = "New List";
        this.createdAt = Instant.now();
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public String title() {
        return title;
    }

    public Instant createdAt() {
        return createdAt;
    }
}