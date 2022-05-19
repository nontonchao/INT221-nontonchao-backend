package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.entities.Event;
import com.example.oasip_back_nontonchao.entities.EventCategory;
import com.example.oasip_back_nontonchao.repositories.EventCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventCategoryService {
    @Autowired
    private EventCategoryRepository repository;

    public List<EventCategory> getEventCategory() {
        List<EventCategory> eventcategories = repository.findAll();
        return eventcategories;
    }

    public List<EventCategory> getEventCategoryByName(String name, Integer id) {
        return repository.findAllByEventCategoryNameAndIdIsNot(name, id);
    }

    public void addEventCategory(EventCategory eventCategory) {
        repository.saveAndFlush(eventCategory);
    }
}
