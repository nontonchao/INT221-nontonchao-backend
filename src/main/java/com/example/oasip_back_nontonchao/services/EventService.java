package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.entities.Event;
import com.example.oasip_back_nontonchao.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository repository;

    public List<Event> getEvents() {
        List<Event> eventList = repository.findAll();
        return eventList;
    }

    public List<Event> getEventsFromEmail(String email) {
        List<Event> eventList = repository.findAllByBookingEmail(email);
        return eventList;
    }
}
