package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.entities.EventCategory;
import com.example.oasip_back_nontonchao.repositories.EventCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventCategoryService {
    @Autowired
    private EventCategoryRepository repository;

    public List<EventCategory> getEventCategory() {
        return repository.findAll();
    }

    public String getEventCategoryNameById(Integer id) {
        return repository.findNameById(id);
    }

    public ResponseEntity editEventCategory(EventCategory update, Integer id) {
        Optional<EventCategory> s = repository.findById(id);
        if (!s.isEmpty()) {
            List<EventCategory> toCheck = repository.findAllByEventCategoryNameAndIdIsNot(update.getEventCategoryName().stripLeading().stripTrailing(), id);
            if (toCheck.stream().count() == 0) {
                s.get().setEventCategoryName(update.getEventCategoryName());
                s.get().setEventDuration(update.getEventDuration());
                s.get().setEventCategoryDescription(update.getEventCategoryDescription());
                repository.saveAndFlush(s.get());
                return ResponseEntity.ok("EventCategory Edited! || eventCategory id: " + s.get().getId());
            } else {
                return new ResponseEntity("eventCategoryName should be unique", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity("eventCategory not found!", HttpStatus.BAD_REQUEST);
        }
    }
}
