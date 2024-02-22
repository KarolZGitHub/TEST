package com.employee.employeeandworkordermanagement.service;

import com.employee.employeeandworkordermanagement.entity.BreakTime;
import com.employee.employeeandworkordermanagement.entity.Task;
import com.employee.employeeandworkordermanagement.entity.User;
import com.employee.employeeandworkordermanagement.entity.WorkingSession;
import com.employee.employeeandworkordermanagement.repository.BreakTimeRepository;
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
        List<BreakTime> filteredBreakTimes = breakTimes.getContent().stream()
                .filter(breakTime -> breakTime.getBreakDuration().compareTo(Duration.ofMinutes(20)) > 0 ||
                        breakTime.getBreakDuration().compareTo(Duration.ofMinutes(1)) < 0).collect(Collectors.toList());
        return new PageImpl<>(filteredBreakTimes, pageable, breakTimes.getTotalElements());
    }

    public Page<BreakTime> findAllAnomalies(Pageable pageable) {
        Page<BreakTime> breakTimes = breakTimeRepository.findAll(pageable);
        List<BreakTime> filteredBreakTimes = breakTimes.getContent().stream()
                .filter(breakTime -> breakTime.getBreakDuration().compareTo(Duration.ofMinutes(20)) > 0 ||
                        breakTime.getBreakDuration().compareTo(Duration.ofMinutes(1)) < 0).collect(Collectors.toList());
        return new PageImpl<>(filteredBreakTimes, pageable, breakTimes.getTotalElements());
    }
}
