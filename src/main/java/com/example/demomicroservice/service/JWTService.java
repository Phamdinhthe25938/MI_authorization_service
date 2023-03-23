package com.example.demomicroservice.service;

import com.example.demomicroservice.model.entity.AppUser;
import com.obys.common.constant.Constants;
import com.obys.common.service.BaseService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.function.Function;

@Service("JWTService")
public class JWTService extends BaseService {


    public String createToken(User user) {
        return Jwts.builder()
                .setSubject((user.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + Constants.AuthService.EXPIRE_TIME * 1000))
                .signWith(SignatureAlgorithm.HS512, Constants.AuthService.KEY_PRIVATE)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(Constants.AuthService.KEY_PRIVATE)
                .parseClaimsJws(token)
                .getBody().getSubject();
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
