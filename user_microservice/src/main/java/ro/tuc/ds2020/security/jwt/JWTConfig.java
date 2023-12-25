package ro.tuc.ds2020.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.security.services.UserDetailsImpl;

import java.util.Date;
import java.util.Map;


@Component
public class JWTConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(JWTConfig.class);

    @Value("${security.jwt.expiration}")
    private int expiration;

    @Value("${security.jwt.secret}")
    private String secret;

    public boolean validate(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token).getBody();
            if (claims.get("authorized") == null || !claims.get("authorized", Boolean.class)) {
                return false;
            }
            return true;
        } catch (SignatureException e) {
            LOGGER.error("Invalid JWT signature: " + e);
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token: " + e);
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JWT token is unsupported: " + e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims string is empty: " + e);
        } catch (Exception e) {
            LOGGER.error("Token expired or something else: " + e);
        }
        return false;
    }

    public String generateToken(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setClaims(Map.of(
                        "sub", user.getUsername(),
                        "pass", user.getPassword(),
                        "authorized", true
                ))
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token).getBody().getSubject();
    }

    public String getPasswordFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token).getBody().get("pass", String.class);
    }


}
