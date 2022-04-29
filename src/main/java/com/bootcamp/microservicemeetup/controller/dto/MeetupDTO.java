package com.bootcamp.microservicemeetup.controller.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetupDTO {

  private String event;

  private String date;

  private Integer ownerId;

  private List<RegistrationDTO> registrations;
}
