package com.example.demomicroservice.config.jwt.fillter;


import com.example.demomicroservice.config.jwt.en_code.Base64EnCode;
import com.example.demomicroservice.service.AppUserService;
import com.example.demomicroservice.service.JWTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final static Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  @Resource
  private JWTService jwtService;

  @Resource
  private AppUserService appUserService;

  @Resource
  private Base64EnCode base64EnCode;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      String base64 = request.getHeader("En_code");
      String codeDecrypt = base64EnCode.decrypt(base64);
      if (codeDecrypt != null) {
        String token = jwtService.getTokenFromRequest(request);
        if (token != null) {
          if (jwtService.validateToken(token)) {
            String username = jwtService.getSubjectFromToken(token);
            if (username != null) {
              UserDetails userDetails = appUserService.loadUserByUsername(username);
              UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
              authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
              SecurityContextHolder.getContext().setAuthentication(authentication);
            }
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("Can NOT set user authentication -> Message: {}" + e.getMessage());
      return;
    }
    filterChain.doFilter(request, response);
  }

}