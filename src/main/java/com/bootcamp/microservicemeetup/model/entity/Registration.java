package com.bootcamp.microservicemeetup.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotEmpty;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Registration {
    @Id
    @Column(name = "registration_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //número da inscrição

    @Column(name = "person_name")
    private String name;

    @Column(name = "person_id")
    private String personId;

    @Column(name = "date_of_registration")
    private LocalDate dateOfRegistration;
    @ManyToOne
    private Meetup meetup;

}
