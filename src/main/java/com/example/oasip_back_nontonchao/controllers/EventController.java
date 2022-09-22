package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.dtos.EventGet;
import com.example.oasip_back_nontonchao.dtos.EventUpdate;
import com.example.oasip_back_nontonchao.entities.Event;
import com.example.oasip_back_nontonchao.services.EventService;
import com.example.oasip_back_nontonchao.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import net.bytebuddy.build.Plugin;
import net.minidev.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLOutput;
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
    public ResponseEntity createEvent(@Valid @RequestBody Event req) {
        return service.createEvent(req);
    }

    @GetMapping("")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN','LECTURER')")
    public List<EventGet> getAllEvent(@RequestHeader HttpHeaders headers) {
        String token = headers.get("Authorization").get(0).substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(token);
        if (!jwtTokenUtil.getRoleFromToken(token).equals("ROLE_ADMIN")) {
            return service.getEventByEmailDTO(email);
        } else {
            return service.getEventDTO();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN','LECTURER')")
    public Event getEventById(@PathVariable Integer id, @RequestHeader HttpHeaders headers) {
        String token = headers.get("Authorization").get(0).substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(token);
        if (!jwtTokenUtil.getRoleFromToken(token).equals("ROLE_ADMIN")) {
            return service.findEventByEmailAndId(email, id);
        } else {
            return service.findEventById(id);
        }
    }

    @GetMapping("/date/{date}/{eventCategoryId}")
    public List<EventGet> getAllEventByDate(@PathVariable String date, @PathVariable Integer eventCategoryId) {
        return service.getEventDateDTO(date, eventCategoryId);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN','LECTURER')")
    public ResponseEntity deleteEventFromId(@PathVariable String id, @RequestHeader HttpHeaders headers) {
        String token = headers.get("Authorization").get(0).substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(token);
        if (!jwtTokenUtil.getRoleFromToken(token).equals("ROLE_ADMIN")) {
            return service.deleteEventFromIdAndEmail(id, email);
        } else {
            return service.deleteEventFromId(id);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN','LECTURER')")
    public ResponseEntity editEvent(@Valid @RequestBody EventUpdate update, @PathVariable Integer id, @RequestHeader HttpHeaders headers) {
        String token = headers.get("Authorization").get(0).substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(token);
        if (!jwtTokenUtil.getRoleFromToken(token).equals("ROLE_ADMIN")) {
            return service.editEvent(update, id, email);
        } else {
            return service.editEventAdmin(update, id);
        }

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
