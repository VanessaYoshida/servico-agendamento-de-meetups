package com.bootcamp.microservicemeetup.repository;

import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetupRepository extends JpaRepository<Meetup, Integer> {

  @Query( value = " select l from Meetup as l " +
          "join l.registrations as b " +
          "where b.personId = :personId " +
          "or l.event = :event ")
  Page<Meetup>findByPersonIdOnMeetup(
          @Param("personId") String personId,
          @Param("event") String event,
          Pageable pageable
  );

  Page<Meetup> findByRegistrations(Registration registration, Pageable pageable );
}
