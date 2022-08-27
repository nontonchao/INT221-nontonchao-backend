package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.dtos.EventGet;
import com.example.oasip_back_nontonchao.dtos.EventUpdate;
import com.example.oasip_back_nontonchao.entities.Event;
import com.example.oasip_back_nontonchao.repositories.EventCategoryRepository;
import com.example.oasip_back_nontonchao.repositories.EventRepository;
import com.example.oasip_back_nontonchao.utils.ListMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository repository;

    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EventCategoryRepository CategoryRepository;

    public ResponseEntity createEvent(Event req) {
        Event event = req;
        if (CategoryRepository.existsById(req.getEventCategory().getId())) {
            Instant dt = req.getEventStartTime().minusSeconds(86400);
            Instant dt2 = req.getEventStartTime().plusSeconds(86400);
            List<Event> compare = repository.findByEventCategoryIdAndEventStartTimeIsBetween(event.getEventCategory().getId(), dt, dt2, Sort.by(Sort.Direction.DESC,
                    "eventStartTime"));
            if (checkOverlap(compare, req)) {
                repository.saveAndFlush(event);
                return ResponseEntity.status(HttpStatus.CREATED).body("Event Added! || event id: " + event.getId());
            }
            return new ResponseEntity("eventStartTime is overlapped!", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity("eventCategory not found!", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity editEvent(EventUpdate update, Integer id) {
        Event event = findEventById(id);
        Event toUpdate = event;
        toUpdate.setEventNotes(update.getEventNotes());
        toUpdate.setEventStartTime(update.getEventStartTime());
        Instant dt = update.getEventStartTime().minusSeconds(86400);
        Instant dt2 = update.getEventStartTime().plusSeconds(86400);
        List<Event> compare = repository.findByEventCategoryIdAndEventStartTimeIsBetweenAndIdIsNot(event.getEventCategory().getId(), dt, dt2, id, Sort.by(Sort.Direction.DESC
                , "eventStartTime"));
        if (checkOverlap(compare, toUpdate)) {
            repository.saveAndFlush(event);
            return ResponseEntity.ok("Event Edited! || event id: " + event.getId());
        }
        return new ResponseEntity("eventStartTime is overlapped!", HttpStatus.BAD_REQUEST);
    }

    public List<EventGet> getEventDateDTO(String date, Integer eventCategoryId) {
        System.out.println(date);
        return listMapper.mapList(repository.findAllByEventStartTime(date, eventCategoryId), EventGet.class, modelMapper);
    }

    public List<EventGet> getEventDTO() {
        return listMapper.mapList(repository.findAll(Sort.by(Sort.Direction.DESC, "eventStartTime")), EventGet.class, modelMapper);
    }

    public void deleteEventFromId(String id) {
        if (repository.existsById(Integer.parseInt(id))) {
            repository.deleteById(Integer.parseInt(id));
        }
    }

    public Event findEventById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event id '" + id + "' does not exist!"));
    }

    private boolean checkOverlap(List<Event> a, Event b) {
        for (Event cmp : a) {
            if (!((b.getEventStartTime().toEpochMilli() >= getEventMilli(cmp) || (getEventMilli(b) <= cmp.getEventStartTime().toEpochMilli())
                    && getEventMilli(b) != getEventMilli(cmp)))) {
                return false;
            }
        }
        return true;
    }

    private long getEventMilli(Event q) {
        return ((q.getEventStartTime().toEpochMilli() + (q.getEventDuration() * 60000))) + 60000;
    }
}
