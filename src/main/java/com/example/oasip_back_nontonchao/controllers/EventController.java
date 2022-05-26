package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.dtos.EventGet;
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
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService service;

    @PostMapping("")
    public ResponseEntity createEvent(@Valid @RequestBody Event req) {
        return service.createEvent(req);
    }

    @GetMapping("")
    public List<EventGet> getAllEvent() {
        return service.getEventDTO();
    }

    @PutMapping("/{id}")
    public ResponseEntity editEvent(@Valid @RequestBody EventUpdate update, @PathVariable Integer id) {
        return service.editEvent(update, id);
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Integer id) {
        return service.findEventById(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEventFromId(@PathVariable String id) {
        service.deleteEventFromId(id.toString());
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
}
