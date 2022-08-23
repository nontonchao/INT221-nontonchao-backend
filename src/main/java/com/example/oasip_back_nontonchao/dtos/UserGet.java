package com.example.oasip_back_nontonchao.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter

public class UserGet {
    private Integer id;
    private String name;
    private String email;
    private String role;
    private Instant onCreated;
    private Instant onUpdated;
}
