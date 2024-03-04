package com.employee.employeeandworkordermanagement.controller;

import com.employee.employeeandworkordermanagement.entity.BreakTimeIssueRequest;
import com.employee.employeeandworkordermanagement.service.BreakTimeIssueRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/time-issue")
public class TimeIssueRequestController {
    private final BreakTimeIssueRequestService breakTimeIssueRequestService;

    @GetMapping("/create")
    private String createRequest(@RequestParam(name = "id") Long id, Model model,
                                 BreakTimeIssueRequest breakTimeIssueRequest) {
        List<String> typeList = List.of(new String("Accidentally added break time."),
                new String("Forgot to stop break time."), new String("Forgot to start break time."));
        model.addAttribute("typeList", typeList);
        model.addAttribute("breakTimeIssueRequest", breakTimeIssueRequest);
        model.addAttribute("id", id);
        return "timeIssue/breakTimeIssueRequest";
    }

    @PostMapping("/create")
    public String handleBreakTimeIssue(@RequestParam(name = "id") Long id,
                                       BreakTimeIssueRequest breakTimeIssueRequest, Authentication authentication) {
        breakTimeIssueRequestService.createBreakTimeIssueRequest(id, breakTimeIssueRequest, authentication);
        return "redirect:/break-time/break-anomalies";
    }

}
