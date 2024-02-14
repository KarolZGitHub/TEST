package com.employee.employeeandworkordermanagement.service;

import com.employee.employeeandworkordermanagement.entity.User;
import com.employee.employeeandworkordermanagement.entity.WorkingDuration;
import com.employee.employeeandworkordermanagement.repository.WorkingDurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkingDurationService {
    private final WorkingDurationRepository workingDurationRepository;

    public Page<WorkingDuration> getUserSortedWorkingTimePage(int page, String direction, String sortField, User user) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Pageable pageable = PageRequest.of(page, 50, sort);
        return workingDurationRepository.findAllByUser(user, pageable);
    }

    public Page<WorkingDuration> getAllSortedWorkingTimePage(int page, String direction, String sortField) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Pageable pageable = PageRequest.of(page, 50, sort);
        return workingDurationRepository.findAll(pageable);
    }
}
