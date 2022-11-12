package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.dtos.EventCategoryGet;
import com.example.oasip_back_nontonchao.entities.EventCategory;
import com.example.oasip_back_nontonchao.entities.EventCategoryOwner;
import com.example.oasip_back_nontonchao.repositories.EventCategoryOwnerRepository;
import com.example.oasip_back_nontonchao.repositories.EventCategoryRepository;
import com.example.oasip_back_nontonchao.repositories.UserRepository;
import com.example.oasip_back_nontonchao.utils.ListMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventCategoryService {
    @Autowired
    private EventCategoryRepository repository;

    @Autowired
    private EventCategoryOwnerRepository eventCategoryOwnerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;


    public List<EventCategoryOwner> getEventCategoryOwner() {
        return eventCategoryOwnerRepository.findAll();
    }

    public ResponseEntity deleteEventCategoryOwner(Integer eventCate_id, Integer user_id) {
        eventCategoryOwnerRepository.deleteEventCategoryOwnersByEventCategoryIdAndUserId(eventCate_id, user_id);
        return ResponseEntity.status(HttpStatus.OK).body("eventCategory owner deleted!");
    }

    public ResponseEntity addEventCategoryOwner(Integer eventCate_id, Integer user_id) {
        if (eventCategoryOwnerRepository.existsEventCategoryOwnerByEventCategory_IdAndUser_Id(eventCate_id, user_id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("eventCategory owner already exists!");
        } else {
            eventCategoryOwnerRepository.addEventCategoryOwner(eventCate_id, user_id);
            return ResponseEntity.status(HttpStatus.OK).body("eventCategory owner added!");
        }
    }

    public List<EventCategoryGet> getEventCategory() {
        List<EventCategoryGet> eventCategories = listMapper.mapList(repository.findAll(), EventCategoryGet.class, modelMapper);
        eventCategories.forEach(eventCategory -> {
            eventCategory.setOwners(eventCategoryOwnerRepository.getOwners(eventCategory.getId()));
        });
        return eventCategories;
    }


    public ResponseEntity editEventCategory(EventCategory update, Integer id, String email) {
        Optional<EventCategory> s = repository.findById(id);
        if (!s.isEmpty()) {
            List<EventCategory> toCheck = repository.findAllByEventCategoryNameAndIdIsNot(update.getEventCategoryName().stripLeading().stripTrailing(), id);
            if (toCheck.stream().count() == 0) {
                if (eventCategoryOwnerRepository.existsEventCategoryOwnerByEventCategory_IdAndUser_Id(id, userRepository.findUserIdByEmail(email))) {
                    s.get().setEventCategoryName(update.getEventCategoryName());
                    s.get().setEventDuration(update.getEventDuration());
                    s.get().setEventCategoryDescription(update.getEventCategoryDescription());
                    repository.saveAndFlush(s.get());
                } else {
                    return new ResponseEntity("this eventCategory is not yours!", HttpStatus.UNAUTHORIZED);
                }
                return ResponseEntity.ok("EventCategory Edited! || eventCategory id: " + s.get().getId());
            } else {
                return new ResponseEntity("eventCategoryName should be unique", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity("eventCategory not found!", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity editEventCategoryAdmin(EventCategory update, Integer id) {
        Optional<EventCategory> s = repository.findById(id);
        if (!s.isEmpty()) {
            List<EventCategory> toCheck = repository.findAllByEventCategoryNameAndIdIsNot(update.getEventCategoryName().stripLeading().stripTrailing(), id);
            if (toCheck.stream().count() == 0) {
                s.get().setEventCategoryName(update.getEventCategoryName());
                s.get().setEventDuration(update.getEventDuration());
                s.get().setEventCategoryDescription(update.getEventCategoryDescription());
                repository.saveAndFlush(s.get());
                return ResponseEntity.ok("EventCategory Edited! || eventCategory id: " + s.get().getId());
            } else {
                return new ResponseEntity("eventCategoryName should be unique", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity("eventCategory not found!", HttpStatus.BAD_REQUEST);
        }
    }
}
