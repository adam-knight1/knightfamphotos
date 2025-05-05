package com.fam.knightfam;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
//adding this class to figure out why https requests aren't terminating into htto requests
//at ALB and cognito redirect is returning an error.
@Component
public class HeaderLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("---- Incoming Request ----");
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Method: " + request.getMethod());
        System.out.println("Headers:");
        Collections.list(request.getHeaderNames()).forEach(header ->
                System.out.println(header + ": " + request.getHeader(header))
        );
        System.out.println("--------------------------");

        filterChain.doFilter(request, response);
    }
}
