package com.rajesh.patient.hub.service;

import com.rajesh.patient.hub.dto.PatientDto;
import com.rajesh.patient.hub.exceptions.PatientDetailsNotAvailableException;

import java.util.List;

public interface PatientService {
    List<PatientDto> getPatients();

    void savePatient(PatientDto patient) throws Exception;

    PatientDto updatePatientDetails(PatientDto patientDto) throws Exception;

    PatientDto getPatientDetailsByEmail(String email) throws PatientDetailsNotAvailableException;

    PatientDto getPatientDetailsByMobile(String mobile) throws PatientDetailsNotAvailableException;


}
