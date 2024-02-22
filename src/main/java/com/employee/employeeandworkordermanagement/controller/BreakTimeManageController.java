package com.employee.employeeandworkordermanagement.controller;

import com.employee.employeeandworkordermanagement.entity.BreakTime;
import com.employee.employeeandworkordermanagement.entity.User;
import com.employee.employeeandworkordermanagement.service.BreakTimeService;
import com.employee.employeeandworkordermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/break-time-manage")
public class BreakTimeManageController {
    private final BreakTimeService breakTimeService;
    private final UserService userService;

    @GetMapping("/break-user-anomalies")
    public String findAllBreakAnomaliesForUser(@RequestParam(name = "id") Long id,
                                               @RequestParam(required = false, defaultValue = "0") int page,
                                               @RequestParam(required = false, defaultValue = "asc") String direction,
                                               @RequestParam(required = false, defaultValue = "id") String sortField,
                                               Model model) {
        User user = userService.findById(id);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Page<BreakTime> anomalyPage = breakTimeService.findAnomalousBreakTimes(user, PageRequest.of(page,
                50, sort));
        model.addAttribute("anomalyPage", anomalyPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("id", id);
        return "breakTime/breakTimeAnomaliesForOperator";
    }
}
