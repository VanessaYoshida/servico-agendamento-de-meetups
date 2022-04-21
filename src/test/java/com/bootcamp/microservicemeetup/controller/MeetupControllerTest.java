package com.bootcamp.microservicemeetup.controller;

import com.bootcamp.microservicemeetup.controller.resource.MeetupController;
import com.bootcamp.microservicemeetup.exception.BusinessException;
import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
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
    private RegistrationService registrationService;

    @MockBean
    private MeetupService meetupService;

    @Test
    @DisplayName("Should register on a meetup")
    public void createMeetupTest() throws Exception {

        // quando enviar uma requisicao para esse registration precisa ser encontrado um valor que tem esse usuario
        MeetupDTO dto = MeetupDTO.builder().registrationAttribute("234").event("Womakerscode Dados").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Registration registration = Registration.builder().id(11).registration("234").build();

        BDDMockito.given(registrationService.getRegistrationByRegistrationAttribute("234")).
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

        MeetupDTO dto = MeetupDTO.builder().registrationAttribute("234").event("Womakerscode Dados").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(registrationService.getRegistrationByRegistrationAttribute("234")).
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
    public void  meetupRegistrationErrorOnCreateMeetupTest() throws Exception {

        MeetupDTO dto = MeetupDTO.builder().registrationAttribute("234").event("Womakerscode Dados").build();
        String json = new ObjectMapper().writeValueAsString(dto);


        Registration registration = Registration.builder().id(11).name("Vanessa Yoshida").registration("234").build();
        BDDMockito.given(registrationService.getRegistrationByRegistrationAttribute("234"))
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

        MeetupDTO dto = MeetupDTO.builder().registrationAttribute("234").event("Womakerscode Dados").build();

        Registration registration = Registration.builder().id(idRegistration).registration("234").build();

        Meetup meetup = Meetup.builder().id(idRegistration).event("Womakerscode Dados").registration(registration).meetupDate("10/10/2022").build();

        String json = new ObjectMapper().writeValueAsString(meetup);

        BDDMockito.given(meetupService.getById(idRegistration)).willReturn(Optional.of(meetup));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(MEETUP_API.concat("/" + idRegistration))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isOk());
//                .andExpect(jsonPath("id").value(44))
//                .andExpect(jsonPath("event").value("Womakerscode Dados"))
//                .andExpect(jsonPath("registration").value(registration))
//                .andExpect(jsonPath("meetupDate").value("10/10/2022"));
    }
}