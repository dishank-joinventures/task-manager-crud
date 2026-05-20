package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    private String taskName;
    private String taskStatus;

    @Column(updatable = false)
    private LocalDateTime taskCreatedAt;

    private LocalDateTime taskDueDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        this.taskCreatedAt = LocalDateTime.now(); // auto set on create, like DEFAULT NOW()
    }
}