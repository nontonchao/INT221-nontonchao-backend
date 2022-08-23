package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@CrossOrigin("*")
@RequestMapping("/api/match")
public class MatchController {
    @Autowired
    MatchService matchService;

    @GetMapping("")
    public ResponseEntity check(@RequestParam String email, @RequestParam String password) {
        return matchService.check(email, password);
    }
}
