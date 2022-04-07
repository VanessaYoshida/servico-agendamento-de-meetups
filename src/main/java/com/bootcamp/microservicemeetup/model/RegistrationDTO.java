package com.bootcamp.microservicemeetup.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RegistrationDTO {

    private Integer id;

    @NotEmpty
    private String name;

    @NotNull
    private LocalDate dateOfRegistration;

    @NotEmpty
    private String registration;

}
