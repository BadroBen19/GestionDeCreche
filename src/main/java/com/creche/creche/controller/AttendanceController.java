package com.creche.creche.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.creche.creche.model.Child;
import com.creche.creche.service.AttendanceService;
import com.creche.creche.service.ChildService;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    private final ChildService childService;
    
    @Autowired
    public AttendanceController(AttendanceService attendanceService, ChildService childService) {
        this.attendanceService = attendanceService;
        this.childService = childService;
    }
    
    @GetMapping("/daily")
    public String dailyAttendance(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        model.addAttribute("date", date);
        model.addAttribute("attendances", attendanceService.findByDate(date));
        model.addAttribute("enrolledChildren", childService.findByEnrollmentStatus(Child.EnrollmentStatus.ENROLLED));
        
        return "attendance/daily";
    }
    
    @PostMapping("/record-arrival")
    public String recordArrival(@RequestParam Long childId, 
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Optional<Child> childOpt = childService.findById(childId);
        if (childOpt.isPresent()) {
            attendanceService.recordArrival(childOpt.get(), date, LocalDateTime.now());
        }
        
        return "redirect:/attendance/daily?date=" + date;
    }
    
    @PostMapping("/record-departure")
    public String recordDeparture(@RequestParam Long childId, 
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        Optional<Child> childOpt = childService.findById(childId);
        if (childOpt.isPresent()) {
            attendanceService.recordDeparture(childOpt.get(), date, LocalDateTime.now());
        }
        
        return "redirect:/attendance/daily?date=" + date;
    }
    
    @GetMapping("/report")
    public String attendanceReport(
            @RequestParam(required = false) Long childId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        model.addAttribute("children", childService.findAll());
        
        if (childId != null && startDate != null && endDate != null) {
            Optional<Child> childOpt = childService.findById(childId);
            if (childOpt.isPresent()) {
                model.addAttribute("selectedChild", childOpt.get());
                model.addAttribute("startDate", startDate);
                model.addAttribute("endDate", endDate);
                model.addAttribute("attendances", 
                    attendanceService.findByChildAndDateRange(childOpt.get(), startDate, endDate));
            }
        }
        
        return "attendance/report";
    }
}