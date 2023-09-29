package com.rajesh.patient.hub.service.impl;

import com.rajesh.patient.hub.dto.AddressDto;
import com.rajesh.patient.hub.dto.MedicalHistoryDto;
import com.rajesh.patient.hub.dto.PatientDto;
import com.rajesh.patient.hub.entity.Address;
import com.rajesh.patient.hub.entity.MedicalHistory;
import com.rajesh.patient.hub.entity.Patient;
import com.rajesh.patient.hub.enums.Gender;
import com.rajesh.patient.hub.exceptions.PatientAlreadyExistsException;
import com.rajesh.patient.hub.exceptions.PatientDetailsNotAvailableException;
import com.rajesh.patient.hub.repository.PatientRepository;
import com.rajesh.patient.hub.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

@Service
public class PatientServiceImpl implements PatientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientServiceImpl.class);
    @Autowired
    private PatientRepository patientRepository;

    @CacheEvict(value="patientslist", allEntries = true)
    @Override
    public void savePatient(PatientDto patient) throws Exception {
        if(patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new PatientAlreadyExistsException(MessageFormatter.format(
                    "Patient with email : {} already exists", patient.getEmail()).getMessage());
        }
        if(patientRepository.findByMobile(patient.getMobile()).isPresent()) {
            throw new PatientAlreadyExistsException(MessageFormatter.format(
                    "Patient with mobile number : {} already exists", patient.getMobile()).getMessage());
        }
        Patient patientEntity = new Patient();
        patientEntity.setGuid(UUID.randomUUID());
        patientEntity.setName(patient.getName());
        patientEntity.setAge(patient.getAge());
        patientEntity.setGender(patient.getGender().name());
        patientEntity.setMobile(patient.getMobile());
        patientEntity.setEmail(patient.getEmail());

        Address address = new Address(patient.getAddress().getStreet(),
                patient.getAddress().getCity(),
                patient.getAddress().getPincode());
        address.setPatient(patientEntity);
        patientEntity.setAddress(address);

        List<MedicalHistoryDto> historiesDto = patient.getHistories();
        List<MedicalHistory> histories = new ArrayList<>();

        historiesDto.stream().forEach(history -> {
            MedicalHistory medicalHistory = new MedicalHistory();
            medicalHistory.setCondition(history.getCondition());
            medicalHistory.setPrescription(history.getPrescription());
            medicalHistory.setDoctor(history.getDoctor());
            medicalHistory.setConsultationDate(
                    history.getConsultationDate()!=null?history.getConsultationDate():new Date());
            medicalHistory.setPatient(patientEntity);
            histories.add(medicalHistory);
        });
        patientEntity.setHistories(histories);

        patientRepository.save(patientEntity);
    }

    @Caching(evict = {
            @CacheEvict(value = "patientslist", allEntries = true)
    },
    put = {

            @CachePut(value = "patients", key = "#patient.email"),
            @CachePut(value = "patients", key = "#patient.mobile")
    })
    @Override
    public PatientDto updatePatientDetails(PatientDto patient) throws Exception {
        Optional<Patient> patientOptional = patientRepository.findByEmail(patient.getEmail());
        if(!patientOptional.isPresent()) {
            patientOptional = patientRepository.findByMobile(patient.getMobile());
        }
        if(!patientOptional.isPresent()) {
            throw new PatientDetailsNotAvailableException("Patient with given email or mobile does not exist");
        }
        Patient patientEntity = patientOptional.get();
        patientEntity.setName(patient.getName());
        patientEntity.setAge(patient.getAge());
        patientEntity.setGender(patient.getGender().name());
        patientEntity.setMobile(patient.getMobile());
        patientEntity.setEmail(patient.getEmail());

        Address address = patientEntity.getAddress();
        address.setStreet(patient.getAddress().getStreet());
        address.setCity(patient.getAddress().getCity());
        address.setPincode(patient.getAddress().getPincode());

        patientEntity.setAddress(address);

        List<MedicalHistoryDto> historiesDto = patient.getHistories();
        List<MedicalHistory> histories = patientEntity.getHistories();

        historiesDto.stream().forEach(history -> {
            MedicalHistory medicalHistory = new MedicalHistory();
            medicalHistory.setCondition(history.getCondition());
            medicalHistory.setPrescription(history.getPrescription());
            medicalHistory.setDoctor(history.getDoctor());
            medicalHistory.setConsultationDate(
                    history.getConsultationDate()!=null?history.getConsultationDate():new Date());
            medicalHistory.setPatient(patientEntity);
            histories.add(medicalHistory);
        });
        patientEntity.setHistories(histories);
        patientRepository.save(patientEntity);

        return mapPatientEntitytoDto(patientEntity);
    }

    @Override
    @Cacheable(value="patientslist")
    public List<PatientDto> getPatients() {
        //Adding 1s delay to non cached requests
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            LOGGER.error("Caught thread Interrupted exception");
        }
        List<Patient> patients = patientRepository.findAll();
        Queue<Patient> pq = new LinkedList<>();
        List<PatientDto> patientDtos = new ArrayList<>();
        patients.stream().forEach(p -> {
            patientDtos.add(mapPatientEntitytoDto(p));
        });
        return patientDtos;
    }

    @Override
    @Cacheable(value="patients", key = "#email")
    public PatientDto getPatientDetailsByEmail(String email) throws PatientDetailsNotAvailableException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            LOGGER.error("Caught thread Interrupted exception");
        }
        Patient patient = patientRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new PatientDetailsNotAvailableException(
                            MessageFormatter.format("Patient with email : {} is not available", email).getMessage()));
        return mapPatientEntitytoDto(patient);
    }

    @Override
    @Cacheable(value="patients", key = "#mobile")
    public PatientDto getPatientDetailsByMobile(String mobile) throws PatientDetailsNotAvailableException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            LOGGER.error("Caught thread Interrupted exception");
        }
        Patient patient = patientRepository
                    .findByMobile(mobile)
                    .orElseThrow(() -> new PatientDetailsNotAvailableException(
                            MessageFormatter.format("Patient with mobile number : {} is not available", mobile.toString()).getMessage()));

        return mapPatientEntitytoDto(patient);
    }

    private void mapPatientDtoToEntity(PatientDto patientDto) {

    }

    private PatientDto mapPatientEntitytoDto(Patient patient) {
        PatientDto patientDto = new PatientDto();
        patientDto.setId(patient.getId());
        patientDto.setGuid(patient.getGuid());
        patientDto.setName(patient.getName());
        patientDto.setGender(Gender.valueOf(patient.getGender()));
        patientDto.setAge(patient.getAge());
        patientDto.setMobile(patient.getMobile());
        patientDto.setEmail(patient.getEmail());
        setAddressFromEntity(patientDto,patient);
        setMedicalHistoriesFromEntity(patientDto,patient);

        return patientDto;
    }
    private void setAddressFromEntity(PatientDto patientDto, Patient patient) {
        //Setting address to PatientDTO
        Address address = patient.getAddress();
        AddressDto addressDto = new AddressDto(address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getPincode());
        patientDto.setAddress(addressDto);
    }

    private void setMedicalHistoriesFromEntity(PatientDto patientDto, Patient patient) {
        //Setting medical histories to patientDTO
        List<MedicalHistoryDto> historiesDto = new ArrayList<>();
        patient.getHistories().stream().forEach(h -> {
            MedicalHistoryDto historyDto = new MedicalHistoryDto(h.getId(),
                    h.getCondition(),
                    h.getPrescription(),
                    h.getDoctor(),
                    h.getConsultationDate());
            historiesDto.add(historyDto);
        });
        patientDto.setHistories(historiesDto);
    }
}
