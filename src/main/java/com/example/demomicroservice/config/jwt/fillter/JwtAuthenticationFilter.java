package com.example.demomicroservice.config.jwt.fillter;


import com.example.demomicroservice.config.jwt.en_code.Base64EnCode;
import com.example.demomicroservice.service.AppUserService;
import com.example.demomicroservice.service.JWTService;
import com.the.common.constant.Constants;
import com.the.common.en_code.Base64Code;
import com.the.common.model.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nonnull;
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
  private Base64EnCode base64EnCode;

  @Override
  protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain chain) throws ServletException, IOException {
    LOGGER.info("Request go employee-service filter !");
    String codeDecrypt = base64EnCode.decrypt(request.getHeader(Base64Code.KEY), request.getHeader(Base64Code.BASE64_CODE));
    String userName;
    if (codeDecrypt != null) {
      String token = jwtService.getTokenFromRequest(request);
      if (token != null) {
        userName = jwtService.getSubjectFromToken(token);
        UserDetails userDetails = new CustomUserDetails(userName);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, request.getHeader(Constants.AuthService.AUTHORIZATION), userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LOGGER.info("Finish employee-service filter !");
      }
    }
    chain.doFilter(request, response);
  }
}