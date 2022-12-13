package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.dtos.EventDateDTO;
import com.example.oasip_back_nontonchao.dtos.EventGet;
import com.example.oasip_back_nontonchao.dtos.EventUpdate;
import com.example.oasip_back_nontonchao.entities.Event;
import com.example.oasip_back_nontonchao.filter.JwtRequestFilter;
import com.example.oasip_back_nontonchao.services.EventService;
import com.example.oasip_back_nontonchao.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity createEvent(@Valid @RequestBody Event req) {
        String token = null;
        try {
            token = JwtRequestFilter.getJwtToken_();
        } catch (Exception e) {
            System.out.println("HEADER NO AUTHORIZATION");
        }
        if (token == null) {
            return service.createEvent(req);
        } else {
            if (jwtTokenUtil.getRoleFromToken(token).equals("ROLE_LECTURER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Lecturer can't POST PUT PATCH /events");
            } else {
                if (jwtTokenUtil.getUsernameFromToken(token).equals(req.getBookingEmail())) {
                    return service.createEvent(req);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("your email is not the same with this token");
                }
            }
        }
    }

    @GetMapping("")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN','LECTURER')")
    public List<EventGet> getAllEvent() {
       return service.getEventDTO();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN','LECTURER')")
    public Event getEventById(@PathVariable Integer id) {
        return service.findEventById(id);
    }

    @GetMapping("/date/{date}/{eventCategoryId}")
    public List<EventDateDTO> getAllEventByDate(@PathVariable String date, @PathVariable Integer eventCategoryId) {
        return service.getEventDateDTO(date, eventCategoryId);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity deleteEventFromId(@PathVariable String id) {
            return service.deleteEventFromId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity editEvent(@Valid @RequestBody EventUpdate update, @PathVariable Integer id) {
        return service.editEvent(update, id);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
