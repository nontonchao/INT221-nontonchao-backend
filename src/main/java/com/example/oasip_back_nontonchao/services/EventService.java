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
        return repository.findAll(Sort.by(Sort.Direction.DESC, "eventStartTime"));
    }

    public List<Event> getEventsFromCategory(Integer id) {
        return repository.findByEventCategoryId(id, Sort.by(Sort.Direction.DESC, "eventStartTime"));
    }

    public List<Event> getEventsFromCategoryExcept(Integer cid, Integer eid) {
        return repository.findByEventCategoryIdAndIdIsNot(cid, eid, Sort.by(Sort.Direction.DESC, "eventStartTime"));
    }


    public void deleteEventFromId(String id) {
        if (repository.existsById(Integer.parseInt(id))) {
            repository.deleteById(Integer.parseInt(id));
        }
    }

    public Event findEventById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event id '" + id + "' does not exist!"));
    }

    public void addEvent(Event event) {
        repository.saveAndFlush(event);
    }
}
