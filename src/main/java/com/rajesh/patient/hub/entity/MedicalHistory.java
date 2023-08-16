package com.rajesh.patient.hub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="medical_history")
public class MedicalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String condition;
    private String prescription;
    private String doctor;
    private Date consultationDate;
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
}
