package com.employee.employeeandworkordermanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "task_feedback")
public class TaskFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Feedback cannot be empty.")
    private String feedback;
    @NotNull
    @Min(value = 1, message = "Difficulty should be at least 1")
    @Max(value = 10, message = "Difficulty should be at most 10")
    private Integer grade;
    @NotNull(message = "Status cannot be null")
    private boolean isSet;
    @OneToOne
    @JoinColumn(name = "task_id")
    private Task task;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        isSet = true;
        createdAt = LocalDateTime.now();
    }
}
