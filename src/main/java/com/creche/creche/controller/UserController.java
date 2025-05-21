package com.creche.creche.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.creche.creche.dto.UserRegistrationDto;
import com.creche.creche.model.User;
import com.creche.creche.repository.RoleRepository;
import com.creche.creche.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder , RoleRepository roleRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }
    
    @GetMapping("/register")
    public String registrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "users/register";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserRegistrationDto registrationDto) {
        // Check if passwords match
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            return "redirect:/users/register?error=passwordMismatch";
        }
        
        // Check if email already exists
        if (userService.existsByEmail(registrationDto.getEmail())) {
            return "redirect:/users/register?error=emailExists";
        }
        
        // Create and save user
        User user = new User(
            registrationDto.getFirstName(),
            registrationDto.getLastName(),
            registrationDto.getEmail(),
            passwordEncoder.encode(registrationDto.getPassword()), // Encoder le mot de passe
            registrationDto.getPhoneNumber()
        );
        
        userService.createUser(user, registrationDto.getRole());
        
        return "redirect:/login?success";
    }
    
    @GetMapping("/list")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }
    @PostMapping("/toggle-status/{id}")
public String toggleUserStatus(@PathVariable Long id) {
    Optional<User> userOpt = userService.findById(id);
    if (userOpt.isPresent()) {
        User user = userOpt.get();
        user.setActive(!user.isActive());
        userService.save(user);
    }
    return "redirect:/users/list";
}
@GetMapping("/edit/{id}")
public String showEditForm(@PathVariable Long id, Model model) {
    Optional<User> userOpt = userService.findById(id);
    if (userOpt.isPresent()) {
        model.addAttribute("user", userOpt.get());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "users/edit";
    }
    return "redirect:/users/list";
}
}