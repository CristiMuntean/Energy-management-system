package ro.tuc.ds2020.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenValidator {
    @Value("${security.jwt.expiration}")
    private int expiration;

    @Value("${security.jwt.secret}")
    private String secret;

    public boolean validate(String token) {
        String jwt = parseToken(token);
        if(jwt == null) {
            return false;
        }
        Claims claims = Jwts.parser().setSigningKey(secret).build().parseClaimsJws(jwt).getBody();
        if (claims.get("authorized") == null || !claims.get("authorized", Boolean.class)) {
            return false;
        }
        return true;
    }

    private String parseToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
