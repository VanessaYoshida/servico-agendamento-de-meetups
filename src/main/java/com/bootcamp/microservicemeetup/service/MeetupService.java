package com.bootcamp.microservicemeetup.service;

import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MeetupService {

  Meetup save(Meetup meetup);

  Optional<Meetup> getById(Integer id);

  Meetup update(Meetup loan);

  void delete(Meetup meetup);

  Page<Meetup> getRegistrationsByMeetup(Meetup meetup, Pageable pageable);

  //  Page<Meetup> findByPersonIdOnMeetup(MeetupFilterDTO filterDTO, Pageable pageable);

}
