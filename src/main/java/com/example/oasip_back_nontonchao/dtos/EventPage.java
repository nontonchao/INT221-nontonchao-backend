package com.example.oasip_back_nontonchao.dtos;

import com.example.oasip_back_nontonchao.entities.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventPage {
    private List<Event> content;
    private int number;
    private int size;
    private int totalPages;
    private int numberOfElements;
    private int totalElements;
    private boolean last;
    private boolean first;
}
