package com.bootcamp.microservicemeetup.repository;

import com.bootcamp.microservicemeetup.model.entity.Meetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class MeetupRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    MeetupRepository repository;

    @Test
    @DisplayName("Should return true when exists an meetup already created.")
    public void returnTrueWhenMeetupExists() {
        Integer idMeetup = 1;
        Meetup meetup_Class_attribute = createNewMeetup();
        entityManager.persist(meetup_Class_attribute);

        boolean exists = repository.existsById(idMeetup);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when doesn't exists an meetup_attribute with a meetup already created.")
    public void returnFalseWhenMeetupAttributeDoesntExists() {
        Integer meetup = 222;

        boolean exists = repository.existsById(meetup);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should get an meetup by id")
    public void findByIdTest() {
        Meetup meetup_attribute = createNewMeetup();
        Pageable pageRequest = PageRequest.of(0,10);

        entityManager.persist(meetup_attribute);

        Page<Meetup> foundMeetup = repository.findByMeetup(meetup_attribute.getEvent(), pageRequest);

        assertThat(foundMeetup.getTotalElements());
    }

    public static Meetup createNewMeetup() {
        return Meetup.builder().event("Womakerscode Java").registrations(null).meetupDate("25/06/2022").registered(true).ownerId(111)
                .build();
    }
}
