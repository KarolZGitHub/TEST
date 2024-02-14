package com.employee.employeeandworkordermanagement.controller;

import com.employee.employeeandworkordermanagement.dto.UserDTO;
import com.employee.employeeandworkordermanagement.entity.Task;
import com.employee.employeeandworkordermanagement.entity.User;
import com.employee.employeeandworkordermanagement.entity.WorkingSession;
import com.employee.employeeandworkordermanagement.service.TaskService;
import com.employee.employeeandworkordermanagement.service.UserService;
import com.employee.employeeandworkordermanagement.service.WorkingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/work")
public class WorkingSessionController {
    private final WorkingSessionService workingSessionService;
    private final UserService userService;
    private final TaskService taskService;

    @ModelAttribute("user")
    public UserDTO userDTO(Authentication authentication) {
        if (authentication != null) {
            return userService.getUserDTO(authentication);
        } else {
            return null;
        }
    }

    @GetMapping("/work-list")
    public String showUsersWorkInformation(@RequestParam(required = false, defaultValue = "0") int page,
                                           @RequestParam(required = false, defaultValue = "asc") String direction,
                                           @RequestParam(required = false, defaultValue = "id") String sortField,
                                           Model model,
                                           Authentication authentication
    ) {
        User theUser = userService.findOptionalUserByEmail(authentication.getName()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User has not been found."));
        model.addAttribute("sortField", sortField);
        Page<WorkingSession> workingSessionPage = workingSessionService.getUserSortedWorkingTimePage(page, direction, sortField, theUser);
        model.addAttribute("workingSessionPage", workingSessionPage);
        return "workingSession/workingSessionForUser";
    }

    @GetMapping("/all-work-list")
    public String showAllWorkInformation(@RequestParam(required = false, defaultValue = "0") int page,
                                         @RequestParam(required = false, defaultValue = "asc") String direction,
                                         @RequestParam(required = false, defaultValue = "id") String sortField,
                                         Model model
    ) {
        model.addAttribute("sortField", sortField);
        Page<WorkingSession> workingSessionPage = workingSessionService.getAllSortedWorkingTimePage(page, direction, sortField);
        model.addAttribute("workingSessionPage", workingSessionPage);
        return "workingSession/workingSessionList";
    }

    @GetMapping("finish-work")
    public String handleStopWork(@RequestParam(name = "id") Long id, Authentication authentication) {
        Task task = taskService.findById(id);
        workingSessionService.stopWorkingSession(task, authentication);
        return "redirect:/task/your-task";
    }

    @GetMapping("start-work")
    public String handleStartWorking(@RequestParam(name = "id") Long id, Authentication authentication) {
        Task task = taskService.findById(id);
        workingSessionService.createWorkingSession(task, authentication);
        return "redirect:/task/your-task";
    }
}
