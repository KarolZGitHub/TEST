package com.employee.employeeandworkordermanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "break_time")
public class BreakTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Working information cannot be null")
    private String workingAtTaskName;
    @NotNull(message = "Start time cannot be null")
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    @NotNull(message = "Active status cannot be null")
    private boolean isActive;
    private Duration breakDuration;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @NotNull(message = "Session has to be set.")
    @ManyToOne
    private WorkingSession workingSession;

    @PrePersist
    protected void onCreate() {
        this.startTime = LocalDateTime.now();
        isActive = true;
    }
}
