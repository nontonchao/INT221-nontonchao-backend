package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.entities.User;
import com.example.oasip_back_nontonchao.repositories.MatchRepository;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MatchService {

    @Autowired
    MatchRepository matchRepository;

    Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 8, 32);

    public ResponseEntity check(String email, String password) {
        User toCheck = matchRepository.findByEmail(email);
        if (toCheck == null) {
            return ResponseEntity.status(404).body("A user with the specified email DOES NOT exist.");
        } else {
            if (argon2.verify(toCheck.getPassword(), password)) {
                return ResponseEntity.ok("Password Matched.");
            } else {
                return ResponseEntity.status(401).body("Password NOT Matched.");
            }
        }

    }
}
