package com.example.oasip_back_nontonchao.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.*;

@Table(name = "event_category", indexes = {
        @Index(name = "eventCategoryName", columnList = "eventCategoryName", unique = true)
})
@Entity
@Getter
@Setter
public class EventCategory {
    @Id
    @Column(name = "eventCategory_id", nullable = false)
    private Integer id;

    @NotBlank(message = "eventCategoryName shouldn't be blank or null")
    @Length(min = 1, max = 100, message = "size must be between 1 and 100")
    @Column(name = "eventCategoryName", nullable = false, length = 100)
    private String eventCategoryName;

    @Length(min = 0, max = 500, message = "size must be between 0 and 500")
    @Column(name = "eventCategoryDescription", length = 500)
    private String eventCategoryDescription;

    @Min(1)
    @Max(480)
    @NotNull(message = "eventDuration shouldn't be blank or null")
    @Column(name = "eventDuration", nullable = false)
    private Integer eventDuration;

}