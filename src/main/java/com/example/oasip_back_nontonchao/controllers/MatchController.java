package com.example.oasip_back_nontonchao.controllers;

import com.example.oasip_back_nontonchao.entities.JwtRequest;
import com.example.oasip_back_nontonchao.repositories.UserRepository;
import com.example.oasip_back_nontonchao.services.JwtUserDetailsService;
import com.example.oasip_back_nontonchao.utils.JwtTokenUtil;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin("*")
@RequestMapping("/api/login")
public class MatchController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("")
    public ResponseEntity check(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String name = userRepository.findUserByEmail(authenticationRequest.getEmail()).getName();
        final String token = jwtTokenUtil.generateToken(userDetails, name);
        String refresh_token = jwtTokenUtil.generateRefreshToken(userDetails, name);
        HashMap<String, String> res = new HashMap<String, String>();
        res.put("token", token);
        res.put("refresh_token", refresh_token);
        res.put("name", name);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) throws Exception {
        DefaultClaims claims = (io.jsonwebtoken.impl.DefaultClaims) request.getAttribute("claims");
        if (!(claims == null)) {
            Map<String, Object> expectedMap = getMapFromIoJsonwebtokenClaims(claims);
            HashMap<String, String> res = new HashMap<String, String>();
            String token = jwtTokenUtil.doGenerateRefreshToken(expectedMap, expectedMap.get("sub").toString());
            res.put("token", token);
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("This is not refresh");
    }

    public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }


    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}
