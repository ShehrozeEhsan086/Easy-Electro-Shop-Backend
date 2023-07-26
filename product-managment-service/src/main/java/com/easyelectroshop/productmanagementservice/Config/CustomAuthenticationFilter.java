package com.easyelectroshop.productmanagementservice.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@Order(1)
public class CustomAuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        String headerValue = request.getHeader("Internal-Header");
        if (headerValue != null && headerValue.equals("ees-internal")) {
            filterChain.doFilter(request, response); // Proceed with the request
        } else {
            System.out.println("IRUN");
            // Return 403 Forbidden response for requests without the required header
            response.getWriter().write("Access forbidden!");
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}