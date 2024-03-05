package com.employee.employeeandworkordermanagement.repository;

import com.employee.employeeandworkordermanagement.entity.BreakTime;
import com.employee.employeeandworkordermanagement.entity.BreakTimeIssueRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BreakTimeIssueRequestRepository extends JpaRepository<BreakTimeIssueRequest, Long> {
    Optional<BreakTimeIssueRequest> findByBreakTime(BreakTime breakTime);
}
