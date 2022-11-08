package com.example.oasip_back_nontonchao.dtos;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserUpdate {

    @NotNull(message = "name shouldn't be null or blank")
    @NotBlank(message = "name shouldn't be null or blank")
    @Length(min = 0, max = 100, message = "maximum 100")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Pattern(regexp = "admin|student|lecturer", message = "role only admin,student,lecturer")
    @NotNull(message = "role shouldn't be null or blank")
    @NotBlank(message = "role shouldn't be null or blank")
    @Lob
    @Column(name = "role", nullable = false)
    private String role;

}
