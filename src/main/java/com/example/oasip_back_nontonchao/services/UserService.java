package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.dtos.UserGet;
import com.example.oasip_back_nontonchao.entities.User;
import com.example.oasip_back_nontonchao.repositories.UserRepository;
import com.example.oasip_back_nontonchao.utils.ListMapper;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.modelmapper.ModelMapper;
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

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;

    public List<UserGet> getAllUsers() {
        return listMapper.mapList(userRepository.findAll(Sort.by(Sort.Direction.ASC, "name")), UserGet.class, modelMapper);
    }

    public ResponseEntity createUser(User user) {
        if (!isUniqueCreate(user.getName().trim(), user.getEmail().trim())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("this name or this email already taken!");
        } else {
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 8, 32);
            String passwordHash = argon2.hash(22, 65536, 1, user.getPassword());
            userRepository.createUser(user.getName(), user.getEmail(), user.getRole(), passwordHash);
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
