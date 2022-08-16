package com.example.oasip_back_nontonchao.dtos;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserUpdate {

    @NotNull(message = "email shouldn't be null or blank")
    @NotBlank(message = "email shouldn't be null or blank")
    @Email(message = "invalid email format")
    @Length(min = 0, max = 50, message = "maximum 50")
    private String email;
}
