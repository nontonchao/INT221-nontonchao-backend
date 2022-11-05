package com.example.oasip_back_nontonchao.repositories;

import com.example.oasip_back_nontonchao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "insert into users (name , email , role , password) values (:name,:email,:role,:password)", nativeQuery = true)
    @Transactional
    @Modifying
    void createUser(@Param("name") String name, @Param("email") String email, @Param("role") String role, @Param("password") String password);

    @Query(value = "update users set email = :uEmail where user_id = :uId", nativeQuery = true)
    @Transactional
    @Modifying
    void updateUser(@Param("uId") Integer id, @Param("uEmail") String email);

    User findUserByNameOrEmail(String name, String email);

    User findUserByEmail(String email);

    @Query(value = "select user_id from users where email = :email", nativeQuery = true)
    Integer findUserIdByEmail(@Param("email") String email);
}
