package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.dtos.EventGet;
import com.example.oasip_back_nontonchao.dtos.EventUpdate;
import com.example.oasip_back_nontonchao.entities.Event;
import com.example.oasip_back_nontonchao.entities.User;
import com.example.oasip_back_nontonchao.repositories.EventCategoryRepository;
import com.example.oasip_back_nontonchao.repositories.EventRepository;
import com.example.oasip_back_nontonchao.repositories.UserRepository;
import com.example.oasip_back_nontonchao.utils.JwtTokenUtil;
import com.example.oasip_back_nontonchao.utils.ListMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository repository;

    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EventCategoryRepository CategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender emailSender;

    public ResponseEntity createEvent(Event req) {
        Event event = req;
        if (CategoryRepository.existsById(req.getEventCategory().getId())) {
            Instant dt = req.getEventStartTime().minusSeconds(86400);
            Instant dt2 = req.getEventStartTime().plusSeconds(86400);
            List<Event> compare = repository.findByEventCategoryIdAndEventStartTimeIsBetween(event.getEventCategory().getId(), dt, dt2, Sort.by(Sort.Direction.DESC, "eventStartTime"));
            if (checkOverlap(compare, req)) {
                event.setBookingName(event.getBookingName().stripTrailing().stripLeading());
                repository.saveAndFlush(event);
                // send email (new thread)
                Thread s = new Thread(() -> {
                    DateTimeFormatter dFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    String date = Instant.ofEpochMilli(req.getEventStartTime().toEpochMilli()).atZone(ZoneId.systemDefault()).format(dFormat);
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom("oasipsy1@gmail.com");
                    message.setTo(req.getBookingEmail());
                    message.setSubject("การจองนัดหมายของคุณสำเร็จแล้ว! OASIP-SY1");
                    message.setText("สวัสดี คุณ " + req.getBookingName() + ",\n" + "\n" + "การจองนัดหมายของคุณที่ " + CategoryRepository.findNameById(req.getEventCategory().getId()) + " วันที่ " + date.split(" ")[0] + " เวลา " + date.split(" ")[1].substring(0, 5) + " ระยะเวลา " + req.getEventDuration() + " นาที ถูกจองสำเร็จแล้ว!\n" + "\n" + "ขอบคุณสำหรับการจองนัดหมายกับเรา\n" + "OASIP-SY1 TEAM\n");
                    emailSender.send(message);
                });
                s.start();
                //
                return ResponseEntity.status(HttpStatus.CREATED).body("Event Added! || event id: " + event.getId());
            }
            return new ResponseEntity("eventStartTime is overlapped!", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity("eventCategory not found!", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity editEventAdmin(EventUpdate update, Integer id) {
        Event event = findEventById(id);
        Event toUpdate = event;
        toUpdate.setEventNotes(update.getEventNotes());
        toUpdate.setEventStartTime(update.getEventStartTime());
        Instant dt = update.getEventStartTime().minusSeconds(86400);
        Instant dt2 = update.getEventStartTime().plusSeconds(86400);
        List<Event> compare = repository.findByEventCategoryIdAndEventStartTimeIsBetweenAndIdIsNot(event.getEventCategory().getId(), dt, dt2, id, Sort.by(Sort.Direction.DESC, "eventStartTime"));
        if (checkOverlap(compare, toUpdate)) {
            repository.saveAndFlush(event);
            return ResponseEntity.ok("Event Edited! || event id: " + event.getId());
        }
        return new ResponseEntity("eventStartTime is overlapped!", HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity editEvent(EventUpdate update, Integer id, String email) {
        Event event = findEventById(id);
        Event toUpdate = event;
        toUpdate.setEventNotes(update.getEventNotes());
        toUpdate.setEventStartTime(update.getEventStartTime());
        Instant dt = update.getEventStartTime().minusSeconds(86400);
        Instant dt2 = update.getEventStartTime().plusSeconds(86400);
        List<Event> compare = repository.findByEventCategoryIdAndEventStartTimeIsBetweenAndIdIsNot(event.getEventCategory().getId(), dt, dt2, id, Sort.by(Sort.Direction.DESC, "eventStartTime"));
        if (checkOverlap(compare, toUpdate)) {
            if (event.getBookingEmail().equals(email)) {
                repository.saveAndFlush(event);
                return ResponseEntity.ok("Event Edited! || event id: " + event.getId());
            } else {
                return new ResponseEntity("this event is not yours", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity("eventStartTime is overlapped!", HttpStatus.BAD_REQUEST);
    }

    public List<EventGet> getEventDateDTO(String date, Integer eventCategoryId) {
        System.out.println(date);
        return listMapper.mapList(repository.findAllByEventStartTime(date, eventCategoryId), EventGet.class, modelMapper);
    }

    public List<EventGet> getEventDTO() {
        return listMapper.mapList(repository.findAll(Sort.by(Sort.Direction.DESC, "eventStartTime")), EventGet.class, modelMapper);
    }

    public List<EventGet> getEventByEmailDTO(String email) {

        return listMapper.mapList(repository.findByBookingEmail(Sort.by(Sort.Direction.DESC, "eventStartTime"), email), EventGet.class, modelMapper);
    }

    public List<EventGet> getAllEventLecturer(String email) {
        User lecturer = userRepository.findUserByEmail(email);
        return listMapper.mapList(repository.findAllEventByLecturerCategory(lecturer.getId()), EventGet.class, modelMapper);
    }

    public Event getEventLecturer(String email, Integer id) {
        User lecturer = userRepository.findUserByEmail(email);
        return repository.findEventByLecturerCategory(lecturer.getId(), id);
    }

    public ResponseEntity deleteEventFromId(String id) {
        if (repository.existsById(Integer.parseInt(id))) {
            repository.deleteById(Integer.parseInt(id));
            return ResponseEntity.ok().body("event " + id + " deleted!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("event " + id + " not found!");
        }
    }

    public ResponseEntity deleteEventFromIdAndEmail(String id, String email) {
        if (repository.findByIdAndBookingEmail(Integer.parseInt(id), email) != null) {
            repository.deleteById(Integer.parseInt(id));
            return ResponseEntity.ok().body("event " + id + " deleted!");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This event is not yours");
        }
    }

    public Event findEventById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event id '" + id + "' does not exist!"));
    }


    public Event findEventByEmailAndId(String email, Integer id) {
        return repository.findEventByBookingEmailAndId(email, id);
    }

    private boolean checkOverlap(List<Event> a, Event b) {
        for (Event cmp : a) {
            if (!((b.getEventStartTime().toEpochMilli() >= getEventMilli(cmp) || (getEventMilli(b) <= cmp.getEventStartTime().toEpochMilli()) && getEventMilli(b) != getEventMilli(cmp)))) {
                return false;
            }
        }
        return true;
    }

    private long getEventMilli(Event q) {
        return ((q.getEventStartTime().toEpochMilli() + (q.getEventDuration() * 60000))) + 300000;
    }
}
