package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.entities.EventCategory;
import com.example.oasip_back_nontonchao.services.EventCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events-category")
public class EventCategoryController {

    @Autowired
    private EventCategoryService service;

    @GetMapping("")
    public List<EventCategory> getEventCategory() {
        return service.getEventCategory();
    }

}
