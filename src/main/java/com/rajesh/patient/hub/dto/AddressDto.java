package com.rajesh.patient.hub.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto implements Serializable {
    private Integer id;
    private String street;
    private String city;
    @NotEmpty
    private String pincode;

}
