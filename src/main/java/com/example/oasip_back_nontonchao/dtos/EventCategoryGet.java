package com.example.oasip_back_nontonchao.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class EventCategoryGet {

    private Integer id;
    private String eventCategoryName;
    private String eventCategoryDescription;
    private Integer eventDuration;
    private List<String> Owners;
}
