package com.employee.employeeandworkordermanagement.controller;

import com.employee.employeeandworkordermanagement.data.Role;
import com.employee.employeeandworkordermanagement.dto.UserDTO;
import com.employee.employeeandworkordermanagement.entity.ArchivedTask;
import com.employee.employeeandworkordermanagement.entity.Task;
import com.employee.employeeandworkordermanagement.entity.User;
import com.employee.employeeandworkordermanagement.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/task")
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;
    private final ArchivedTaskService archivedTaskService;
    private final WorkingSessionService workingSessionService;
    private final BreakTimeService breakTimeService;

    @ModelAttribute("user")
    public UserDTO userDTO(Authentication authentication) {
        if (authentication != null) {
            return userService.getUserDTO(authentication);
        } else {
            return null;
        }
    }

    @ModelAttribute("blockDesignerView")
    public boolean blockDesignerAccess(Authentication authentication) {
        if (authentication != null) {
            User user = userService.findOptionalUserByEmail(authentication.getName()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User has not been found"));
            return user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.OPERATOR);
        } else {
            return false;
        }
    }

    @GetMapping("/all-tasks")
    public String showAllTasks(@RequestParam(required = false, defaultValue = "0") int page,
                               @RequestParam(required = false, defaultValue = "asc") String direction,
                               @RequestParam(required = false, defaultValue = "id") String sortField,
                               Model model) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Page<Task> taskPage = taskService.getAllTasksPage(PageRequest.of(page, 50, sort));
        model.addAttribute("taskPage", taskPage);
        model.addAttribute("sortField", sortField);
        return "task/tasks";
    }

    @GetMapping("/complete-task")
    public String completeTask(@RequestParam(name = "id") Long id, Model model, Authentication authentication) {
        taskService.markTaskAsComplete(id, authentication);
        model.addAttribute("message", "Congratulations, you have marked task as completed.");
        return "/task/taskCompleted";
    }

    @GetMapping("/your-task")
    public String showSingleTask(Authentication authentication, Model model) {
        User designer = userService.findUserByEmail(authentication.getName());
        Task task = taskService.findTaskByUser(designer);
        model.addAttribute("hideStopButton", workingSessionService.hideStopButton(task));
        model.addAttribute("task", task);
        model.addAttribute("showStopBreakButton", breakTimeService.showStopButton(task));
        return "/task/singleTask";
    }

    @GetMapping("/archived-tasks")
    public String showAllArchivedTasks(@RequestParam(required = false, defaultValue = "0") int page,
                                       @RequestParam(required = false, defaultValue = "asc") String direction,
                                       @RequestParam(required = false, defaultValue = "id") String sortField,
                                       Model model) {
        model.addAttribute("sortField", sortField);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Page<ArchivedTask> archivedTaskPage = archivedTaskService.getAllArchivedTasksPage(PageRequest.of(
                page, 50, sort));
        model.addAttribute("archivedTaskPage", archivedTaskPage);
        model.addAttribute("sortField", sortField);
        return "task/archivedTasks";
    }
}
