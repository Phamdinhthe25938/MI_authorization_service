package com.example.demomicroservice.service;

import com.example.demomicroservice.model.dto.mapper.GetRole;
import com.example.demomicroservice.repository.IAppUserRepo;
import com.the.common.constant.Constants;
import com.the.common.service.BaseService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("JwtService")
public class JWTService extends BaseService {

  @Resource
  @Qualifier("IAppUserRepo")
  public IAppUserRepo iAppUserRepo;

  public String createToken(User user) {
    List<GetRole> roleList = iAppUserRepo.getRole(user.getUsername());
    List<String> roles = roleList.stream().map(GetRole::getRole).collect(Collectors.toList());
    StringBuilder role = new StringBuilder(roles.get(0));
    for (int i = 1; i < roles.size(); i++) {
      role.append(",").append(roles.get(i));
    }
    Claims claims = Jwts.claims();
    claims.put(Constants.AuthService.ROLE, role.toString());
    return Jwts.builder()
        .setClaims(claims)
        .setSubject((user.getUsername()))
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + Constants.AuthService.EXPIRE_TIME * 1000))
        .signWith(SignatureAlgorithm.HS512, Constants.AuthService.KEY_PRIVATE)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      return !ObjectUtils.isEmpty(getSubjectFromToken(token)) && !isTokenExpired(token);
    } catch (Exception ex) {
      return false;
    }
  }

  public String getTokenFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader(Constants.AuthService.AUTHORIZATION);
    if (authHeader != null && authHeader.startsWith(Constants.AuthService.BEARER)) {
      return authHeader.replace(Constants.AuthService.BEARER, "");
    }
    return null;
  }

  public String getSubjectFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = Jwts.parser().setSigningKey(Constants.AuthService.KEY_PRIVATE).parseClaimsJws(token).getBody();
    return claimsResolver.apply(claims);
  }

  private boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  private Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }
}
