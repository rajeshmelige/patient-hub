package com.rajesh.patient.hub.repository;

import com.rajesh.patient.hub.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {

    Optional<Patient> findByMobile(String mobile);

    Optional<Patient> findByEmail(String email);
}
