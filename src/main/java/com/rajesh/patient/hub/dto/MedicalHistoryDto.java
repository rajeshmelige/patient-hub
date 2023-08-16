package com.rajesh.patient.hub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalHistoryDto implements Serializable {

    private Integer id;
    private String condition;
    private String prescription;
    private String doctor;
    private Date consultationDate;
}
