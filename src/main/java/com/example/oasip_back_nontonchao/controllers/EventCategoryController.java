package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.entities.EventCategory;
import com.example.oasip_back_nontonchao.services.EventCategoryService;
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
@RequestMapping("/api/events-category")
public class EventCategoryController {

    @Autowired
    private EventCategoryService service;

    @GetMapping("")
    public List<EventCategory> getEventCategory() {
        return service.getEventCategory();
    }


    @PutMapping("/{id}")
    public ResponseEntity editEventCategory(@Valid @RequestBody EventCategory update) {
        if (update.getEventDuration() < 1 || update.getEventDuration() > 480) {
            return new ResponseEntity("eventDuration should be between 1 and 480", HttpStatus.BAD_REQUEST);
        } else {
            service.addEventCategory(update);
            return ResponseEntity.ok("EventCategory Edited! || eventCategory id: " + update.getId());
        }
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
