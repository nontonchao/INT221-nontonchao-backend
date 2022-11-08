package com.example.oasip_back_nontonchao.repositories;

import com.example.oasip_back_nontonchao.entities.Event;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByEventCategoryIdAndEventStartTimeIsBetween(Integer id, Instant dt, Instant dt2, Sort sort);

    List<Event> findByEventCategoryIdAndEventStartTimeIsBetweenAndIdIsNot(Integer cId, Instant dt, Instant dt2, Integer eId, Sort sort);

    @Query(value = "select * from event where eventStartTime like %:date% and eventCategory = :eventCategoryId", nativeQuery = true)
    List<Event> findAllByEventStartTime(String date, Integer eventCategoryId);

    @Query(value = "select e.* from event e join event_category_owner eco on e.eventCategory = eco.eventCategory_id where eco.user_id = :lecturer_id order by e.eventStartTime desc", nativeQuery = true)
    List<Event> findAllEventByLecturerCategory(Integer lecturer_id);

    @Query(value = "select e.* from event e join event_category_owner eco on e.eventCategory = eco.eventCategory_id where eco.user_id = :lecturer_id and e.event_id = :event_id order by e.eventStartTime desc", nativeQuery = true)
    Event findEventByLecturerCategory(Integer lecturer_id, Integer event_id);

    List<Event> findByBookingEmail(Sort sort, String email);

    Event findEventByAttachment(String name);

    @Query(value = "update event set attachment = :eAttachment where event_id = :eId",nativeQuery = true)
    @Transactional
    @Modifying
    void updateAttachment(@Param("eId") Integer id, @Param("eAttachment") String attachment);

    Event findEventByBookingEmailAndId(String email, Integer id);

    Event findByIdAndBookingEmail(Integer id, String email);
}
