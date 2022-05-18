package com.example.oasip_back_nontonchao.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Table(name = "event", indexes = {
        @Index(name = "fk_event_event_category_idx", columnList = "eventCategory")
})
@Entity
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Integer id;

    @NotBlank(message = "name shouldn't be blank or null")
    @Length(min = 1, max = 100, message = "size must be between 1 and 100")
    @Column(name = "bookingName", nullable = false, length = 100)
    private String bookingName;

    @NotBlank(message = "email shouldn't be blank or null")
    @Length(min = 1, max = 100, message = "size must be between 1 and 100")
    @Column(name = "bookingEmail", nullable = false)
    private String bookingEmail;


    @NotNull(message = "eventStartTime shouldn't be blank or null")
    @Future(message = "must be a future date")
    @Column(name = "eventStartTime", nullable = false)
    private Instant eventStartTime;

    @NotNull(message = "eventDuration shouldn't be blank or null")
    @Column(name = "eventDuration", nullable = false)
    private Integer eventDuration;

    @Length(min = 0, max = 500, message = "size must be between 0 and 500")
    @Column(name = "eventNotes", length = 500)
    private String eventNotes;


    @NotNull(message = "eventCategory shouldn't be blank or null")
    @ManyToOne(optional = false)
    @JoinColumn(name = "eventCategory", nullable = false)
    private EventCategory eventCategory;

}