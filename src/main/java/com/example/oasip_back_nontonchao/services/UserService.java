package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.dtos.EventGet;
import com.example.oasip_back_nontonchao.dtos.UserGet;
import com.example.oasip_back_nontonchao.entities.User;
import com.example.oasip_back_nontonchao.repositories.UserRepository;
import com.example.oasip_back_nontonchao.utils.ListMapper;
import org.hibernate.boot.model.source.internal.hbm.CompositeIdentifierSingularAttributeSourceManyToOneImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
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
        return listMapper.mapList(userRepository.findAll(Sort.by(Sort.Direction.ASC, "name")),UserGet.class,modelMapper);
    }

    public ResponseEntity createUser(User user) {
        if (!isUniqueCreate(user.getName().trim(), user.getEmail().trim())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("this name or this email already taken!");
        } else {
           Argon2PasswordEncoder s = new Argon2PasswordEncoder();
           String encryptedPassword = s.encode(user.getPassword());
           userRepository.createUser(user.getName(), user.getEmail(), user.getRole() ,encryptedPassword);
           return ResponseEntity.status(HttpStatus.CREATED).body("user " + user.getName() + " created!");
        }
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
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
