package com.example.oasip_back_nontonchao.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @NotFound
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @NotNull(message = "name shouldn't be null or blank")
    @NotBlank(message = "name shouldn't be null or blank")
    @Length(min = 0, max = 100, message = "maximum 100")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "email shouldn't be null or blank")
    @NotBlank(message = "email shouldn't be null or blank")
    @Email(message = "invalid email format")
    @Length(min = 0, max = 50, message = "maximum 50")
    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Pattern(regexp = "admin|student|lecturer", message = "role only admin,student,lecturer")
    @NotNull(message = "role shouldn't be null or blank")
    @NotBlank(message = "role shouldn't be null or blank")
    @Lob
    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "onCreated", nullable = false)
    private Instant onCreated;

    @Column(name = "onUpdated", nullable = false)
    private Instant onUpdated;
}