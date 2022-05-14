package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.dtos.EventPage;
import com.example.oasip_back_nontonchao.entities.Event;
import com.example.oasip_back_nontonchao.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
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
    public EventPage getAllEvent(@RequestParam(defaultValue = "") int page) {
        return service.getEvents(page);
    }


    @PutMapping("/edit")
    public ResponseEntity editEvent(@RequestBody Event update) {
        if (update.getEventStartTime().toString().length() <= 3) {
            return new ResponseEntity("date time error", HttpStatus.BAD_REQUEST);
        } else if (Instant.now().minusSeconds(60).isAfter(update.getEventStartTime())) {
            return new ResponseEntity("Please future time!", HttpStatus.BAD_REQUEST);
        } else {
            Event event = update;
            List<Event> compare = service.getEventsFromCategoryExcept(update.getEventCategory().getId(), update.getId());
            if (compare.stream().count() == 0) {
                service.addEvent(event);
                return ResponseEntity.ok(HttpStatus.OK);
            } else {
                if (checkOverlap(compare, update)) {
                    service.addEvent(event);
                    return ResponseEntity.ok(HttpStatus.OK);
                }
                return new ResponseEntity("eventStartTime is overlapped!", HttpStatus.BAD_REQUEST);
            }
        }
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
    public ResponseEntity createEvent(@RequestBody Event req) {
        Pattern p = Pattern.compile("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,24}))$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(req.getBookingEmail());
        boolean isValidEmail = m.find();
        if (req.getBookingName().length() <= 0 || req.getBookingEmail().length() <= 0 || req.getEventStartTime().toString().length() <= 0) {
            return new ResponseEntity("Missing some field!", HttpStatus.BAD_REQUEST);
        } else if (!isValidEmail) {
            return new ResponseEntity("Invalid email!", HttpStatus.BAD_REQUEST);
        } else if (Instant.now().minusSeconds(60).isAfter(req.getEventStartTime())) {
            return new ResponseEntity("Please future time!", HttpStatus.BAD_REQUEST);
        } else {
            Event event = req;
            List<Event> compare = service.getEventsFromCategory(req.getEventCategory().getId());
            if (compare.stream().count() == 0) {
                service.addEvent(event);
                return ResponseEntity.ok(HttpStatus.OK);
            } else {
                if (checkOverlap(compare, req)) {
                    service.addEvent(event);
                    return ResponseEntity.ok(HttpStatus.OK);
                }
                return new ResponseEntity("eventStartTime is overlapped!", HttpStatus.BAD_REQUEST);
            }
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

    @DeleteMapping("/delete/{id}")
    public void deleteEventFromId(@PathVariable String id) {
        service.deleteEventFromId(id.toString());
    }
}
