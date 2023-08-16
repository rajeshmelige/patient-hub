package com.rajesh.patient.hub.dto;

import com.rajesh.patient.hub.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDto implements Serializable {

    private Integer id;
    private UUID guid;
    @NotNull
    private String name;
    private Integer age;
    private Gender gender;
    @Email
    private String email;
    @Size(min = 10,max = 10)
    private String mobile;
    @Valid
    private AddressDto address;
    private List<MedicalHistoryDto> histories;
}
