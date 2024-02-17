package com.employee.employeeandworkordermanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "working_duration")
public class WorkingDuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Duration duration;
    @NotNull
    private LocalDateTime date;
    @NotNull
    private String taskName;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
