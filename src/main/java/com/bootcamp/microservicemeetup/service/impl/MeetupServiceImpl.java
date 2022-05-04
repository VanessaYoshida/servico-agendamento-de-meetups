package com.bootcamp.microservicemeetup.service.impl;

import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.repository.MeetupRepository;
import com.bootcamp.microservicemeetup.service.MeetupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MeetupServiceImpl implements MeetupService {

    private MeetupRepository repository;

    public MeetupServiceImpl(MeetupRepository repository) {
        this.repository = repository;
    }

    @Override
    public Meetup save(Meetup meetup) {
        return repository.save(meetup);
    }

    @Override
    public Optional<Meetup> getById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public void delete(Meetup meetup) {
        if (meetup == null || meetup.getId() == null) {
            throw new IllegalArgumentException("Registration id cannot be null");
        }
        this.repository.delete(meetup);
    }

    @Override
    public Meetup update(Meetup meetup) {
        if (meetup == null || meetup.getId() == null) {
            throw new IllegalArgumentException("Meetup id cannot be null");
        }
        return this.repository.save(meetup);
    }

    @Override
    public Page<Meetup> getRegistrationsByMeetup(Meetup meetup, Pageable pageable) {
        if (meetup == null || meetup.getEvent() == null) {
            throw new IllegalArgumentException("Meetup event cannot be null");
        }
        return repository.findByMeetup(meetup.getEvent(), pageable);
    }

    //    @Override
//    public Page<Meetup> findByPersonIdOnMeetup(MeetupFilterDTO filterDTO, Pageable pageable) {
//        return repository.findByPersonIdOnMeetup(filterDTO.getPersonId(), filterDTO.getEvent(), pageable);
//    }

}