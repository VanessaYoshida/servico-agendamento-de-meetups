package com.bootcamp.microservicemeetup.controller;

import com.bootcamp.microservicemeetup.controller.dto.MeetupFilterDTO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
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

    @Test
    @DisplayName("Should register on a meetup")
    public void createMeetupTest() throws Exception {

        MeetupDTO dto = MeetupDTO.builder()
                .event("Womakerscode Dados")
                .date("20/06/2022")
                .ownerId(1)
                .registrations(null)
                .build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Registration registration = Registration.builder()
                .id(11)
                .build();

        BDDMockito.given(registrationService.getRegistrationById(11)).
                willReturn(Optional.of(registration));

        Meetup meetup = Meetup.builder()
                .id(11)
                .event("Womakerscode Dados")
                .registrations(List.of(registration))
                .meetupDate("20/06/2022")
                .ownerId(1)
                .build();

        BDDMockito.given(meetupService.save(Mockito.any(Meetup.class))).willReturn(meetup);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(MEETUP_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("11"));
    }

    @Test
    @DisplayName("Should return error when try to register a registration already register on a meetup")
    public void meetupRegistrationErrorOnCreateMeetupTest() throws Exception {

        MeetupDTO dto = MeetupDTO.builder()
                .event("Womakerscode Dados")
                .date("20/06/2022")
                .ownerId(1)
                .registrations(null)
                .build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(meetupService.save(Mockito.any(Meetup.class)))
                .willThrow(new BusinessException("Meetup already enrolled"));

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

        Integer idMeetup = 44;

        Meetup meetup = Meetup.builder()
                .id(idMeetup)
                .event("Womakerscode Dados")
                .meetupDate("20/06/2022")
                .registered(true)
                .build();

        String json = new ObjectMapper().writeValueAsString(meetup);

        BDDMockito.given(meetupService.getById(idMeetup)).willReturn(Optional.of(meetup));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(MEETUP_API.concat("/" + idMeetup))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("event").value("Womakerscode Dados"))
                .andExpect(jsonPath("date").value("20/06/2022"));
    }

    @Test
    @DisplayName("Should return empty when get an meetup registration by id when doesn't exists")
    public void meetupRegistrationNotFound() throws Exception {

        Integer idMeetup = 44;

        Meetup meetup = Meetup.builder()
                .id(idMeetup)
                .event("Womakerscode Dados")
                .meetupDate("20/06/2022")
                .registered(true)
                .build();

        String json = new ObjectMapper().writeValueAsString(meetup);

        BDDMockito.given(meetupService.getById(idMeetup)).willReturn(Optional.of(meetup));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(MEETUP_API.concat("/" + 45))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete the meetup")
    public void deleteMeetup() throws Exception {

        Integer idMeetup = 44;

        Meetup meetup = Meetup.builder()
                .id(idMeetup)
                .event("Womakerscode Dados")
                .meetupDate("20/06/2022")
                .registered(true)
                .build();
        String json = new ObjectMapper().writeValueAsString(meetup);

        BDDMockito.given(meetupService.getById(idMeetup)).willReturn(Optional.of(meetup));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(MEETUP_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }
}