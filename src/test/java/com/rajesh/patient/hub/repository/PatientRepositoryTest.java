package com.rajesh.patient.hub.repository;

import com.rajesh.patient.hub.entity.Address;
import com.rajesh.patient.hub.entity.MedicalHistory;
import com.rajesh.patient.hub.entity.Patient;
import com.rajesh.patient.hub.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatientRepositoryTest {
    @Mock
    private PatientRepository patientRepository;

    private Patient patientEntity;

    @BeforeEach
    public void setup() {
        patientEntity = new Patient();
        patientEntity.setGuid(UUID.randomUUID());
        patientEntity.setName("John");
        patientEntity.setAge(50);
        patientEntity.setGender(Gender.MALE.name());
        patientEntity.setMobile("1234567890");
        patientEntity.setEmail("john@abc.com");

        Address address = new Address("abc",
                "blr",
                "560073");
        address.setPatient(patientEntity);
        patientEntity.setAddress(address);

        List<MedicalHistory> histories = new ArrayList<>();
        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setCondition("diseaseA");
        medicalHistory.setPrescription("prescA");
        medicalHistory.setDoctor("Dr.X");
        medicalHistory.setConsultationDate(new Date());
        medicalHistory.setPatient(patientEntity);
        histories.add(medicalHistory);
        patientEntity.setHistories(histories);
    }

    @Test
    public void testFindByMobile() {
        String mobile = "1234567890";
        when(patientRepository.findByMobile(mobile)).thenReturn(Optional.of(patientEntity));
        Optional<Patient> result = patientRepository.findByMobile(mobile);

        assertTrue(result.isPresent());
        assertEquals(mobile, result.get().getMobile());
    }

    @Test
    public void testFindByEmail() {
        String email = "john@abc.com";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patientEntity));
        Optional<Patient> result = patientRepository.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }
}
