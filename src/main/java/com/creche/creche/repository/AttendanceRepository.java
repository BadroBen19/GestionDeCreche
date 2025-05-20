package com.creche.creche.repository;

import com.creche.creche.model.Attendance;
import com.creche.creche.model.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByDate(LocalDate date);
    List<Attendance> findByChildAndDateBetween(Child child, LocalDate startDate, LocalDate endDate);
    Optional<Attendance> findByChildAndDate(Child child, LocalDate date);
}