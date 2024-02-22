package com.employee.employeeandworkordermanagement.controller;

import com.employee.employeeandworkordermanagement.data.Role;
import com.employee.employeeandworkordermanagement.dto.UserDTO;
import com.employee.employeeandworkordermanagement.service.UserService;
import com.employee.employeeandworkordermanagement.entity.User;
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
@RequestMapping("/designer")
public class DesignerController {
    private final UserService userService;

    @ModelAttribute("user")
    public UserDTO userDTO(Authentication authentication) {
        if (authentication != null) {
            return userService.getUserDTO(authentication);
        } else {
            return null;
        }
    }
    @ModelAttribute("adminOrOperator")
    public boolean isAdminOrOperator(Authentication authentication) {
        if (authentication != null) {
            User user = userService.findOptionalUserByEmail(authentication.getName()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User has not been found"));
            if (user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.OPERATOR)) {
                return true;
            } else return false;
        } else {
            return false;
        }
    }

    @GetMapping("/all-designers")
    public String showAllDesigners(@RequestParam(required = false, defaultValue = "0") int page,
                                   @RequestParam(required = false, defaultValue = "asc") String direction,
                                   @RequestParam(required = false, defaultValue = "id") String sortField,
                                   Model model) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Page<User> designerPage = userService.designerPage(PageRequest.of(page, 50, sort));
        model.addAttribute("designerPage", designerPage);
        model.addAttribute("sortField", sortField);
        return "designer/designerList";
    }
}
