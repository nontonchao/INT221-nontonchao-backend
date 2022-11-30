package com.example.oasip_back_nontonchao.repositories;

import com.example.oasip_back_nontonchao.entities.EventCategoryOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;


public interface EventCategoryOwnerRepository extends JpaRepository<EventCategoryOwner, Integer> {
    boolean existsEventCategoryOwnerByEventCategory_IdAndUser_Id(Integer eventCategory_id, Integer user_id);

    @Query(value = "select u.name , u.user_id , u.email from users u , event_category_owner eco where eco.user_id = u.user_id and eco.eventCategory_Id = :id", nativeQuery = true)
    List<Map<String, String>> getOwners(@Param("id") Integer id);

    @Query(value = "select u.user_id from users u , event_category_owner eco where eco.user_id = u.user_id and eco.eventCategory_Id = :id", nativeQuery = true)
    List<Integer> getOwnersId(@Param("id") Integer id);

    @Modifying
    @Transactional
    void deleteEventCategoryOwnersByEventCategoryIdAndUserId(Integer eventCate_id, Integer user_id);

    @Query(value = "delete from event_category_owner where user_id = :user_id", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteAssociateByUserId(@Param("user_id") Integer user_id);

    @Query(value = "insert into event_category_owner (eventCategory_Id , user_id) values (:eventCate_id,:user_id)", nativeQuery = true)
    @Transactional
    @Modifying
    void addEventCategoryOwner(@Param("eventCate_id") Integer eventCate_id, @Param("user_id") Integer user_id);
}
