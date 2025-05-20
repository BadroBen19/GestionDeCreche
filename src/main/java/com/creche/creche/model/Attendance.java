package com.creche.creche.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
public class Attendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column
    private LocalDateTime arrivalTime;
    
    @Column
    private LocalDateTime departureTime;
    
    @Column
    private String notes;
    
    // Constructeurs
    public Attendance() {}
    
    public Attendance(Child child, LocalDate date) {
        this.child = child;
        this.date = date;
    }
    
    public Attendance(Child child, LocalDate date, LocalDateTime arrivalTime) {
        this.child = child;
        this.date = date;
        this.arrivalTime = arrivalTime;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", childId=" + (child != null ? child.getId() : null) +
                ", date=" + date +
                ", arrivalTime=" + arrivalTime +
                ", departureTime=" + departureTime +
                '}';
    }
}