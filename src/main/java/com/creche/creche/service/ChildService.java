package com.creche.creche.service;

import com.creche.creche.model.Child;
import com.creche.creche.model.User;
import com.creche.creche.repository.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChildService {
    
    private final ChildRepository childRepository;
    
    @Autowired
    public ChildService(ChildRepository childRepository) {
        this.childRepository = childRepository;
    }
    
    public List<Child> findAll() {
        return childRepository.findAll();
    }
    
    public Optional<Child> findById(Long id) {
        return childRepository.findById(id);
    }
    
    public List<Child> findByParent(User parent) {
        return childRepository.findByParentsContaining(parent);
    }
    
    public List<Child> findByEnrollmentStatus(Child.EnrollmentStatus status) {
        return childRepository.findByEnrollmentStatus(status);
    }
    
    public Child save(Child child) {
        return childRepository.save(child);
    }
    
    public void delete(Child child) {
        childRepository.delete(child);
    }
    
    public void deleteById(Long id) {
        childRepository.deleteById(id);
    }
    
    public Child updateEnrollmentStatus(Long childId, Child.EnrollmentStatus status) {
        Optional<Child> childOpt = childRepository.findById(childId);
        if (childOpt.isPresent()) {
            Child child = childOpt.get();
            child.setEnrollmentStatus(status);
            return childRepository.save(child);
        }
        return null;
    }
}