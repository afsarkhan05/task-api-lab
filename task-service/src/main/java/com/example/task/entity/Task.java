package com.example.task.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Task() {
    }

    public Task(String title, String description, TaskPriority priority) {
        this.title = title;
        this.description = description;
        this.priority = priority == null ? TaskPriority.MEDIUM : priority;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void updateStatus(TaskStatus status) {
        this.status = status;
    }

    public void updateDetails(String title, String description, TaskPriority priority) {
        this.title = title;
        this.description = description;
        this.priority = priority == null ? TaskPriority.MEDIUM : priority;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public TaskPriority getPriority() { return priority; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
