package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin("*")
@RequestMapping("/api/login")
public class MatchController {
    @Autowired
    MatchService matchService;

    @PostMapping("")
    public ResponseEntity check(@RequestParam String email, @RequestParam String password) {
        return matchService.check(email, password);
    }
}
