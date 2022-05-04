package com.bootcamp.microservicemeetup.controller.resource;

import com.bootcamp.microservicemeetup.controller.dto.RegistrationDTO;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.service.MeetupService;
import com.bootcamp.microservicemeetup.service.RegistrationService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {
  private RegistrationService registrationService;
  private final MeetupService meetupService;
  private ModelMapper modelMapper;

  public RegistrationController(RegistrationService registrationService, MeetupService meetupService, ModelMapper modelMapper) {
    this.registrationService = registrationService;
    this.meetupService = meetupService;
    this.modelMapper = modelMapper;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RegistrationDTO create(@RequestBody @Valid RegistrationDTO dto) {

    //verificando se o meetup existe
    meetupService.getById(dto.getMeetupId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

    Registration entity = modelMapper.map(dto, Registration.class);
    entity = registrationService.save(entity);

    return modelMapper.map(entity, RegistrationDTO.class);
  }

  @GetMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public RegistrationDTO get(@PathVariable Integer id) {

    return registrationService
      .getRegistrationById(id)
      .map(registration -> modelMapper.map(registration, RegistrationDTO.class))
      .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteByRegistrationId(@PathVariable Integer id) {
    Registration registration = registrationService.getRegistrationById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    registrationService.delete(registration);
  }

  @PutMapping("{id}")
  public RegistrationDTO update(@PathVariable Integer id, RegistrationDTO registrationDTO) {

    return registrationService.getRegistrationById(id).map(registration -> {
      registration.setName(registrationDTO.getName());
      registration.setDateOfRegistration(registrationDTO.getDateOfRegistration());
      registration = registrationService.update(registration);

      return modelMapper.map(registration, RegistrationDTO.class);
    }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

  }

  @GetMapping
  public Page<RegistrationDTO> find(RegistrationDTO dto, Pageable pageRequest) {
    Registration filter = modelMapper.map(dto, Registration.class);
    Page<Registration> result = registrationService.find(filter, pageRequest);

    List<RegistrationDTO> list = result.getContent()
      .stream()
      .map(entity -> modelMapper.map(entity, RegistrationDTO.class))
      .collect(Collectors.toList());

    return new PageImpl<RegistrationDTO>(list, pageRequest, result.getTotalElements());
  }
}