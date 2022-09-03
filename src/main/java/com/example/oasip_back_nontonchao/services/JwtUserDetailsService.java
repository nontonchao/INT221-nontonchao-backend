package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.entities.User;
import com.example.oasip_back_nontonchao.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository user;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User s = user.findUserByEmail(username);
        if (s == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        return new org.springframework.security.core.userdetails.User(s.getEmail(), s.getPassword(),
                new ArrayList<>());
    }
}
