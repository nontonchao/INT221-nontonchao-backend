package com.example.oasip_back_nontonchao.filter;

import com.example.oasip_back_nontonchao.services.JwtUserDetailsService;
import com.example.oasip_back_nontonchao.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            String isRefreshToken = request.getHeader("isRefreshToken");
            String requestURL = request.getRequestURL().toString();
            Claims claims = jwtTokenUtil.getClaimsFromToken(jwtToken);

            if (isRefreshToken != null && isRefreshToken.equals("true") && requestURL.contains("refresh")) {
                if (claims.getExpiration().getTime() - claims.getIssuedAt().getTime() > 1800000) { // check if this token is refresh_token
                    if (claims.getExpiration().getTime() > Instant.now().toEpochMilli()) {
                        allowForRefreshToken(claims, request);
                    }

                }
            }

            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
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
}


