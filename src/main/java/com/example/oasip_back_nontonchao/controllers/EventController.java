package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.dtos.EventUpdate;
import com.example.oasip_back_nontonchao.entities.Event;
import com.example.oasip_back_nontonchao.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService service;

    @GetMapping("")
    public List<Event> getAllEvent() {
        return service.getEvents();
    }


    @PutMapping("/{id}")
    public ResponseEntity editEvent(@Valid @RequestBody EventUpdate update, @PathVariable Integer id) {
        Event event = service.findEventById(id);
        Event toUpdate = event;
        toUpdate.setEventNotes(update.getEventNotes());
        toUpdate.setEventStartTime(update.getEventStartTime());
        List<Event> compare = service.getEventsFromCategoryExcept(event.getEventCategory().getId(), id);
        if (checkOverlap(compare, toUpdate)) {
            service.addEvent(event);
            return ResponseEntity.ok("Event Edited! || event id: " + event.getId());
        }
        return new ResponseEntity("eventStartTime is overlapped!", HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Integer id) {
        return service.findEventById(id);
    }

    @GetMapping("/category/{category_id}")
    public List<Event> getEventByCategory(@PathVariable Integer category_id) {
        return service.getEventsFromCategory(category_id);
    }

    @PostMapping("")
    public ResponseEntity createEvent(@Valid @RequestBody Event req) {
        Pattern p = Pattern.compile("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,24}))$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(req.getBookingEmail());
        boolean isValidEmail = m.find();
        if (!isValidEmail) {
            return new ResponseEntity("must be a well-formed email address", HttpStatus.BAD_REQUEST);
        } else {
            Event event = req;
            List<Event> compare = service.getEventsFromCategory(req.getEventCategory().getId());
            if (checkOverlap(compare, req)) {
                service.addEvent(event);
                return ResponseEntity.ok("Event Added! || event id: " + event.getId());
            }
            return new ResponseEntity("eventStartTime is overlapped!", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean checkOverlap(List<Event> a, Event b) {
        for (Event cmp : a) {
            if (!((b.getEventStartTime().toEpochMilli() >= getEventMilli(cmp) || (getEventMilli(b) <= cmp.getEventStartTime().toEpochMilli()) && getEventMilli(b) != getEventMilli(cmp)))) {
                return false;
            }
        }
        return true;
    }

    private long getEventMilli(Event q) {
        return ((q.getEventStartTime().toEpochMilli() + (q.getEventDuration() * 60000))) + 60000;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEventFromId(@PathVariable String id) {
        service.deleteEventFromId(id.toString());
    }
}
