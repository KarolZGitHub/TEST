package com.employee.employeeandworkordermanagement.service;

import com.employee.employeeandworkordermanagement.entity.BreakTime;
import com.employee.employeeandworkordermanagement.entity.BreakTimeIssueRequest;
import com.employee.employeeandworkordermanagement.repository.BreakTimeIssueRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BreakTimeIssueRequestService {
    private final BreakTimeIssueRequestRepository breakTimeIssueRequestRepository;
    private final BreakTimeService breakTimeService;

    public void createBreakTimeIssueRequest(Long id, BreakTimeIssueRequest breakTimeIssueRequest,
                                            Authentication authentication) {
        BreakTime breakTime = breakTimeService.findById(id);
        if (!breakTime.getUser().getEmail().equalsIgnoreCase(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are not authorized.");
        }
        if (breakTimeIssueRequestRepository.findByBreakTime(breakTime).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request has been sent.");
        }
        breakTimeIssueRequest.setBreakTime(breakTimeService.findById(id));
        breakTimeIssueRequestRepository.save(breakTimeIssueRequest);
    }

    public BreakTimeIssueRequest findById(Long id) {
        return breakTimeIssueRequestRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request has not been found"));
    }
}
