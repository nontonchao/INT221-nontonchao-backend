package com.example.oasip_back_nontonchao.repositories;

import com.example.oasip_back_nontonchao.entities.Event;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByEventCategoryIdAndEventStartTimeIsBetween(Integer id, Instant dt, Instant dt2, Sort sort);

    List<Event> findByEventCategoryIdAndEventStartTimeIsBetweenAndIdIsNot(Integer cId, Instant dt, Instant dt2, Integer eId, Sort sort);
}
