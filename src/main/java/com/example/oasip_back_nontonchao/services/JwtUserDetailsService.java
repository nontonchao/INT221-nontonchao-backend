package com.example.oasip_back_nontonchao.services;

import com.example.oasip_back_nontonchao.entities.User;
import com.example.oasip_back_nontonchao.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

        List<GrantedAuthority> role = new ArrayList<GrantedAuthority>();
        role.add(new SimpleGrantedAuthority("ROLE_" + s.getRole().toUpperCase()));
//        return new org.springframework.security.core.userdetails.User(s.getEmail(), s.getPassword(),
//                new ArrayList<>());
        return new org.springframework.security.core.userdetails.User(s.getEmail(), s.getPassword(),
                role);
    }
}
