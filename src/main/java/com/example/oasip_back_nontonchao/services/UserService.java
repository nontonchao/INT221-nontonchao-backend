package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.entities.User;
import com.example.oasip_back_nontonchao.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public ResponseEntity createUser(User user) {
        if (!isUniqueCreate(user.getName(), user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("this name or this email already taken!");
        } else {
            userRepository.createUser(user.getName(), user.getEmail(), user.getRole());
            return ResponseEntity.status(HttpStatus.CREATED).body("user " + user.getName() + " created!");
        }
    }

    public User findUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user id '" + id + "' does not exist!"));
    }

    public ResponseEntity updateUser(Integer id, String email) {
        User user = findUserById(id);
        if (!isUniqueUpdate(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("this email already taken!");
        } else {
            userRepository.updateUser(id, email);
            return ResponseEntity.status(HttpStatus.OK).body("user id " + id + " updated!");
        }
    }


    //region email , name unique check
    public boolean isUniqueCreate(String name, String email) {
        if (userRepository.findUserByNameOrEmail(name, email) != null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isUniqueUpdate(String email) {
        if (userRepository.findUserByEmail(email) != null) {
            return false;
        } else {
            return true;
        }
    }
    //endregion
}
