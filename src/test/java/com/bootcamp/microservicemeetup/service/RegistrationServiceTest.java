package com.bootcamp.microservicemeetup.service;

import com.bootcamp.microservicemeetup.exception.BusinessException;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.repository.RegistrationRepository;
import com.bootcamp.microservicemeetup.service.impl.RegistrationServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class RegistrationServiceTest {

    RegistrationService registrationService;

    @MockBean
    RegistrationRepository repository;

    @BeforeEach
    public void setUp() {
        this.registrationService = new RegistrationServiceImpl(repository);
    }

    @Test
    @DisplayName("Should save an registration")
    public void saveRegistration() {
        Registration registration = createValidRegistration();

        Mockito.when(repository.existsByPersonId(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(registration)).thenReturn(createValidRegistration());

        Registration savedRegistration = registrationService.save(registration);

        assertThat(savedRegistration.getId()).isEqualTo(101);
        assertThat(savedRegistration.getName()).isEqualTo("Vanessa Yoshida");
        assertThat(savedRegistration.getDateOfRegistration()).isEqualTo(LocalDate.now());
        assertThat(savedRegistration.getPersonId()).isEqualTo("001");
    }

    private Registration createValidRegistration() {
        return Registration.builder()
                .id(101)
                .name("Vanessa Yoshida")
                .dateOfRegistration(LocalDate.now())
                .personId("001")
                .build();
    }

    @Test
    @DisplayName("Should throw business error when thy " +
            "to save a new registration with a registration duplicated")
    public void shouldNotSaveAsRegistrationDuplicated() {

        Registration registration = createValidRegistration();

        Mockito.when(repository.existsByPersonId(Mockito.any())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable( () -> registrationService.save(registration));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Registration already created");

        Mockito.verify(repository, Mockito.never()).save(registration);
    }

    @Test
    @DisplayName("Should get an Registration by Id")
    public void getByRegistrationIdTest() {
        Integer id = 11;
        Registration registration = createValidRegistration();
        registration.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(registration));

        Optional<Registration> foundRegistration = registrationService.getRegistrationById(id);

        assertThat(foundRegistration.isPresent()).isTrue();
        assertThat(foundRegistration.get().getId()).isEqualTo(id);
        assertThat(foundRegistration.get().getName()).isEqualTo(registration.getName());
        assertThat(foundRegistration.get().getDateOfRegistration()).isEqualTo(registration.getDateOfRegistration());
        assertThat(foundRegistration.get().getPersonId()).isEqualTo(registration.getPersonId());
    }

    @Test
    @DisplayName("Should return empty when get an registration by id when doesn't exists")
    public void registrationNotFoundByIdTest() {
        Integer id = 11;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Registration> registration  = registrationService.getRegistrationById(id);

        assertThat(registration.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should delete an registration")
    public void deleteRegistrationTest() {
        Registration registration = Registration.builder().id(11).build();

        assertDoesNotThrow(() -> registrationService.delete(registration));

        Mockito.verify(repository, Mockito.times(1)).delete(registration);
    }

    @Test
    @DisplayName("Should return exception when trying to delete a registration null")
    public void tryDeleteNullRegistration() {
        Registration registration = null;

        Throwable exception = Assertions.catchThrowable( () -> registrationService.delete(registration));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Registration id cannot be null");

        Mockito.verify(repository, Mockito.never()).delete(registration);
    }

    @Test
    @DisplayName("Should return exception when trying to delete a registration id null")
    public void tryDeleteNullRegistrationId() {
        Registration registration = Registration.builder().id(null).dateOfRegistration(LocalDate.now()).build();;

        Throwable exception = Assertions.catchThrowable( () -> registrationService.delete(registration));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Registration id cannot be null");

        Mockito.verify(repository, Mockito.never()).delete(registration);
    }

    @Test
    @DisplayName("Shoud update an registration")
    public void updateRegistration() {
        Integer id = 11;
        Registration updatingRegistration = Registration.builder().id(11).build();

        Registration updatedRegistration = createValidRegistration();
        updatedRegistration.setId(id);

        Mockito.when(repository.save(updatingRegistration)).thenReturn(updatedRegistration);
        Registration registration = registrationService.update(updatingRegistration);

        assertThat(registration.getId()).isEqualTo(updatedRegistration.getId());
        assertThat(registration.getName()).isEqualTo(updatedRegistration.getName());
        assertThat(registration.getDateOfRegistration()).isEqualTo(updatedRegistration.getDateOfRegistration());
        assertThat(registration.getPersonId()).isEqualTo(updatedRegistration.getPersonId());
    }

    @Test
    @DisplayName("Should return exception when trying to update a registration null")
    public void tryUpdateNullRegistration() {
        Registration updateRegistration = null;

        Throwable exception = Assertions.catchThrowable( () -> registrationService.update(updateRegistration));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Registration id cannot be null");

        Mockito.verify(repository, Mockito.never()).save(updateRegistration);
    }

    @Test
    @DisplayName("Should return exception when trying to update a registration with id null")
    public void tryUpdateNullRegistrationId() {
        Registration updateRegistration = Registration.builder().id(null).dateOfRegistration(LocalDate.now()).build();

        Throwable exception = Assertions.catchThrowable( () -> registrationService.update(updateRegistration));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Registration id cannot be null");

        Mockito.verify(repository, Mockito.never()).save(updateRegistration);
    }

    @Test
    @DisplayName("Should filter registrations must by properties")
    public void findRegistrationTest() {
        Registration registration = createValidRegistration();
        Pageable pageRequest = PageRequest.of(0,10);

        List<Registration> listRegistrations = Arrays.asList(registration);
        Page<Registration> page = new PageImpl<Registration>(Arrays.asList(registration),
                PageRequest.of(0,10), 1);

        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Registration> result = registrationService.find(registration, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(listRegistrations);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should get an Registration model by registration attribute")
    public void getRegistrationByRegistrationAtrb() {
        String registrationAttribute = "1234";

        Mockito.when(repository.findByPersonId(registrationAttribute))
                .thenReturn(Optional.of(Registration.builder().id(11).personId(registrationAttribute).build()));

        Optional<Registration> registration  = registrationService.getRegistrationByRegistrationAttribute(registrationAttribute);

        assertThat(registration.isPresent()).isTrue();
        assertThat(registration.get().getId()).isEqualTo(11);
        assertThat(registration.get().getPersonId()).isEqualTo(registrationAttribute);

        Mockito.verify(repository, Mockito.times(1)).findByPersonId(registrationAttribute);
    }

    @Test
    @DisplayName("Should get all Registration")
    public void getAllRegistration() {
        Registration registration = createValidRegistration();

        Integer id = 11;
        Registration registration2 = createValidRegistration();
        registration2.setId(id);

        Mockito.when(repository.findAll()).thenReturn(Arrays.asList(registration, registration2));

        List<Registration> foundRegistration = registrationService.getRegistrationAll();

        assertThat(foundRegistration.listIterator());
        assertEquals(2, foundRegistration.size(), "Same amount of Register found");
    }
}