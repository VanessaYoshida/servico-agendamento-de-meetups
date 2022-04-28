package com.bootcamp.microservicemeetup.controller;

import com.bootcamp.microservicemeetup.controller.dto.RegistrationDTO;
import com.bootcamp.microservicemeetup.controller.dto.RegisteredMeetupDTO;
import com.bootcamp.microservicemeetup.controller.resource.MeetupController;
import com.bootcamp.microservicemeetup.exception.BusinessException;
import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.repository.MeetupRepository;
import com.bootcamp.microservicemeetup.service.MeetupService;
import com.bootcamp.microservicemeetup.service.RegistrationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {MeetupController.class})
@AutoConfigureMockMvc

public class MeetupControllerTest {

    static final String MEETUP_API = "/api/meetups";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RegistrationService registrationService;

    @MockBean
    MeetupService meetupService;

    @MockBean
    MeetupRepository meetupRepository;

//    @BeforeEach
//    public void setUp() {
//        this.meetupService = new MeetupServiceImpl(meetupRepository);
//    }

    @Test
    @DisplayName("Should register on a meetup")
    public void createMeetupTest() throws Exception {

        // quando enviar uma requisicao para esse registration precisa ser encontrado um valor que tem esse usuario
        MeetupDTO dto = MeetupDTO.builder()
                .registrationAttribute("234")
                .event("Womakerscode Dados")
                .registration(RegistrationDTO.builder().id(11).build())
                .build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Registration registration = Registration.builder()
                .id(11)
                .build();

        BDDMockito.given(registrationService.getRegistrationById(11)).
                willReturn(Optional.of(registration));

        Meetup meetup = Meetup.builder().id(11).event("Womakerscode Dados").registration(registration).meetupDate("10/10/2022").build();

        BDDMockito.given(meetupService.save(Mockito.any(Meetup.class))).willReturn(meetup);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(MEETUP_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // Aqui o que retorna é o id do registro no meetup
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("11"));

    }

    @Test
    @DisplayName("Should return error when try to register an a meetup no existent")
    public void invalidRegistrationCreateMeetupTest() throws Exception {

        MeetupDTO dto = MeetupDTO.builder()
                .registration(RegistrationDTO.builder().id(11).build())
                .event("Womakerscode Dados")
                .build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(registrationService.getRegistrationById(11)).
                willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(MEETUP_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Should return error when try to register a registration already register on a meetup")
    public void meetupRegistrationErrorOnCreateMeetupTest() throws Exception {

        MeetupDTO dto = MeetupDTO.builder().registrationAttribute("234")
                .registration(RegistrationDTO.builder().id(11).build())
                .event("Womakerscode Dados").build();
        String json = new ObjectMapper().writeValueAsString(dto);


        Registration registration = Registration.builder().id(11).name("Vanessa Yoshida")
                .build();
        BDDMockito.given(registrationService.getRegistrationById(11))
                .willReturn(Optional.of(registration));

        // Procura na base se ja tem algum registration para esse meetup
        BDDMockito.given(meetupService.save(Mockito.any(Meetup.class))).willThrow(new BusinessException("Meetup already enrolled"));


        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(MEETUP_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get meetup registration")
    public void getMeetupTest() throws Exception {

        Integer idRegistration = 44;
        Registration registration = Registration.builder().id(idRegistration).build();
        Meetup meetup = Meetup.builder()
                .id(idRegistration)
                .event("Womakerscode Dados")
                .registration(registration)
                .meetupDate("10/10/2022")
                .registered(true)
                .build();

        BDDMockito.given(meetupService.getById(idRegistration)).willReturn(Optional.of(meetup));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(MEETUP_API.concat("/" + idRegistration))
                .accept(MediaType.APPLICATION_JSON);

        RegisteredMeetupDTO registered = RegisteredMeetupDTO.builder().registered(true).build();

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("registered").value(registered.getRegistered()));
    }

    @Test
    @DisplayName("Should return empty when get an meetup registration by id when doesn't exists")
    public void meetupRegistrationNotFound() throws Exception {

        Integer idRegistration = 44;

        MeetupDTO.builder().registrationAttribute("234").event("Womakerscode Dados").build();

        Registration.builder().id(idRegistration).build();

        BDDMockito.given(meetupService.getById(idRegistration)).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(MEETUP_API.concat("/" + idRegistration))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete the meetup registration")
    public void deleteMeetupRegistration() throws Exception {
//        MeetupDTO dto = MeetupDTO.builder().registrationAttribute("234").event("Womakerscode Dados").build();
//        String json = new ObjectMapper().writeValueAsString(dto);
//
        Registration registration = Registration.builder().id(11).build();
//
//        BDDMockito.given(registrationService.getRegistrationByRegistrationAttribute("234")).
//                willReturn(Optional.of(registration));

        Meetup meetup = Meetup.builder().id(11).event("Womakerscode Dados").registration(registration).meetupDate("10/10/2022").build();

        BDDMockito.given(meetupService.getById(anyInt())).willReturn(Optional.of(meetup));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(MEETUP_API.concat("/" + 11))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());

//        Integer idRegistration = 44;
//
//        Registration registration = Registration.builder().id(idRegistration).registration("234").build();
//
//        Meetup meetup = Meetup.builder().id(idRegistration).event("Womakerscode Dados").registration(registration).meetupDate("10/10/2022").registered(true).build();
//
//        BDDMockito.given(meetupService.getById(anyInt())).willReturn(Optional.of(meetup));
//
//        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
//                .delete(MEETUP_API.concat("/" + 1))
//                .accept(MediaType.APPLICATION_JSON);
//
//        mockMvc.perform(requestBuilder)
//                .andExpect(status().isNoContent());
    }


}