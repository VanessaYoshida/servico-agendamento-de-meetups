package com.bootcamp.microservicemeetup.controller.resource;

import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.bootcamp.microservicemeetup.controller.dto.RegistrationDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.service.MeetupService;
import com.bootcamp.microservicemeetup.service.RegistrationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/meetups")
@RequiredArgsConstructor
public class MeetupController {
    private final MeetupService meetupService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private Integer create(@RequestBody MeetupDTO meetupDTO) {

        Meetup entity = Meetup.builder()
                .event(meetupDTO.getEvent())
                .meetupDate(meetupDTO.getDate().toString())
                .build();

        entity = meetupService.save(entity);
        return entity.getId();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public MeetupDTO get(@PathVariable Integer id) {

        return meetupService
                .getById(id)
                .map(meetup -> modelMapper.map(meetup, MeetupDTO.class))
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<MeetupDTO> find(MeetupFilterDTO dto, Pageable pageRequest) {
        Page<Meetup> result = meetupService.find(dto, pageRequest);
        List<MeetupDTO> meetups = result
                .getContent()
                .stream()
                .map(entity -> {

                    List<Registration> registrations = entity.getRegistrations();

                    List<RegistrationDTO> registrationDTOS = registrations.stream()
                            .map(registration -> modelMapper.map(registration, RegistrationDTO.class))
                            .collect(Collectors.toList());

                    MeetupDTO meetupDTO = modelMapper.map(entity, MeetupDTO.class);
                    meetupDTO.setRegistrations(registrationDTOS);

                    return meetupDTO;

                }).collect(Collectors.toList());
        return new PageImpl<MeetupDTO>(meetups, pageRequest, result.getTotalElements());
    }

    @PutMapping("{id}")
    public MeetupDTO update(@PathVariable Integer id, MeetupDTO meetupDTO) {

        return meetupService.getById(id).map(meetup -> {
            meetup.setEvent(meetupDTO.getEvent());
            meetup.setMeetupDate(meetupDTO.getDate());
            meetup.setOwnerId(meetupDTO.getOwnerId());
            meetup = meetupService.update(meetup);

            return modelMapper.map(meetup, MeetupDTO.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteByMeetupEvent(@RequestBody Meetup meetup) {

        Meetup meetupId = meetupService.getById(meetup.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        meetupService.delete(meetupId);

        return ResponseEntity.ok().build();
    }
}
