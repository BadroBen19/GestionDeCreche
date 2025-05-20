package com.creche.creche.service;

import com.creche.creche.model.Attendance;
import com.creche.creche.model.Child;
import com.creche.creche.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    
    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }
    
    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }
    
    public List<Attendance> findByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }
    
    public Optional<Attendance> findById(Long id) {
        return attendanceRepository.findById(id);
    }
    
    public List<Attendance> findByChildAndDateRange(Child child, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByChildAndDateBetween(child, startDate, endDate);
    }
    
    public Attendance recordArrival(Child child, LocalDate date, LocalDateTime arrivalTime) {
        Optional<Attendance> existingAttendance = attendanceRepository.findByChildAndDate(child, date);
        
        if (existingAttendance.isPresent()) {
            Attendance attendance = existingAttendance.get();
            attendance.setArrivalTime(arrivalTime);
            return attendanceRepository.save(attendance);
        } else {
            Attendance newAttendance = new Attendance(child, date, arrivalTime);
            return attendanceRepository.save(newAttendance);
        }
    }
    
    public Attendance recordDeparture(Child child, LocalDate date, LocalDateTime departureTime) {
        Optional<Attendance> existingAttendance = attendanceRepository.findByChildAndDate(child, date);
        
        if (existingAttendance.isPresent()) {
            Attendance attendance = existingAttendance.get();
            attendance.setDepartureTime(departureTime);
            return attendanceRepository.save(attendance);
        }
        return null; // No arrival record found
    }
    
    public Attendance save(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }
    
    public void delete(Attendance attendance) {
        attendanceRepository.delete(attendance);
    }
    
    public void deleteById(Long id) {
        attendanceRepository.deleteById(id);
    }
}