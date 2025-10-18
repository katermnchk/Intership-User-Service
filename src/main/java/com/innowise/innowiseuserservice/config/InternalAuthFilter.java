package com.innowise.innowiseuserservice.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InternalAuthFilter implements Filter {

  @Value("${internal.api.header-name}")
  private String headerName;

  @Value("${internal.api.secret}")
  private String secret;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String requestUri = httpRequest.getRequestURI();

    if (requestUri.startsWith("/api/v1/internal")) {
      String headerValue = httpRequest.getHeader(headerName);

      if (headerValue == null || !headerValue.equals(secret)) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        return;
      }
    }

    chain.doFilter(request, response);
  }
}