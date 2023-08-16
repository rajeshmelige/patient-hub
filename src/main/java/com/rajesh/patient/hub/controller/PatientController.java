package com.rajesh.patient.hub.controller;

import com.rajesh.patient.hub.dto.PatientDto;
import com.rajesh.patient.hub.service.PatientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/patient")
public class PatientController {
    private static final String PATIENT_SAVED_MSG = "patient details saved successfully";
    private static final String PATIENT_UPDATED_MSG = "patient details updated successfully";

    @Autowired
    private PatientService patientService;

    @PostMapping
    public ResponseEntity savePatient(@Valid @RequestBody PatientDto patient) throws Exception {
        patientService.savePatient(patient);
        return ResponseEntity.status(HttpStatus.OK).body(PATIENT_SAVED_MSG);
    }

    @GetMapping("/list")
    public ResponseEntity getPatients() {
        List<PatientDto> patients = patientService.getPatients();
        return ResponseEntity.status(HttpStatus.OK).body(patients);
    }

    @GetMapping("/{mobile}")
    public ResponseEntity getPatientByMobile(@PathVariable("mobile") @Size(min = 10, max = 10) String mobile) throws Exception {
        PatientDto patient = patientService.getPatientDetailsByMobile(mobile);
        return ResponseEntity.status(HttpStatus.OK).body(patient);
    }

    @GetMapping
    public ResponseEntity getPatientByEmail(@RequestParam @Email String email) throws Exception {
        PatientDto patient = patientService.getPatientDetailsByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(patient);
    }

    @PutMapping
    public ResponseEntity updatePatientDetails(@Valid @RequestBody PatientDto patientDto) throws Exception {
        patientService.updatePatientDetails(patientDto);
        return ResponseEntity.status(HttpStatus.OK).body(PATIENT_UPDATED_MSG);
    }
}
