package com.employee.employeeandworkordermanagement.controller;

import com.employee.employeeandworkordermanagement.entity.BreakTimeIssueRequest;
import com.employee.employeeandworkordermanagement.entity.User;
import com.employee.employeeandworkordermanagement.service.BreakTimeIssueRequestService;
import com.employee.employeeandworkordermanagement.service.BreakTimeService;
import com.employee.employeeandworkordermanagement.service.MessageService;
import com.employee.employeeandworkordermanagement.service.UserService;
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
@RequestMapping("/manage-time-issue")
public class TimeIssueRequestManageController {
    private final BreakTimeIssueRequestService breakTimeIssueRequestService;
    private final BreakTimeService breakTimeService;
    private final MessageService messageService;
    private final UserService userService;

    @GetMapping("/accept-break-time-request")
    public String acceptBreakTimeRequest(@RequestParam(name = "id") Long id, Authentication authentication) {
        BreakTimeIssueRequest breakTimeIssueRequest = breakTimeIssueRequestService.findById(id);
        User sender = userService.findUserByEmail(authentication.getName());
        User designer = breakTimeIssueRequest.getBreakTime().getUser();
        breakTimeService.acceptBreakTimeIssueRequest(breakTimeIssueRequest);
        messageService.notifyDesignerThatBreakTimeRequestAccepted(designer, sender);
        return "timeIssue/handleSuccessful";
    }

    @GetMapping("/decline-break-time-request")
    public String handleAccidentallyStartedBreak(@RequestParam(name = "id") Long id, Authentication authentication) {
        BreakTimeIssueRequest breakTimeIssueRequest = breakTimeIssueRequestService.findById(id);
        User sender = userService.findUserByEmail(authentication.getName());
        User designer = breakTimeIssueRequest.getBreakTime().getUser();
        breakTimeService.declineBreakTimeIssueRequest(breakTimeIssueRequest);
        messageService.notifyDesignerThatBreakTimeRequestDeclined(designer, sender);
        return "timeIssue/handleSuccessful";
    }

    @GetMapping("/break-time-requests")
    public String showAllBreakTimeRequests(@RequestParam(required = false, defaultValue = "0") int page,
                                           @RequestParam(required = false, defaultValue = "asc") String direction,
                                           @RequestParam(required = false, defaultValue = "id") String sortField,
                                           Model model) {
        model.addAttribute("sortField", sortField);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Page<BreakTimeIssueRequest> requestsPage = breakTimeIssueRequestService.allRequests(PageRequest.of(page, 50, sort));
        model.addAttribute("requestsPage", requestsPage);
        return "timeIssue/breakTimeRequests";
    }
}
