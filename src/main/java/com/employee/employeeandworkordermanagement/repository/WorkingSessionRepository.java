package com.employee.employeeandworkordermanagement.repository;

import com.employee.employeeandworkordermanagement.entity.Task;
import com.employee.employeeandworkordermanagement.entity.User;
import com.employee.employeeandworkordermanagement.entity.WorkingSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkingSessionRepository extends JpaRepository<WorkingSession, Long> {
    List<WorkingSession> findAllByUser(User user);

    Page<WorkingSession> findAllByUser(User user, Pageable pageable);

    Page<WorkingSession> findAllByUserAndCreatedAtBetween(User user, LocalDateTime fromDate, LocalDateTime toDate,
                                                          Pageable pageable);

    Optional<WorkingSession> findByisActiveAndTaskAndUser(Boolean active, Task task, User user);

}
