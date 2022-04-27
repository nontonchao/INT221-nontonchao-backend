package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.entities.Event;
import com.example.oasip_back_nontonchao.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.Instant;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService service;

    @GetMapping("")
    public List<Event> getAllEvent() {
        return service.getEvents();
    }

    @GetMapping("/find/{email}")
    public List<Event> getEventFromEmail(@PathVariable String email) {
        return service.getEventsFromEmail(email);
    }

    @PutMapping("/edit/{id}")
    public void editEvent(@PathVariable Integer id, @RequestBody String DateTime) throws ParseException {
        Event event = service.findEventById(id);
        event.setEventStartTime(Instant.parse(DateTime));
        service.addEvent(event);
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

    @DeleteMapping("/delete/{id}")
    public void deleteEventFromId(@PathVariable String id) {
        service.deleteEventFromId(id.toString());
    }
}
