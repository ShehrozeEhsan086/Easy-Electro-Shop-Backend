package com.easyelectroshop.amazons3service.Configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@Order(1)
public class CustomAuthenticationFilter extends GenericFilterBean {

    @Value("${internal.communication.header.name}")
    private String headerName;

    @Value("${internal.communication.header.value}")
    private String headerValue;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        String requestHeaderValue = request.getHeader(headerName);
        if (requestHeaderValue != null && requestHeaderValue.equals(headerValue)) {
            filterChain.doFilter(request, response); // Proceed with the request
        } else {
            // Return 403 Forbidden response for requests without the required header
            response.getWriter().write("Access forbidden!");
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}