package com.bootcamp.microservicemeetup.service;

import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.repository.MeetupRepository;
import com.bootcamp.microservicemeetup.service.impl.MeetupServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MeetupServiceTest {

    MeetupService meetupService;

    @MockBean
    MeetupRepository repository;

    @BeforeEach
    public void setUp() {
        this.meetupService = new MeetupServiceImpl(repository);
    }

    @Test
    @DisplayName("Should save an meetup")
    public void saveMeetup() {
        Meetup meetup = createNewMeetup();

        Mockito.when(repository.existsById(any(Integer.class))).thenReturn(false);
        Mockito.when(repository.save(meetup)).thenReturn(createNewMeetup());

        Meetup savedMeetup = meetupService.save(meetup);

        assertThat(savedMeetup.getId()).isEqualTo(101);
        assertThat(savedMeetup.getEvent()).isEqualTo("Womakerscode Java");
        assertThat(savedMeetup.getMeetupDate()).isEqualTo("25/06/2022");
        assertThat(savedMeetup.getRegistered()).isEqualTo(true);
    }

    @Test
    @DisplayName("Should get an Meetup by Id")
    public void getByMeetupIdTest() {
        Integer id = 11;
        Meetup meetup = createNewMeetup();
        meetup.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(meetup));

        Optional<Meetup> foundMeetup = meetupService.getById(id);

        assertThat(foundMeetup.isPresent()).isTrue();
        assertThat(foundMeetup.get().getId()).isEqualTo(id);
        assertThat(foundMeetup.get().getEvent()).isEqualTo(meetup.getEvent());
        assertThat(foundMeetup.get().getMeetupDate()).isEqualTo(meetup.getMeetupDate());
        assertThat(foundMeetup.get().getRegistered()).isEqualTo(meetup.getRegistered());
    }

    @Test
    @DisplayName("Should return empty when get an meetup by id when doesn't exists")
    public void meetupNotFoundByIdTest() {
        Integer id = 11;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Meetup> meetup  = meetupService.getById(id);

        assertThat(meetup.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should delete an meetup")
    public void deleteMeetupTest() {
        Meetup meetup = Meetup.builder().id(11).build();

        assertDoesNotThrow(() -> meetupService.delete(meetup));

        Mockito.verify(repository, Mockito.times(1)).delete(meetup);
    }
    @Test
    @DisplayName("Should return exception when trying to delete a meetup null")
    public void tryDeleteNullMeetup() {
        Meetup meetup = null;

        Throwable exception = Assertions.catchThrowable( () -> meetupService.delete(meetup));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Meetup cannot be null");

        Mockito.verify(repository, Mockito.never()).delete(meetup);
    }

    @Test
    @DisplayName("Should return exception when trying to delete a meetup with id null")
    public void tryDeleteNullMeetupId() {
        Meetup meetup = Meetup.builder().id(null).meetupDate("06-05-2022").build();

        Throwable exception = Assertions.catchThrowable( () -> meetupService.delete(meetup));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Meetup cannot be null");

        Mockito.verify(repository, Mockito.never()).delete(meetup);
    }

    @Test
    @DisplayName("Shoud update an meetup")
    public void updateMeetup() {
        Integer id = 11;
        Meetup updatingMeetup = Meetup.builder().id(11).build();

        Meetup updatedMeetup = createNewMeetup();
        updatedMeetup.setId(id);

        Mockito.when(repository.save(updatingMeetup)).thenReturn(updatedMeetup);
        Meetup meetup = meetupService.update(updatingMeetup);

        assertThat(meetup.getId()).isEqualTo(updatedMeetup.getId());
        assertThat(meetup.getEvent()).isEqualTo(updatedMeetup.getEvent());
        assertThat(meetup.getMeetupDate()).isEqualTo(updatedMeetup.getMeetupDate());
        assertThat(meetup.getRegistered()).isEqualTo(updatedMeetup.getRegistered());
    }

    @Test
    @DisplayName("Should return exception when trying to update a meetup null")
    public void tryUpdateNullMeetup() {
        Meetup meetup = null;

        Throwable exception = Assertions.catchThrowable( () -> meetupService.update(meetup));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Meetup cannot be null");

        Mockito.verify(repository, Mockito.never()).save(meetup);
    }

    @Test
    @DisplayName("Should return exception when trying to update a meetup with id null")
    public void tryUpdateNullMeetupId() {
        Meetup meetup = Meetup.builder().id(null).meetupDate("06-05-2022").build();

        Throwable exception = Assertions.catchThrowable( () -> meetupService.update(meetup));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Meetup cannot be null");

        Mockito.verify(repository, Mockito.never()).save(meetup);
    }

    @Test
    @DisplayName("Should filter meetups must by properties")
    public void findMeetupTest() {
        Meetup meetup = createNewMeetup();
        Pageable pageRequest = PageRequest.of(0,10);

        List<Meetup> listMeetups = Arrays.asList(meetup);
        Page<Meetup> page = new PageImpl<Meetup>(Arrays.asList(meetup),
                PageRequest.of(0,10), 1);

        Mockito.when(repository.findByMeetup(anyString(), any(Pageable.class)))
                .thenReturn(page);

        Page<Meetup> result = meetupService.getRegistrationsByMeetup(meetup, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(listMeetups);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should return exception when trying to find a meetup null")
    public void tryFindMeetupNull() {
        Meetup meetup = null;
        Pageable pageRequest = PageRequest.of(0,10);

        Throwable exception = Assertions.catchThrowable( () -> meetupService.getRegistrationsByMeetup(meetup, pageRequest));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Meetup event cannot be null");

        Mockito.verify(repository, Mockito.never()).findByMeetup("Womakercode", pageRequest);
    }

    private Meetup createNewMeetup() {
        return Meetup.builder().id(101).event("Womakerscode Java").meetupDate("25/06/2022").registered(true)
                .build();
    }
}