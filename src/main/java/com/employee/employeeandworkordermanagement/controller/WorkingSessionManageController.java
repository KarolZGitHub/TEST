package com.employee.employeeandworkordermanagement.controller;

import com.employee.employeeandworkordermanagement.dto.UserDTO;
import com.employee.employeeandworkordermanagement.entity.User;
import com.employee.employeeandworkordermanagement.entity.WorkingSession;
import com.employee.employeeandworkordermanagement.service.UserService;
import com.employee.employeeandworkordermanagement.service.WorkingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/work-manage")
public class WorkingSessionManageController {
    private final UserService userService;
    private final WorkingSessionService workingSessionService;

    @ModelAttribute("user")
    public UserDTO userDTO(Authentication authentication) {
        if (authentication != null) {
            return userService.getUserDTO(authentication);
        } else {
            return null;
        }
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

    @GetMapping("/anomalies")
    public String showAllDesigners(@RequestParam(required = false, defaultValue = "0") int page,
                                   @RequestParam(required = false, defaultValue = "asc") String direction,
                                   @RequestParam(required = false, defaultValue = "id") String sortField,
                                   Model model) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Page<User> designerPage = userService.designerPage(PageRequest.of(page, 50, sort));
        model.addAttribute("designerPage", designerPage);
        return "workingSession/anomalies";
    }

    @GetMapping("/check-anomalies")
    public String showAnomaly(@RequestParam(required = false, defaultValue = "0") Long id,
                              @RequestParam(required = false, defaultValue = "0") int page,
                              @RequestParam(required = false, defaultValue = "asc") String direction,
                              @RequestParam(required = false, defaultValue = "id") String sortField,
                              Model model) {
        User user = userService.findById(id);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Page<WorkingSession> anomalyPage = workingSessionService.findAnomalousWorkingSessions(user, PageRequest.of(page,
                50, sort));
        model.addAttribute("designerPage", anomalyPage);
        return "workingSession/userAnomaly";
    }

    @GetMapping("/user-anomalies")
    public String showAnomaly(@RequestParam(name = "id") Long id,
                              @RequestParam(required = false, defaultValue = "0") int page,
                              @RequestParam(required = false, defaultValue = "asc") String direction,
                              @RequestParam(required = false, defaultValue = "id") String sortField,
                              Model model, Authentication authentication) {
        User user = userService.findById(id);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Page<WorkingSession> anomalyPage = workingSessionService.findAnomalousWorkingSessions(user, PageRequest.of(page,
                50, sort));
        model.addAttribute("anomalyPage", anomalyPage);
        model.addAttribute("id", id);
        return "workingSession/userAnomalyForOperator";
    }
}
