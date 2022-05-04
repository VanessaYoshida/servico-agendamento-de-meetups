//package com.bootcamp.microservicemeetup.repository;
//
//import com.bootcamp.microservicemeetup.model.entity.Meetup;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@ExtendWith(SpringExtension.class)
//@ActiveProfiles("test")
//@DataJpaTest
//public class MeetupRepositoryTest {
//
//    @Autowired
//    TestEntityManager entityManager;
//
//    @Autowired
//    MeetupRepository repository;
//
//    @Test
//    @DisplayName("Should return true when exists an meetup already created.")
//    public void returnTrueWhenMeetupExists() {
//        Integer idMeetup = 14;
//        Meetup meetup_Class_attribute = createNewMeetup(idMeetup);
//        entityManager.persist(meetup_Class_attribute);
//
//        boolean exists = repository.existsById(idMeetup);
//
//        assertThat(exists).isTrue();
//    }
//    @Test
//    @DisplayName("Should return false when doesn't exists an meetup_attribute with a meetup already created.")
//    public void returnFalseWhenMeetupAttributeDoesntExists() {
//        Integer meetup = 222;
//
//        boolean exists = repository.existsById(meetup);
//
//        assertThat(exists).isFalse();
//    }
//
//    @Test
//    @DisplayName("Should get an meetup by id")
//    public void findByIdTest() {
//        Integer idMeetup = 14;
//        Meetup meetup_attribute = createNewMeetup(idMeetup);
//        entityManager.persist(meetup_attribute);
//
//        Optional<Meetup> foundMeetup = repository.findById(meetup_attribute.getId());
//
//        assertThat(foundMeetup.isPresent()).isTrue();
//    }
//
//    @Test
//    @DisplayName("Should save an meetup")
//    public void saveMeetupTest(){
//        Integer idMeetup = 14;
//        Meetup meetup_attribute = createNewMeetup(idMeetup);
//        Meetup savedMeetup = repository.save(meetup_attribute);
//
//        assertThat(savedMeetup.getId()).isNotNull();
//    }
//
//    @Test
//    @DisplayName("Should delete and meetup from the base")
//    public void deleteRegistation() {
//        Integer idMeetup = 14;
//        Meetup meetup_attribute = createNewMeetup(idMeetup);
//        entityManager.persist(meetup_attribute);
//
//        Meetup foundMeetup = entityManager
//                .find(Meetup.class, meetup_attribute.getId());
//        repository.delete(foundMeetup);
//
//        Meetup deleteMeetup = entityManager
//                .find(Meetup.class, meetup_attribute.getId());
//
//        assertThat(deleteMeetup).isNull();
//
//    }
//
//    public static Meetup createNewMeetup(Integer idMeetup) {
//        return Meetup.builder().id(idMeetup).event("Womakerscode Java").registrations(null).meetupDate("25/06/2022").registered(true).ownerId(111)
//                .build();
//    }
//}
