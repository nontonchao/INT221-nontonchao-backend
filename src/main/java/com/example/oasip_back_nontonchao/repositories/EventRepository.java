package com.example.oasip_back_nontonchao.repositories;

import com.example.oasip_back_nontonchao.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllByBookingEmail(String email);
}
