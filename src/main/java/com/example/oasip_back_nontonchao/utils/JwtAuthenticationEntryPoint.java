package com.example.oasip_back_nontonchao.utils;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        String res = null;
        try {
            res = request.getAttribute("message").toString();
        } catch (Exception e) {

        }
        if (!(res == null)) {
            response.setStatus(401);
            response.getOutputStream().print(res);
        }
        response.setStatus(401);
        response.getOutputStream().print(" Unauthorized");
    }

}
