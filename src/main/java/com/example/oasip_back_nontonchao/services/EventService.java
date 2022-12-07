package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.dtos.EventDateDTO;
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

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private FileStorageService fileStorageService;

    private final HttpServletRequest request;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public EventService(HttpServletRequest request){
        this.request = request;
    }
    public ResponseEntity createEvent(Event req) {
        Event event = req;
        if (CategoryRepository.existsById(req.getEventCategory().getId())) {
            if (!CategoryRepository.existsByIdAndEventCategoryStatus(req.getEventCategory().getId(), Byte.parseByte("1"))) {
                return new ResponseEntity("eventCategory closed!", HttpStatus.BAD_REQUEST);
            }
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

    public ResponseEntity editEvent(EventUpdate update, Integer id) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(token);
        Event event = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event id '" + id + "' does not exist!"));
        Event toUpdate = event;
        toUpdate.setEventNotes(update.getEventNotes());
        toUpdate.setEventStartTime(update.getEventStartTime());
        Instant dt = update.getEventStartTime().minusSeconds(86400);
        Instant dt2 = update.getEventStartTime().plusSeconds(86400);
        List<Event> compare = repository.findByEventCategoryIdAndEventStartTimeIsBetweenAndIdIsNot(event.getEventCategory().getId(), dt, dt2, id, Sort.by(Sort.Direction.DESC, "eventStartTime"));

            if(!jwtTokenUtil.getRoleFromToken(token).equals("ROLE_ADMIN")) {
                if (event.getBookingEmail().equals(email)) {
                    if (checkOverlap(compare, toUpdate)) {
                        // file update
                        if (update.getAttachment() == null && event.getAttachment() != null) { // delete
                            fileStorageService.deleteFile(event.getAttachment());
                            toUpdate.setAttachment(null);
                        } else if (event.getAttachment() == null && update.getAttachment() != null) { // add file
                            toUpdate.setAttachment(update.getAttachment());
                        } else if ((update.getAttachment() != null) && (!update.getAttachment().equals(event.getAttachment()))) { // edit file
                            fileStorageService.deleteFile(event.getAttachment());
                            toUpdate.setAttachment(update.getAttachment());
                        }
                        //
                        repository.saveAndFlush(event);
                        return ResponseEntity.ok("Event Edited! || event id: " + event.getId());
                    } else{
                        return new ResponseEntity("eventStartTime is overlapped!", HttpStatus.BAD_REQUEST);
                    }
                    }else {
                        return new ResponseEntity("this event is not yours", HttpStatus.FORBIDDEN);
                    }
                } else {
                    if (update.getAttachment() == null && event.getAttachment() != null) { // delete
                        fileStorageService.deleteFile(event.getAttachment());
                        toUpdate.setAttachment(null);
                    } else if (event.getAttachment() == null && update.getAttachment() != null) { // add file
                        toUpdate.setAttachment(update.getAttachment());
                    } else if ((update.getAttachment() != null) && (!update.getAttachment().equals(event.getAttachment()))) { // edit file
                        fileStorageService.deleteFile(event.getAttachment());
                        toUpdate.setAttachment(update.getAttachment());
                    }
                    //
                    repository.saveAndFlush(event);
                    return ResponseEntity.ok("Event Edited! || event id: " + event.getId());
                }
            }

    public List<EventDateDTO> getEventDateDTO(String date, Integer eventCategoryId) {
        return listMapper.mapList(repository.findAllByEventStartTime(date, eventCategoryId), EventDateDTO.class, modelMapper);
    }

    public List<EventGet> getEventDTO() {
        String token =request.getHeader("Authorization").substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(token);
        switch (jwtTokenUtil.getRoleFromToken(token)) {
            case "ROLE_ADMIN":
                return listMapper.mapList(repository.findAll(Sort.by(Sort.Direction.DESC, "eventStartTime")), EventGet.class, modelMapper);
            case "ROLE_STUDENT":
                return listMapper.mapList(repository.findByBookingEmail(Sort.by(Sort.Direction.DESC, "eventStartTime"), email), EventGet.class, modelMapper);
            case "ROLE_LECTURER":
                User lecturer = userRepository.findUserByEmail(email);
                return listMapper.mapList(repository.findAllEventByLecturerCategory(lecturer.getId()), EventGet.class, modelMapper);
        }
        return null;
    }
     public ResponseEntity deleteEventFromId(String id) {
         String token =request.getHeader("Authorization").substring(7);
         String email = jwtTokenUtil.getUsernameFromToken(token);
         switch (jwtTokenUtil.getRoleFromToken(token)){
             case "ROLE_ADMIN":
                 if (repository.existsById(Integer.parseInt(id))) {
                 try {
                     fileStorageService.deleteFile(repository.findById(Integer.parseInt(id)).get().getAttachment());
                 } catch (Exception ex) {

                 }
                 repository.deleteById(Integer.parseInt(id));
                 return ResponseEntity.ok().body("event " + id + " deleted!");
             } else {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("event " + id + " not found!");
             }
             default:
                 if (repository.findByIdAndBookingEmail(Integer.parseInt(id), email) != null) {
                 try {
                     fileStorageService.deleteFile(repository.findById(Integer.parseInt(id)).get().getAttachment());
                 } catch (Exception ex) {
                 }
                 repository.deleteById(Integer.parseInt(id));
                 return ResponseEntity.ok().body("event " + id + " deleted!");
             } else {
                 return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This event is not yours");
             }
         }

    }

    public Event findEventById(Integer id) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(token);
        switch (jwtTokenUtil.getRoleFromToken(token)) {
            case "ROLE_ADMIN":
                return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event id '" + id + "' does not exist!"));
            case "ROLE_STUDENT":
                return repository.findEventByBookingEmailAndId(email, id);
            case "ROLE_LECTURER":
                User lecturer = userRepository.findUserByEmail(email);
                return repository.findEventByLecturerCategory(lecturer.getId(), id);
        }
        return null;
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
