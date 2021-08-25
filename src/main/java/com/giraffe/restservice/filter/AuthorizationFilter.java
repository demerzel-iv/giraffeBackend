package com.giraffe.restservice.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.giraffe.restservice.service.JwtService;

import org.springframework.beans.factory.annotation.Autowired;

@WebFilter
public class AuthorizationFilter implements Filter {

    JwtService jwtService;

    @Autowired
    AuthorizationFilter(JwtService jwtService){
        this.jwtService = jwtService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            if (!httpRequest.getRequestURI().startsWith("/api/auth")) {
                String jwtToken = httpRequest.getHeader("Authorization");
                int id;
                try {
                    id = jwtService.jwtTokenToId(jwtToken);
                } catch (Exception e) {
                    ((HttpServletResponse) response).setStatus(401);
                    return;
                }
                httpRequest.setAttribute("id", id);
            }

            chain.doFilter(request, response);
        }
        
    }
    
}
