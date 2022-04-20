package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.entities.Event;
import com.example.oasip_back_nontonchao.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService service;

    @CrossOrigin(origins = "*")
    @GetMapping("")
    public List<Event> getEvent(@RequestParam(value = "email", required = false) String email) {
        if (email == null) {
            return service.getEvents();
        } else {
            return service.getEventsFromEmail(email);
        }
    }
}
