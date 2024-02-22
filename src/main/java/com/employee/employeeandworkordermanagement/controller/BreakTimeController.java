package com.employee.employeeandworkordermanagement.controller;

import com.employee.employeeandworkordermanagement.entity.BreakTime;
import com.employee.employeeandworkordermanagement.entity.Task;
import com.employee.employeeandworkordermanagement.service.BreakTimeService;
import com.employee.employeeandworkordermanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/break-time")
public class BreakTimeController {
    private final BreakTimeService breakTimeService;
    private final TaskService taskService;

    @GetMapping("/start")
    public String handleStartBreakTime(@RequestParam(name = "id") Long id, Authentication authentication) {
        Task task = taskService.findById(id);
        breakTimeService.startBreakTime(task, authentication);
        return "redirect:/task/your-task";
    }

    @GetMapping("/stop")
    public String handleStopBreakTime(@RequestParam(name = "id") Long id, Authentication authentication) {
        Task task = taskService.findById(id);
        breakTimeService.stopBreakTime(task, authentication);
        return "redirect:/task/your-task";
    }

    @GetMapping("/break-time-list-for-user")
    public String showBreakTimesForUser(@RequestParam(required = false, defaultValue = "0") int page,
                                        @RequestParam(required = false, defaultValue = "asc") String direction,
                                        @RequestParam(required = false, defaultValue = "id") String sortField,
                                        Model model, Authentication authentication) {
        model.addAttribute("sortField", sortField);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Page<BreakTime> breakTimes = breakTimeService.getBreakTimesForUser(authentication, PageRequest.of(page, 50, sort));
        model.addAttribute("breakTimes", breakTimes);
        model.addAttribute("sortField", sortField);
        return "breakTime/breakTimeListForUser";
    }

    @GetMapping("/all-break-times")
    public String showAllBreakTimes(@RequestParam(required = false, defaultValue = "0") int page,
                                    @RequestParam(required = false, defaultValue = "asc") String direction,
                                    @RequestParam(required = false, defaultValue = "id") String sortField,
                                    Model model) {
        model.addAttribute("sortField", sortField);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Page<BreakTime> breakTimes = breakTimeService.getAllBreakTimesList(PageRequest.of(page, 50, sort));
        model.addAttribute("breakTimes", breakTimes);
        model.addAttribute("sortField", sortField);
        return "breakTime/allBreakTimeList";
    }

}
