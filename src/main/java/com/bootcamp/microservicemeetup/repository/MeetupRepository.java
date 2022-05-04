package com.bootcamp.microservicemeetup.repository;

import com.bootcamp.microservicemeetup.model.entity.Meetup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetupRepository extends JpaRepository<Meetup, Integer> {

  @Query( value = " select l from Meetup as l " +
          "join l.registrations as b " +
          "where b.personId = :personId " +
          "and l.event = :event ")
  Page<Meetup>findByPersonIdOnMeetup(
          @Param("personId") String personId,
          @Param("event") String event,
          Pageable pageable
  );

  @Query ( value = " select l from Meetup as l " +
          "join l.registrations as b " +
          "where l.event = :event ")
  Page<Meetup> findByMeetup(
          @Param("event") String event,
          Pageable pageable
  );

  boolean existsById(Integer id);
}
