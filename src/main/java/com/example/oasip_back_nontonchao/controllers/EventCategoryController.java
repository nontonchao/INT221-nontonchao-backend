package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.entities.EventCategory;
import com.example.oasip_back_nontonchao.services.EventCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @PutMapping("/edit")
    public ResponseEntity editEventCategory(@RequestBody EventCategory update) {
        service.addEventCategory(update);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
