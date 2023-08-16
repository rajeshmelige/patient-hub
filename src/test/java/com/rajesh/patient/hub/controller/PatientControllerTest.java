package com.rajesh.patient.hub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rajesh.patient.hub.dto.AddressDto;
import com.rajesh.patient.hub.dto.MedicalHistoryDto;
import com.rajesh.patient.hub.dto.PatientDto;
import com.rajesh.patient.hub.enums.Gender;
import com.rajesh.patient.hub.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PatientController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class PatientControllerTest {

    @MockBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

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
    void testSave_orUpdate_PatientDetails() throws Exception {
        mockMvc.perform(post("/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patientDto)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patientDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<PatientDto> captor = ArgumentCaptor.forClass(PatientDto.class);
        verify(patientService, times(1)).savePatient(captor.capture());
        assertEquals(captor.getValue().getEmail(),("john@abc.com"));
        assertEquals(captor.getValue().getAddress().getCity(),("blr"));
        assertNotEquals(captor.getValue().getHistories().get(0).getCondition(),("diseaseB"));
    }

    @Test
    void testSavePatientDetails_withInvalidInputEmail() throws Exception {
        PatientDto patientDto1 = patientDto;
        patientDto1.setEmail("abcd");
        mockMvc.perform(post("/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patientDto1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSavePatientDetails_withEmptyPincode() throws Exception {
        PatientDto patientDto1 = patientDto;
        patientDto1.getAddress().setPincode(null);
        mockMvc.perform(post("/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patientDto1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPatientDetailsByMobile() throws Exception {
        mockMvc.perform(get("/patient/1234567890"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPatientDetailsByMobile_withInvalidMobileNumber() throws Exception {
        mockMvc.perform(get("/patient/12345678"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPatientDetailsByEmail() throws Exception {
        mockMvc.perform(get("/patient")
                .param("email", "abc@gmail.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPatientDetailsByEmail_withInvalidEmail() throws Exception {
        mockMvc.perform(get("/patient")
                        .param("email", "abc.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPatientsList() throws Exception {
        mockMvc.perform(get("/patient/list"))
                .andExpect(status().isOk());
    }


}
