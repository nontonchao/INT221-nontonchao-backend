package com.example.oasip_back_nontonchao.filter;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.oasip_back_nontonchao.services.JwtUserDetailsService;
import com.example.oasip_back_nontonchao.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minidev.json.JSONUtil;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.json.JSONObject;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Getter
    @Setter
    static String jwtToken_;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            String isRefreshToken = request.getHeader("isRefreshToken");
            String requestURL = request.getRequestURL().toString();

            setJwtToken_(jwtToken);

            JSONObject payload = null;

            if (getJwtToken_() != null) {
                payload = extractMSJwt(getJwtToken_());
            }

            if (StringUtils.hasText(getJwtToken_()) == true && payload.getString("iss").equals("https://login.microsoftonline.com/6f4432dc-20d2-441d-b1db-ac3380ba633d/v2.0")) {
                String extract;
                try {
                    extract = payload.getString("roles").replaceAll("[^a-zA-Z]+", "");
                } catch (JSONException ex) {
                    extract = "GUEST";
                }
                setJwtToken_(jwtTokenUtil.doGenerateAccessToken(extract, payload.getString("preferred_username"), payload.getString("name"), 0).getJwttoken());
            }
            if (StringUtils.hasText(getJwtToken_()) == true && jwtTokenUtil.validateTokenn(getJwtToken_())) {
                List<GrantedAuthority> role = new ArrayList<GrantedAuthority>();
                role.add(new SimpleGrantedAuthority("ROLE_" + jwtTokenUtil.getRoleFromToken(getJwtToken_()).split("_")[1]));
                UserDetails userDetails = new User(jwtTokenUtil.getUsernameFromToken(getJwtToken_()), "", role);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                System.out.println("Please log in for get Token again.");
                request.setAttribute("message", "Please log in for get Token again.");
            }


            Claims claims = jwtTokenUtil.getClaimsFromToken(getJwtToken_());

            if (isRefreshToken != null && isRefreshToken.equals("true") && requestURL.contains("refresh")) {
                if (claims.getExpiration().getTime() - claims.getIssuedAt().getTime() > 1800000) { // check if this token is refresh_token
                    if (claims.getExpiration().getTime() > Instant.now().toEpochMilli()) {
                        allowForRefreshToken(claims, request);
                    }
                }
            }

            try {
                username = jwtTokenUtil.getUsernameFromToken(getJwtToken_());
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                if (e.getClaims().getExpiration().getTime() - e.getClaims().getIssuedAt().getTime() == 1800000) { // check if this token is access_token
                    System.out.println("JWT Token has expired");
                    request.setAttribute("message", "access_token has expired");
                } else {
                    System.out.println("JWT Refresh Token has expired");
                    request.setAttribute("message", "refresh_token expired try login again!");
                }
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String [" + request.getRequestURL() + "]");
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

    private void allowForRefreshToken(Claims claims, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(null, null, null);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        request.setAttribute("claims", claims);
    }

    @SneakyThrows
    public JSONObject extractMSJwt(String token) {
        String[] chunks = token.split("\\.");
        JSONObject header = new JSONObject(decode(chunks[0]));
        JSONObject payload = new JSONObject(decode(chunks[1]));
        String signature = decode(chunks[2]);
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
}


