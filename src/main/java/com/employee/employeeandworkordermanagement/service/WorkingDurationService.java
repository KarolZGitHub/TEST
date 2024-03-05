package com.employee.employeeandworkordermanagement.service;

import com.employee.employeeandworkordermanagement.entity.User;
import com.employee.employeeandworkordermanagement.entity.WorkingDuration;
import com.employee.employeeandworkordermanagement.repository.WorkingDurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WorkingDurationService {
    private final WorkingDurationRepository workingDurationRepository;

    public Page<WorkingDuration> getUserSortedWorkingTimePage(int page, String direction, String sortField, User user,
                                                              LocalDate startDate, LocalDate endDate) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Pageable pageable = PageRequest.of(page, 50, sort);

        if (startDate != null && endDate != null) {
            return workingDurationRepository.findAllByUserAndDateBetween(user, startDate.atStartOfDay(), endDate.atStartOfDay(), pageable);
        } else {
            return workingDurationRepository.findAllByUser(user, pageable);
        }
    }

    public Page<WorkingDuration> getAllSortedWorkingTimePage(int page, String direction, String sortField,
                                                             LocalDate startDate, LocalDate endDate) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Pageable pageable = PageRequest.of(page, 50, sort);
        if (startDate != null && endDate != null) {
            return workingDurationRepository.findAllByDateBetween(startDate.atStartOfDay(), endDate.atStartOfDay(), pageable);
        } else {
            return workingDurationRepository.findAll(pageable);
        }
    }

    public WorkingDuration findByDateBetween(LocalDateTime start, LocalDateTime finish, User user) {
        return workingDurationRepository.findByDateBetweenAndUser(start, finish, user).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Working duration has not been found"));
    }

    public WorkingDuration findByTaskNameAndUser(String taskName, User user) {
        return workingDurationRepository.findByTaskNameAndUser(taskName, user).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Working duration has not been found"));
    }
}
