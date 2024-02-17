package com.employee.employeeandworkordermanagement.controller;

import com.employee.employeeandworkordermanagement.entity.User;
import com.employee.employeeandworkordermanagement.entity.WorkingDuration;
import com.employee.employeeandworkordermanagement.service.UserService;
import com.employee.employeeandworkordermanagement.service.WorkingDurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/work-duration")
public class WorkingDurationController {
    private final WorkingDurationService workingDurationService;
    private final UserService userService;

    @GetMapping("/duration-list")
    public String showUsersWorkInformation(@RequestParam(required = false, defaultValue = "0") int page,
                                           @RequestParam(required = false, defaultValue = "asc") String direction,
                                           @RequestParam(required = false, defaultValue = "id") String sortField,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDate startDate,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDate endDate,
                                           Model model,
                                           Authentication authentication
    ) {
        User theUser = userService.findOptionalUserByEmail(authentication.getName()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User has not been found."));
        model.addAttribute("sortField", sortField);
        Page<WorkingDuration> workingDurationPage = workingDurationService.getUserSortedWorkingTimePage(page, direction,
                sortField, theUser, startDate, endDate);
        model.addAttribute("workingDurationPage", workingDurationPage);
        return "workingDuration/workDurationForUser";
    }

    @GetMapping("/all-duration-list")
    public String showAllWorkInformation(@RequestParam(required = false, defaultValue = "0") int page,
                                         @RequestParam(required = false, defaultValue = "asc") String direction,
                                         @RequestParam(required = false, defaultValue = "id") String sortField,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDate startDate,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDate endDate,
                                         Model model
    ) {
        model.addAttribute("sortField", sortField);
        Page<WorkingDuration> workingDurationPage = workingDurationService.getAllSortedWorkingTimePage(page, direction,
                sortField, startDate, endDate);
        model.addAttribute("workingDurationPage", workingDurationPage);
        return "workingDuration/allWorkDurations";
    }
}
