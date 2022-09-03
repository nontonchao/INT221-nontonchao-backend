package com.example.oasip_back_nontonchao.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adminonly")
public class AdminOnly {

    @GetMapping("")
    public ResponseEntity hello_admin() {
        return ResponseEntity.ok().body("IF YOU CAN SEE THIS YOU ARE AN ADMIN :)");
    }
}
