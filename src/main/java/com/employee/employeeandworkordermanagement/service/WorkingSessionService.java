package com.employee.employeeandworkordermanagement.service;

import com.employee.employeeandworkordermanagement.entity.Task;
import com.employee.employeeandworkordermanagement.entity.User;
import com.employee.employeeandworkordermanagement.entity.WorkingDuration;
import com.employee.employeeandworkordermanagement.entity.WorkingSession;
import com.employee.employeeandworkordermanagement.repository.TaskRepository;
import com.employee.employeeandworkordermanagement.repository.WorkingDurationRepository;
import com.employee.employeeandworkordermanagement.repository.WorkingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkingSessionService {
    private final WorkingSessionRepository workingSessionRepository;
    private final WorkingDurationRepository workingDurationRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final BreakTimeService breakTimeService;

    public WorkingSession findById(Long id) {
        return workingSessionRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Working time has not been found."));
    }

    public void createWorkingSession(Task task, Authentication authentication) {
        userService.checkCurrentDesigner(task, authentication);
        List<WorkingSession> workingSessions = workingSessionRepository.findAllByUser(task.getDesigner());
        boolean isWorkActive = workingSessions.stream().anyMatch(WorkingSession::isActive);
        if (isWorkActive) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is already active work session.");
        } else {
            WorkingSession workingSession = new WorkingSession();
            workingSession.setWorkStarted(LocalDateTime.now());
            workingSession.setUser(task.getDesigner());
            workingSession.setTask(task);
            workingSessionRepository.save(workingSession);
        }
    }

    public void stopWorkingSession(Task task, Authentication authentication) {
        userService.checkCurrentDesigner(task, authentication);
        List<WorkingSession> workingSessions = workingSessionRepository.findAllByUser(task.getDesigner());
        WorkingSession workingSession = workingSessions.stream().filter(WorkingSession::isActive)
                .findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                        "Working Session has not been found."));
        if (breakTimeService.checkIfBreakIsActive(task.getDesigner())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You cannot stop your work during a break");
        }
        workingSession.setWorkFinished(LocalDateTime.now());
        WorkingDuration workingDuration = new WorkingDuration();
        workingDuration.setDate(LocalDateTime.now());
        workingDuration.setUser(task.getDesigner());
        workingDuration.setTaskName(task.getTaskName());
        workingDuration.setDuration(breakTimeService.workingDurationWithBreaks(task.getDesigner().getBreakTimes(),
                workingSession));
        workingSession.setActive(false);
        workingDurationRepository.save(workingDuration);
        workingSessionRepository.save(workingSession);
        task.setWorkDuration(task.getWorkDuration().plus(workingDuration.getDuration()));
        taskRepository.save(task);
    }

    public boolean hideStopButton(Task task) {
        List<WorkingSession> workingSessions = workingSessionRepository.findAllByUser(task.getDesigner());
        return workingSessions.stream().anyMatch(workingSession -> workingSession.getWorkFinished() == null);
    }

    public Page<WorkingSession> getUserSortedWorkingTimePage(int page, String direction, String sortField, User user) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Pageable pageable = PageRequest.of(page, 50, sort);
        return workingSessionRepository.findAllByUser(user, pageable);
    }

    public Page<WorkingSession> getAllSortedWorkingTimePage(int page, String direction, String sortField) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Pageable pageable = PageRequest.of(page, 50, sort);
        return workingSessionRepository.findAll(pageable);
    }
}
