package com.rajesh.patient.hub.service;

import com.rajesh.patient.hub.dto.AddressDto;
import com.rajesh.patient.hub.dto.MedicalHistoryDto;
import com.rajesh.patient.hub.dto.PatientDto;
import com.rajesh.patient.hub.entity.Patient;
import com.rajesh.patient.hub.enums.Gender;
import com.rajesh.patient.hub.exceptions.PatientAlreadyExistsException;
import com.rajesh.patient.hub.exceptions.PatientDetailsNotAvailableException;
import com.rajesh.patient.hub.repository.PatientRepository;
import com.rajesh.patient.hub.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private PatientDto patientDto;

    @BeforeEach
    public void setup() {
        patientDto = new PatientDto();
        patientDto.setName("John");
        patientDto.setAge(50);
        patientDto.setGender(Gender.MALE);
        patientDto.setMobile("1234567890");
        patientDto.setEmail("john@abc.com");

        AddressDto address = new AddressDto(1,"abc",
                "blr",
                "560073");
        patientDto.setAddress(address);

        List<MedicalHistoryDto> histories = new ArrayList<>();
        MedicalHistoryDto medicalHistory = new MedicalHistoryDto();
        medicalHistory.setCondition("diseaseA");
        medicalHistory.setPrescription("prescA");
        medicalHistory.setDoctor("Dr.X");
        medicalHistory.setConsultationDate(new Date());
        histories.add(medicalHistory);
        patientDto.setHistories(histories);
    }
    @Test
    void testSavePatientDetails() throws Exception {
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when((patientRepository.findByMobile(anyString()))).thenReturn(Optional.empty());
        patientService.savePatient(patientDto);

        ArgumentCaptor<Patient> patientEntityCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository, times(1)).save(patientEntityCaptor.capture());

        assertEquals("john@abc.com", patientEntityCaptor.getValue().getEmail());
        assertEquals("Dr.X", patientEntityCaptor.getValue().getHistories().get(0).getDoctor());
    }

    @Test
    void testSavePatientDetails_throwsPatientAlreadyExists() throws Exception {
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.of(new Patient()));

        assertThrows(PatientAlreadyExistsException.class, () -> patientService.savePatient(patientDto));
    }

    @Test
    void testGetPatientDetailsByMobile_throwsPatientNotExists() {
        when(patientRepository.findByMobile(anyString())).thenReturn(Optional.empty());
        assertThrows(PatientDetailsNotAvailableException.class, () -> patientService.getPatientDetailsByMobile(anyString()));
        verify(patientRepository, times(1)).findByMobile(anyString());
    }

}
