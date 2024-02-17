package com.employee.employeeandworkordermanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "archived_task")
public class ArchivedTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Task name can not be blank")
    private String taskName;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    @NotNull(message = "You have to set designer to this archived task.")
    @OneToOne
    @JoinColumn(name = "designer_id")
    private User designer;
    private LocalDateTime createdAt;
    @NotBlank(message = "Task feedback cannot be blank")
    private String taskFeedback;
    @Min(value = 1, message = "Minimum value for grade is 1.")
    @Max(value = 10, message = "Maximum value for grade is 10.")
    private int grade;
    private Duration totalWorkingTime;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
