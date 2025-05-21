package com.creche.creche.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/kitchen")
@PreAuthorize("hasRole('KITCHEN')")
public class KitchenController {

    @GetMapping("/dashboard")
    public String kitchenDashboard(Model model) {
        return "kitchen/dashboard";
    }
}