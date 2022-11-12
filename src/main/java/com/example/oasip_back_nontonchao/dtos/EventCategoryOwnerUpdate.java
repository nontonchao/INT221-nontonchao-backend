package com.example.oasip_back_nontonchao.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventCategoryOwnerUpdate {
    private Integer eventCategory_id;
    private int[] user_id;
}
