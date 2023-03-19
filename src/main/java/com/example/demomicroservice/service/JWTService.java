package com.example.demomicroservice.service;

import com.obys.common.constant.Constants;
import com.obys.common.service.BaseService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTService extends BaseService {

    // key để mã hóa token.

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(Constants.AuthService.KEY_PRIVATE)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public boolean validateToken(String token, String subject) {
        try {
            return getSubjectFromToken(token).equals(subject) && !isTokenExpired(token);
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
