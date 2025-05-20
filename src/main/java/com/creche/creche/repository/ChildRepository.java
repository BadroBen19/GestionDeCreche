// ChildRepository.java
package com.creche.creche.repository;

import com.creche.creche.model.Child;
import com.creche.creche.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    List<Child> findByParentsContaining(User parent);
    List<Child> findByEnrollmentStatus(Child.EnrollmentStatus status);
}