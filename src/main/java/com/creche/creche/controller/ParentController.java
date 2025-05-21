package com.creche.creche.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.creche.creche.model.User;
import com.creche.creche.service.AttendanceService;
import com.creche.creche.service.ChildService;
import com.creche.creche.service.UserService;

@Controller
@RequestMapping("/parent")
@PreAuthorize("hasRole('PARENT')")
public class ParentController {

    private final UserService userService;
    private final ChildService childService;
    private final AttendanceService attendanceService;
    
    @Autowired
    public ParentController(UserService userService, ChildService childService, AttendanceService attendanceService) {
        this.userService = userService;
        this.childService = childService;
        this.attendanceService = attendanceService;
    }
    
    @GetMapping("/dashboard")
    public String parentDashboard(Model model, Authentication authentication) {
        String email = authentication.getName();
        User parent = userService.findByEmail(email).orElseThrow();
        
        model.addAttribute("children", childService.findByParent(parent));
        return "parent/dashboard";
    }
    
    @GetMapping("/children/{id}/attendance")
    public String childAttendance(@PathVariable Long id, Model model, Authentication authentication) {
        String email = authentication.getName();
        User parent = userService.findByEmail(email).orElseThrow();
        
        // Vérifier que l'enfant appartient bien au parent connecté
        var child = childService.findById(id)
            .filter(c -> c.getParents().contains(parent))
            .orElseThrow(() -> new RuntimeException("Accès non autorisé"));
        
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        
        model.addAttribute("child", child);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("attendances", 
            attendanceService.findByChildAndDateRange(child, startDate, endDate));
        
        return "parent/child-attendance";
    }
}