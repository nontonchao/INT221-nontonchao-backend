package com.example.oasip_back_nontonchao.repositories;

import com.example.oasip_back_nontonchao.entities.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Integer> {
    List<EventCategory> findAllByEventCategoryNameAndIdIsNot(String name, Integer id);
}
