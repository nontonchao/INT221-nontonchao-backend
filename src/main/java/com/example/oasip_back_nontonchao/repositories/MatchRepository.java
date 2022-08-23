package com.example.oasip_back_nontonchao.repositories;

import com.example.oasip_back_nontonchao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
}
