package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.entities.User;
import com.example.oasip_back_nontonchao.repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MatchService {

    @Autowired
    MatchRepository matchRepository;

    Argon2PasswordEncoder ch = new Argon2PasswordEncoder();

    public ResponseEntity check(String email, String password) {
        User toCheck = matchRepository.findByEmail(email);
        if (toCheck == null) {
            return ResponseEntity.status(404).body("A user with the specified email DOES NOT exist.");
        } else {
            if (ch.matches(password, toCheck.getPassword())) {
                return ResponseEntity.ok("Password Matched.");
            } else {
                return ResponseEntity.status(401).body("Password NOT Matched.");
            }
        }

    }
}
