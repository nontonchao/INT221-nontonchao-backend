package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.dtos.EventCategoryGet;
import com.example.oasip_back_nontonchao.dtos.EventCategoryOwnerUpdate;
import com.example.oasip_back_nontonchao.entities.EventCategory;
import com.example.oasip_back_nontonchao.entities.EventCategoryOwner;
import com.example.oasip_back_nontonchao.services.EventCategoryService;
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
@RequestMapping("/api/events-category")
public class EventCategoryController {

    @Autowired
    private EventCategoryService service;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @GetMapping("")
    public List<EventCategoryGet> getEventCategory() {
        return service.getEventCategory();
    }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('ADMIN')")
    public List<EventCategoryOwner> getEventCategoryOwner() {
        return service.getEventCategoryOwner();
    }

    @PostMapping("/owner")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity addEventCategoryOwner(@RequestBody EventCategoryOwnerUpdate e) {
        return service.addEventCategoryOwner(e);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    public ResponseEntity editEventCategory(@Valid @RequestBody EventCategory update, @PathVariable Integer id, @RequestHeader HttpHeaders headers) {
        String token = headers.get("Authorization").get(0).substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(token);
        switch (jwtTokenUtil.getRoleFromToken(token)) {
            case "ROLE_ADMIN":
                return service.editEventCategoryAdmin(update, id);
            case "ROLE_LECTURER":
                return service.editEventCategory(update, id, email);
        }
        return null;
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
