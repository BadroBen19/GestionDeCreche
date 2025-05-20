package com.creche.creche.controller;

import com.creche.creche.dto.ChildDto;
import com.creche.creche.model.Child;
import com.creche.creche.model.User;
import com.creche.creche.service.ChildService;
import com.creche.creche.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/children")
public class ChildController {
    
    private final ChildService childService;
    private final UserService userService;
    
    @Autowired
    public ChildController(ChildService childService, UserService userService) {
        this.childService = childService;
        this.userService = userService;
    }
    
    @GetMapping("/list")
    public String listChildren(Model model) {
        model.addAttribute("children", childService.findAll());
        return "children/list";
    }
    
    @GetMapping("/register")
    public String registrationForm(Model model) {
        model.addAttribute("child", new ChildDto());
        model.addAttribute("parents", userService.findAll());
        return "children/register";
    }
    
    @PostMapping("/register")
    public String registerChild(@ModelAttribute("child") ChildDto childDto, @RequestParam Long parentId) {
        // Create child object
        Child child = new Child(
            childDto.getFirstName(),
            childDto.getLastName(),
            childDto.getBirthDate()
        );
        
        child.setMedicalNotes(childDto.getMedicalNotes());
        child.setDietaryRestrictions(childDto.getDietaryRestrictions());
        child.setAllergies(childDto.getAllergies());
        child.setSpecialNeeds(childDto.getSpecialNeeds());
        
        // Set enrollment status (default is WAITING_LIST)
        
        // Add parent
        Optional<User> parentOpt = userService.findById(parentId);
        if (parentOpt.isPresent()) {
            child.addParent(parentOpt.get());
        }
        
        childService.save(child);
        
        return "redirect:/children/list";
    }
    
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Child> childOpt = childService.findById(id);
        if (childOpt.isPresent()) {
            Child child = childOpt.get();
            
            ChildDto childDto = new ChildDto();
            childDto.setId(child.getId());
            childDto.setFirstName(child.getFirstName());
            childDto.setLastName(child.getLastName());
            childDto.setBirthDate(child.getBirthDate());
            childDto.setMedicalNotes(child.getMedicalNotes());
            childDto.setDietaryRestrictions(child.getDietaryRestrictions());
            childDto.setAllergies(child.getAllergies());
            childDto.setSpecialNeeds(child.getSpecialNeeds());
            childDto.setEnrollmentStatus(child.getEnrollmentStatus().name());
            
            model.addAttribute("child", childDto);
            model.addAttribute("parents", userService.findAll());
            return "children/edit";
        }
        return "redirect:/children/list";
    }
    
    @PostMapping("/edit")
    public String updateChild(@ModelAttribute("child") ChildDto childDto) {
        Optional<Child> childOpt = childService.findById(childDto.getId());
        if (childOpt.isPresent()) {
            Child child = childOpt.get();
            
            child.setFirstName(childDto.getFirstName());
            child.setLastName(childDto.getLastName());
            child.setBirthDate(childDto.getBirthDate());
            child.setMedicalNotes(childDto.getMedicalNotes());
            child.setDietaryRestrictions(childDto.getDietaryRestrictions());
            child.setAllergies(childDto.getAllergies());
            child.setSpecialNeeds(childDto.getSpecialNeeds());
            
            if (childDto.getEnrollmentStatus() != null) {
                child.setEnrollmentStatus(Child.EnrollmentStatus.valueOf(childDto.getEnrollmentStatus()));
            }
            
            childService.save(child);
        }
        return "redirect:/children/list";
    }
    
    @GetMapping("/waiting-list")
    public String waitingList(Model model) {
        model.addAttribute("waitingChildren", 
            childService.findByEnrollmentStatus(Child.EnrollmentStatus.WAITING_LIST));
        return "children/waiting-list";
    }
    
    @PostMapping("/{id}/enroll")
    public String enrollChild(@PathVariable Long id) {
        childService.updateEnrollmentStatus(id, Child.EnrollmentStatus.ENROLLED);
        return "redirect:/children/waiting-list";
    }
}