package com.employee.employeeandworkordermanagement.controller;

import com.employee.employeeandworkordermanagement.data.Role;
import com.employee.employeeandworkordermanagement.dto.UserDTO;
import com.employee.employeeandworkordermanagement.service.UserService;
import com.employee.employeeandworkordermanagement.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    @ModelAttribute("user")
    public UserDTO userDTO(Authentication authentication) {
        if (authentication != null) {
            return userService.getUserDTO(authentication);
        } else {
            return null;
        }
    }

    @GetMapping("/panel")
    public String showAdminPanel() {
        return "admin/adminPanel";
    }

    @GetMapping("/all-users")
    public String showAllUsers(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            @RequestParam(required = false, defaultValue = "id") String sortField,
            Model model) {
        model.addAttribute("sortField", sortField);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Page<User> userPage = userService.getAllUsers(PageRequest.of(page, 50,sort));
        model.addAttribute("userPage", userPage);
        model.addAttribute("sortField", sortField);
        return "admin/allUsers";
    }

    @PostMapping("/change-role/{id}")
    public String changeRoleHandler(@PathVariable(name = "id") long id, @RequestParam Role role) {
        User theUser = userService.findById(id);
        theUser.setRole(role);
        userService.editUserRole(theUser, role);
        return "redirect:/admin/allUsers";
    }
}
