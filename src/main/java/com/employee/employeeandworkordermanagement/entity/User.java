package com.employee.employeeandworkordermanagement.entity;

import com.employee.employeeandworkordermanagement.data.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String password;
    @NaturalId(mutable = true)
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean isEnabled = false;
    private String profilePicturePath;
    @OneToOne(mappedBy = "designer")
    private Task assignedTask;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WorkingDuration> workingDurations;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    List<BreakTime> breakTimes;
}
