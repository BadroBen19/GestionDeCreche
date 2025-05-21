package com.creche.creche.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.creche.creche.model.Role;
import com.creche.creche.model.User;
import com.creche.creche.repository.RoleRepository;
import com.creche.creche.service.UserService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    
    @Autowired
    public UserAdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }
    
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        List<Role> allRoles = roleRepository.findAll();
        
        model.addAttribute("users", users);
        model.addAttribute("allRoles", allRoles);
        return "admin/users";
    }
    
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Add any admin-specific dashboard data
        return "admin/dashboard";
    }
    
    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(!user.isActive());
            userService.save(user);
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{id}/update-role")
    public String updateUserRoles(@PathVariable Long id, @RequestParam List<String> roles) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.getRoles().clear();
            
            for (String roleName : roles) {
                roleRepository.findByName(roleName).ifPresent(user::addRole);
            }
            
            userService.save(user);
        }
        return "redirect:/admin/users";
    }
}