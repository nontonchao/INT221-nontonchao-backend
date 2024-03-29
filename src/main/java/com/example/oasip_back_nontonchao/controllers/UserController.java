package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.dtos.UserUpdate;
import com.example.oasip_back_nontonchao.entities.User;
import com.example.oasip_back_nontonchao.services.UserService;
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
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/create")
    public ResponseEntity createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity editUser(@PathVariable Integer id, @Valid @RequestBody UserUpdate user) {
        return userService.updateUser(id, user.getEmail());
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        return userService.findUserById(id);
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
