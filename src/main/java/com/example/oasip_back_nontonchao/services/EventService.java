package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.entities.Event;
import com.example.oasip_back_nontonchao.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository repository;

    public List<Event> getEvents() {
        List<Event> eventList = repository.findAll(Sort.by(Sort.Direction.DESC, "eventStartTime"));
        return eventList;
    }

    public List<Event> getEventsFromCategory(Integer id) {
        List<Event> eventList = repository.findByEventCategoryId(id, Sort.by(Sort.Direction.DESC, "eventStartTime"));
        return eventList;
    }

    public void deleteEventFromId(String id) {
        repository.deleteById(Integer.parseInt(id));
    }

    public Event findEventById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event id '" + id + "' does not exist!"));
    }

    public void addEvent(Event event) {
        repository.saveAndFlush(event);
    }
}
