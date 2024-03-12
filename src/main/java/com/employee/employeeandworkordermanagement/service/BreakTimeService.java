package com.employee.employeeandworkordermanagement.service;

import com.employee.employeeandworkordermanagement.entity.*;
import com.employee.employeeandworkordermanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BreakTimeService {
    private final BreakTimeRepository breakTimeRepository;
    private final UserService userService;
    private final WorkingSessionRepository workingSessionRepository;
    private final WorkingDurationRepository workingDurationRepository;
    private final BreakTimeIssueRequestRepository breakTimeIssueRequestRepository;
    private final TaskRepository taskRepository;

    public Duration workingDurationWithBreaks(List<BreakTime> breakTimeList, WorkingSession workingSession) {
        List<BreakTime> filteredList = breakTimeList.stream().filter(b -> b.getStartTime().isAfter(
                workingSession.getCreatedAt())).toList();
        Duration totalBreaksDuration = filteredList.stream()
                .filter(b -> b.getStartTime().isBefore(b.getFinishTime()))
                .map(b -> Duration.between(b.getStartTime(), b.getFinishTime()))
                .reduce(Duration.ZERO, Duration::plus);
        Duration workingSessionDuration = Duration.between(workingSession.getWorkStarted(),
                workingSession.getWorkFinished());
        if (totalBreaksDuration.compareTo(workingSessionDuration) > 0) {
            return Duration.ZERO;
        } else {
            return workingSessionDuration.minus(totalBreaksDuration);
        }
    }

    public void startBreakTime(Task task, Authentication authentication) {
        userService.checkCurrentDesigner(task, authentication);
        BreakTime breakTime = new BreakTime();
        breakTime.setStartTime(LocalDateTime.now());
        breakTime.setUser(task.getDesigner());
        breakTime.setWorkingAtTaskName(task.getTaskName());
        breakTime.setWorkingSession(workingSessionRepository.findByisActiveAndTaskAndUser(true, task,
                task.getDesigner()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Working session has not been found.")));
        breakTimeRepository.save(breakTime);
    }

    public void stopBreakTime(Task task, Authentication authentication) {
        userService.checkCurrentDesigner(task, authentication);
        List<BreakTime> breakTimeList = task.getDesigner().getBreakTimes();
        BreakTime breakTime = breakTimeList.stream().filter(BreakTime::isActive).findFirst().orElseThrow(
                () -> new ResponseStatusException(HttpStatus.CONFLICT, "Breaking time has not been found."));
        breakTime.setFinishTime(LocalDateTime.now());
        breakTime.setActive(false);
        breakTime.setBreakDuration(Duration.between(breakTime.getStartTime(), breakTime.getFinishTime()));
        breakTimeRepository.save(breakTime);
    }

    public boolean showStopButton(Task task) {
        List<BreakTime> breakTimeList = task.getDesigner().getBreakTimes();
        return breakTimeList.stream().anyMatch(BreakTime::isActive);
    }

    public boolean checkIfBreakIsActive(User user) {
        List<BreakTime> breakTimeList = user.getBreakTimes();
        return breakTimeList.stream().anyMatch(BreakTime::isActive);
    }

    public Page<BreakTime> getBreakTimesForUser(Authentication authentication, PageRequest pageRequest) {
        if (authentication != null) {
            User user = userService.findUserByEmail(authentication.getName());
            return breakTimeRepository.findAllByUser(user, pageRequest);
        } else {
            return Page.empty();
        }
    }

    public Page<BreakTime> getAllBreakTimesList(PageRequest pageRequest) {
        return breakTimeRepository.findAll(pageRequest);
    }

    public Page<BreakTime> findAnomalousBreakTimes(User user, Pageable pageable) {
        Page<BreakTime> breakTimes = breakTimeRepository.findAllByUser(user, pageable);
        List<BreakTime> filteredBreakTimes = breakTimes.getContent().stream().filter(breakTime ->
                        breakTime.getBreakDuration() != null)
                .filter(breakTime -> breakTime.getBreakDuration().compareTo(Duration.ofMinutes(20)) > 0 ||
                        breakTime.getBreakDuration().compareTo(Duration.ofMinutes(1)) < 0).collect(Collectors.toList());
        return new PageImpl<>(filteredBreakTimes, pageable, breakTimes.getTotalElements());
    }

    public BreakTime findById(Long id) {
        return breakTimeRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Break time has not been found."));
    }

    public void acceptBreakTimeIssueRequest(BreakTimeIssueRequest breakTimeIssueRequest) {
        BreakTime breakTime = breakTimeIssueRequest.getBreakTime();
        WorkingSession workingSession = breakTime.getWorkingSession();
        if (workingSession.isActive()) {
            breakTimeRepository.delete(breakTime);
            breakTimeIssueRequestRepository.delete(breakTimeIssueRequest);
        } else {
            Task task = workingSession.getTask();
            WorkingDuration workingDuration = workingDurationRepository.findByTaskNameAndUser(task.getTaskName(),
                    workingSession.getUser()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Working duration has not been found."));

            switch (breakTimeIssueRequest.getType().toLowerCase()) {
                case "accidentally added break time.":
                    handleAccidentallyAddedBreakTime(breakTime, workingSession, workingDuration, task,
                            breakTimeIssueRequest);
                    break;
                case "forgot to stop break time.":
                    handleForgotToStopBreakTime(breakTime, workingSession, workingDuration, task,
                            breakTimeIssueRequest);
                    break;
                case "forgot to start break time.":
                    handleForgotToStartBreakTime(breakTime, workingSession, workingDuration, task,
                            breakTimeIssueRequest);
                    break;
            }
        }
    }

    public void handleAccidentallyAddedBreakTime(BreakTime breakTime, WorkingSession workingSession,
                                                 WorkingDuration workingDuration, Task task,
                                                 BreakTimeIssueRequest breakTimeIssueRequest) {
        Duration durationForReduce = breakTime.getBreakDuration();
        workingDuration.setDuration(workingDuration.getDuration().minus(durationForReduce));
        workingDurationRepository.save(workingDuration);
        workingSession.setDuration(workingSession.getDuration().minus(durationForReduce));
        workingSessionRepository.save(workingSession);
        task.setWorkDuration(task.getWorkDuration().minus(durationForReduce));
        taskRepository.save(task);
        breakTimeRepository.delete(breakTime);
        breakTimeIssueRequestRepository.delete(breakTimeIssueRequest);
    }

    public void handleForgotToStopBreakTime(BreakTime breakTime, WorkingSession workingSession,
                                            WorkingDuration workingDuration, Task task,
                                            BreakTimeIssueRequest breakTimeIssueRequest) {
        Duration durationDifference = breakTime.getBreakDuration().minus(Duration.ofMinutes(
                breakTimeIssueRequest.getMinutes()));
        breakTime.setBreakDuration(Duration.ofMinutes(breakTimeIssueRequest.getMinutes()));
        breakTimeRepository.save(breakTime);
        workingDuration.setDuration(workingDuration.getDuration().minus(durationDifference));
        workingDurationRepository.save(workingDuration);
        workingSession.setDuration(workingSession.getDuration().minus(durationDifference));
        workingSessionRepository.save(workingSession);
        task.setWorkDuration(task.getWorkDuration().minus(durationDifference));
        breakTimeIssueRequestRepository.delete(breakTimeIssueRequest);
    }

    public void handleForgotToStartBreakTime(BreakTime breakTime, WorkingSession workingSession,
                                             WorkingDuration workingDuration, Task task,
                                             BreakTimeIssueRequest breakTimeIssueRequest) {
        Duration durationDifference = Duration.ofMinutes(breakTimeIssueRequest.getMinutes())
                .minus(breakTime.getBreakDuration());
        breakTime.setBreakDuration(Duration.ofMinutes(breakTimeIssueRequest.getMinutes()));
        breakTimeRepository.save(breakTime);
        workingDuration.setDuration(workingDuration.getDuration().plus(durationDifference));
        workingDurationRepository.save(workingDuration);
        workingSession.setDuration(workingSession.getDuration().plus(durationDifference));
        workingSessionRepository.save(workingSession);
        task.setWorkDuration(task.getWorkDuration().plus(durationDifference));
        taskRepository.save(task);
        breakTimeIssueRequestRepository.delete(breakTimeIssueRequest);
    }

    public void declineBreakTimeIssueRequest(BreakTimeIssueRequest breakTimeIssueRequest) {
        breakTimeIssueRequestRepository.delete(breakTimeIssueRequest);
    }
}
