package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.entities.Event;
import com.example.oasip_back_nontonchao.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService service;

    @GetMapping("")
    public List<Event> getEvent(@RequestParam(value = "email", required = false) String email) {
        if (email == null) {
            return service.getEvents();
        } else {
            return service.getEventsFromEmail(email);
        }
    }

    @DeleteMapping("/delete")
    public void deleteEventFromId(@RequestParam(value = "id", required = true) String id) {
        service.deleteEventFromId(id.toString());
    }

    @PostMapping("")
    public ResponseEntity createEvent(@RequestBody Event req) {
        if (req.getBookingName().length() <= 0 || req.getBookingEmail().length() <= 0 || req.getEventStartTime().toString().length() <= 0) {
            return ResponseEntity.status(400).body("");
        } else {
            Event event = req;
            service.addEvent(event);
            return ResponseEntity.ok(HttpStatus.OK);
        }
    }
}
