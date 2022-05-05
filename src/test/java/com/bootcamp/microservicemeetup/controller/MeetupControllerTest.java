package com.bootcamp.microservicemeetup.controller;

import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.controller.resource.MeetupController;
import com.bootcamp.microservicemeetup.exception.BusinessException;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        Meetup meetup = Meetup.builder()
                .id(11)
                .event("Womakerscode Dados")
                .registrations(Collections.emptyList())
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

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(MEETUP_API.concat("/" + idMeetup))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return resource not found when no meetup is found to delete")
    public void tryDeleteMeetupThatDontExists() throws Exception {

        Integer idMeetup = 44;

        Meetup meetup = Meetup.builder()
                .id(idMeetup)
                .event("Womakerscode Dados")
                .meetupDate("20/06/2022")
                .registered(true)
                .build();
        String json = new ObjectMapper().writeValueAsString(meetup);

        meetupService.delete(meetup);
        BDDMockito.given(meetupService.getById(idMeetup)).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(MEETUP_API.concat("/" + idMeetup))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update when meetup info")
    public void updateMeetupTest() throws Exception {
        String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(createNewMeetup());

        Integer idMeetup = 44;

        Meetup meetup = Meetup.builder()
                .id(idMeetup)
                .event("Womakerscode Dados")
                .meetupDate("20/06/2022")
                .registered(true)
                .build();

        BDDMockito.given(meetupService.getById(idMeetup)).willReturn(Optional.of(meetup));

        Meetup meetupUpdated = meetup;
        meetupUpdated.setEvent("Womakercode Java");

        BDDMockito.given(meetupService.update(meetup)).willReturn(meetupUpdated);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(MEETUP_API.concat("/" + idMeetup))
                .contentType(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    private Meetup createNewMeetup() {
        return Meetup.builder().id(101).event("Womakerscode Java").meetupDate("25/06/2022").registered(true)
                .build();
    }
}