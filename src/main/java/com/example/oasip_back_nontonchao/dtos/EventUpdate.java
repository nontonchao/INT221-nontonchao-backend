package com.example.oasip_back_nontonchao.dtos;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Future;
import java.time.Instant;

@Getter
@Setter
public class EventUpdate {

    @Future(message = "must be a future date")
    private Instant eventStartTime;

    @Length(min = 0, max = 500, message = "size must be between 0 and 500")
    private String eventNotes;

}
