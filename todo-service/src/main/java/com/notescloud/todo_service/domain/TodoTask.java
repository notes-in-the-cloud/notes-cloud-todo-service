package com.notescloud.todo_service.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "todo_tasks", schema = "todo")
public class TodoTask {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID listId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean done;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoPriority priority;

    private LocalDateTime dueDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected TodoTask() {
    }

    public TodoTask(
        UUID listId,
        UUID userId,
        String title,
        TodoPriority priority,
        LocalDateTime dueDate
    ) {
        this.listId = listId;
        this.userId = userId;
        this.title = title;
        this.done = false;
        this.priority = priority == null ? TodoPriority.MEDIUM : priority;
        this.dueDate = dueDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(
        String title,
        TodoPriority priority,
        LocalDateTime dueDate
    ) {
        if (title != null) {
            this.title = title;
        }

        if (priority != null) {
            this.priority = priority;
        }

        if (dueDate != null) {
            this.dueDate = dueDate;
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void markDone() {
        this.done = true;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID id() {
        return id;
    }

    public UUID listId() {
        return listId;
    }

    public UUID userId() {
        return userId;
    }

    public String title() {
        return title;
    }

    public boolean done() {
        return done;
    }

    public TodoPriority priority() {
        return priority;
    }

    public LocalDateTime dueDate() {
        return dueDate;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }
}