package com.employee.employeeandworkordermanagement.controller;

import com.employee.employeeandworkordermanagement.entity.BreakTimeIssueRequest;
import com.employee.employeeandworkordermanagement.service.BreakTimeIssueRequestService;
import com.employee.employeeandworkordermanagement.service.BreakTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manage-time-issue")
public class TimeIssueRequestManageController {
    private final BreakTimeIssueRequestService breakTimeIssueRequestService;
    private final BreakTimeService breakTimeService;

    @GetMapping("/handle-accidentally-started-break")
    public String handleAccidentallyStartedBreak(@RequestParam(name = "id") Long id) {
        BreakTimeIssueRequest breakTimeIssueRequest = breakTimeIssueRequestService.findById(id);
        breakTimeService.handleAccidentallyStartedBreak(breakTimeIssueRequest.getBreakTime());
        return "timeIssue/handleSuccessful";
    }
}
