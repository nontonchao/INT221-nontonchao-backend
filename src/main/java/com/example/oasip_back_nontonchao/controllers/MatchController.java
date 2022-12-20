package com.example.oasip_back_nontonchao.controllers;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.oasip_back_nontonchao.entities.JwtRequest;
import com.example.oasip_back_nontonchao.entities.User;
import com.example.oasip_back_nontonchao.repositories.UserRepository;
import com.example.oasip_back_nontonchao.services.JwtUserDetailsService;
import com.example.oasip_back_nontonchao.utils.JwtTokenUtil;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.SneakyThrows;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
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

    @Autowired
    private PasswordEncoder passwordEncoder;
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

    @SneakyThrows
    @PostMapping("/ms")
    public ResponseEntity checkMs(@RequestBody String msJwt) {
        JSONObject payload = extractMSJwt(msJwt);
        String extract;
        try {
            extract = payload.getString("roles").replaceAll("[^a-zA-Z]+", "");
        } catch (JSONException ex) {
            extract = "GUEST";
        }
        User c = userRepository.findUserByEmail(payload.getString("preferred_username"));

        if( c ==  null && extract != "GUEST"){
            userRepository.createUser(payload.getString("name"),payload.getString("preferred_username"),extract.toLowerCase(),passwordEncoder.encode(getAlphaNumericString(40)));
        }else{
            if(c.getRole() != extract){
                userRepository.updateUser(c.getId(),payload.getString("name"),extract);
            }
        }

        final String name = payload.getString("name");
        final String token = jwtTokenUtil.doGenerateAccessToken(extract, payload.getString("preferred_username"), payload.getString("name"), 0).getJwttoken();
        String refresh_token = jwtTokenUtil.doGenerateAccessToken(extract, payload.getString("preferred_username"), payload.getString("name"), 1).getJwttoken();

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

    @SneakyThrows
    public JSONObject extractMSJwt(String token) {
        String[] chunks = token.split("\\.");
        JSONObject payload = new JSONObject(decode(chunks[1]));
        if (payload.getString("iss").equals("https://login.microsoftonline.com/6f4432dc-20d2-441d-b1db-ac3380ba633d/v2.0")) {
            DecodedJWT jwt = JWT.decode(token);
            JwkProvider provider = new UrlJwkProvider(new URL("https://login.microsoftonline.com/common/discovery/keys"));
            Jwk jwk = provider.get(jwt.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            algorithm.verify(jwt);
        }
        return payload;
    }

    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    static String getAlphaNumericString(int n)
    {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }
}
