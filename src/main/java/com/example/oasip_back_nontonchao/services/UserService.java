package com.example.oasip_back_nontonchao.services;

import com.azure.core.annotation.Get;
import com.example.oasip_back_nontonchao.dtos.AssociationGet;
import com.example.oasip_back_nontonchao.dtos.UserGet;
import com.example.oasip_back_nontonchao.dtos.UserUpdate;
import com.example.oasip_back_nontonchao.entities.EventCategoryOwner;
import com.example.oasip_back_nontonchao.entities.User;
import com.example.oasip_back_nontonchao.repositories.EventCategoryOwnerRepository;
import com.example.oasip_back_nontonchao.repositories.UserRepository;
import com.example.oasip_back_nontonchao.utils.JwtTokenUtil;
import com.example.oasip_back_nontonchao.utils.ListMapper;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EventCategoryOwnerRepository eventCategoryOwnerRepository;

    @Autowired
    @Getter
    @Setter
    private HttpServletRequest httpServletRequest;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public List<UserGet> getAllUsers() {
        return listMapper.mapList(userRepository.findAll(Sort.by(Sort.Direction.ASC, "name")), UserGet.class, modelMapper);
    }

    public List<UserGet> getAllLecturers() {
        return listMapper.mapList(userRepository.findAllByRole(Sort.by(Sort.Direction.ASC, "name"), "lecturer"), UserGet.class, modelMapper);
    }

    public ResponseEntity createUser(User user) {
        if (!isUniqueCreate(user.getName().trim(), user.getEmail().trim())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("this name or this email already taken!");
        } else {
            String passwordHash = passwordEncoder.encode(user.getPassword());
            userRepository.createUser(user.getName().stripTrailing().stripLeading(), user.getEmail(), user.getRole(), passwordHash);
            return ResponseEntity.status(HttpStatus.CREATED).body("user " + user.getName() + " created!");
        }
    }

    public AssociationGet getAssociate(Integer id) {

        List<String> cName = new ArrayList<>();
        List<String> aName = new ArrayList<>();

        List<EventCategoryOwner> eco = eventCategoryOwnerRepository.getEventCategoryOwnersByUserId(id);
        for (int i = 0; i < eco.toArray().length; i++) {
            List<EventCategoryOwner> ownersOfCategory = eventCategoryOwnerRepository.isOnlyOne(eco.get(i).getEventCategory().getId(), eco.get(i).getUser().getId());
            if (ownersOfCategory.toArray().length == 0) {
                cName.add(eco.get(i).getEventCategory().getEventCategoryName());
            }
            aName.add(eco.get(i).getEventCategory().getEventCategoryName());
        }

        AssociationGet asc = new AssociationGet(aName, cName);

        return asc;
    }

    public ResponseEntity deleteUser(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.get().getRole().equals("lecturer")) {
            if (isOnlyOne(eventCategoryOwnerRepository.getEventCategoryOwnersByUserId(id))) {
                eventCategoryOwnerRepository.deleteAssociateByUserId(id);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("eventCategory owner should have at least 1!");
            }
        }
        String email = jwtTokenUtil.getUsernameFromToken(httpServletRequest.getHeader("Authorization").substring(7));
        User s = userRepository.findUserByEmail(email);
        if (s.getId() == id) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("can't delete yourself");
        }
        userRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("user id: " + id + " deleted!");
    }

    public boolean isOnlyOne(List<EventCategoryOwner> eco) {
        for (int i = 0; i < eco.toArray().length; i++) {
            List<EventCategoryOwner> ownersOfCategory = eventCategoryOwnerRepository.isOnlyOne(eco.get(i).getEventCategory().getId(), eco.get(i).getUser().getId());
            if (ownersOfCategory.toArray().length == 0) {
                return false;
            }
        }
        return true;
    }

    public ResponseEntity checkEmail(String email) {
        if (isUniqueUpdate(email)) {
            return ResponseEntity.status(HttpStatus.OK).body("This email is available");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This email already exist");
    }

    public User findUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user id '" + id + "' does not exist!"));
    }

    public UserGet findUserByIdDTO(Integer id) {
        User d = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user id '" + id + "' does not exist!"));
        return modelMapper.map(d, UserGet.class);
    }

    public ResponseEntity updateUser(Integer id, UserUpdate user) {
        User user_ = findUserById(id);
        if (user_ == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user not found!");
        } else {
            userRepository.updateUser(id, user.getName(), user.getRole());
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
