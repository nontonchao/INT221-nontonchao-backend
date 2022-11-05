package com.example.oasip_back_nontonchao.repositories;

import com.example.oasip_back_nontonchao.entities.EventCategoryOwner;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EventCategoryOwnerRepository extends JpaRepository<EventCategoryOwner, Integer> {
    boolean existsEventCategoryOwnerByEventCategory_IdAndUser_Id(Integer eventCategory_id, Integer user_id);

}
