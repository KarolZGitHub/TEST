package com.employee.employeeandworkordermanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "working_session")
public class WorkingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Creation date cannot be empty.")
    private LocalDateTime createdAt;
    private LocalDateTime workStarted;
    private LocalDateTime workFinished;
    @NotNull(message = "Working time have to be set to user.")
    @ManyToOne
    private User user;
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
    @NotNull(message = "Active status cannot be null")
    private boolean isActive;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isActive = true;
    }
}
