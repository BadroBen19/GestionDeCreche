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
            model.addAttribute("child", child);
            model.addAttribute("availableParents", userService.findAll().stream()
                .filter(user -> user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_PARENT")))
                .toList());
            return "children/edit";
        }
        return "redirect:/children/list";
    }
    
    @PostMapping("/edit/{id}")
    public String updateChild(@PathVariable Long id, @ModelAttribute Child child) {
        Optional<Child> existingChildOpt = childService.findById(id);
        if (existingChildOpt.isPresent()) {
            Child existingChild = existingChildOpt.get();
            
            existingChild.setFirstName(child.getFirstName());
            existingChild.setLastName(child.getLastName());
            existingChild.setBirthDate(child.getBirthDate());
            existingChild.setMedicalNotes(child.getMedicalNotes());
            existingChild.setDietaryRestrictions(child.getDietaryRestrictions());
            existingChild.setAllergies(child.getAllergies());
            existingChild.setSpecialNeeds(child.getSpecialNeeds());
            existingChild.setEnrollmentStatus(child.getEnrollmentStatus());
            
            childService.save(existingChild);
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
        Optional<Child> childOpt = childService.findById(id);
        if (childOpt.isPresent()) {
            Child child = childOpt.get();
            child.setEnrollmentStatus(Child.EnrollmentStatus.ENROLLED);
            childService.save(child);
        }
        return "redirect:/children/waiting-list";
    }
    
    @GetMapping("/{id}/removeParent/{parentId}")
    public String removeParent(@PathVariable Long id, @PathVariable Long parentId) {
        Optional<Child> childOpt = childService.findById(id);
        Optional<User> parentOpt = userService.findById(parentId);
        
        if (childOpt.isPresent() && parentOpt.isPresent()) {
            Child child = childOpt.get();
            User parent = parentOpt.get();
            child.getParents().remove(parent);
            childService.save(child);
        }
        
        return "redirect:/children/edit/" + id;
    }
    
    @PostMapping("/addParent")
    public String addParent(@RequestParam Long id, @RequestParam Long parentId) {
        Optional<Child> childOpt = childService.findById(id);
        Optional<User> parentOpt = userService.findById(parentId);
        
        if (childOpt.isPresent() && parentOpt.isPresent()) {
            Child child = childOpt.get();
            User parent = parentOpt.get();
            child.getParents().add(parent);
            childService.save(child);
        }
        
        return "redirect:/children/edit/" + id;
    }
}