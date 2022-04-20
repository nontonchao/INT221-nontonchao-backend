package com.example.oasip_back_nontonchao.entities;

import javax.persistence.*;

@Table(name = "event_category", indexes = {
        @Index(name = "eventCategoryName", columnList = "eventCategoryName", unique = true)
})
@Entity
public class EventCategory {
    @Id
    @Column(name = "eventCategory_id", nullable = false)
    private Integer id;

    @Column(name = "eventCategoryName", nullable = false, length = 45)
    private String eventCategoryName;

    @Column(name = "eventCategoryDescription", length = 45)
    private String eventCategoryDescription;

    @Column(name = "eventDuration", nullable = false)
    private Integer eventDuration;

    public Integer getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(Integer eventDuration) {
        this.eventDuration = eventDuration;
    }

    public String getEventCategoryDescription() {
        return eventCategoryDescription;
    }

    public void setEventCategoryDescription(String eventCategoryDescription) {
        this.eventCategoryDescription = eventCategoryDescription;
    }

    public String getEventCategoryName() {
        return eventCategoryName;
    }

    public void setEventCategoryName(String eventCategoryName) {
        this.eventCategoryName = eventCategoryName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}