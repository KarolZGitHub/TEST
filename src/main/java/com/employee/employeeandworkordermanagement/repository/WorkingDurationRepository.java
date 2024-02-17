package com.employee.employeeandworkordermanagement.repository;

import com.employee.employeeandworkordermanagement.entity.User;
import com.employee.employeeandworkordermanagement.entity.WorkingDuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkingDurationRepository extends JpaRepository<WorkingDuration, Long> {

    Page<WorkingDuration> findAllByUser(User user, Pageable pageable);
}
