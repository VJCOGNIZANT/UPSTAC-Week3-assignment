package org.upgrad.upstac.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.parser;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class TokenProvider implements Serializable {

    public static final long JWT_TOKEN_VALIDITY = 12 * 60 * 60;
    static final String AUTHORITIES_KEY = "scopes";
    private static final Logger log = getLogger(TokenProvider.class);
    @Value("${token.secret}")
    private String secretKey;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(Authentication authentication) {
        final String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(joining(","));

        log.info("authorities", authorities);
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(HS256, secretKey)
                .setIssuedAt(new Date(currentTimeMillis()))
                .setExpiration(new Date(currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .compact();
    }

    public void validateToken(String token, UserDetails userDetails) throws ServletException {
        final String username = getUsernameFromToken(token);
        if (username.equals(userDetails.getUsername()) == false)
            throw new ServletException("Invalid User Name");

        if (!isTokenExpired(token) == false)
            throw new ServletException("Token Expired");

    }

    UsernamePasswordAuthenticationToken getAuthentication(final String token, final Authentication existingAuth, final UserDetails userDetails) {

        final JwtParser jwtParser = parser().setSigningKey(secretKey);

        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

        final Claims claims = claimsJws.getBody();


        log.info("claims" + claims.get(AUTHORITIES_KEY).toString());
        final Collection<? extends GrantedAuthority> authorities =
                stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(toList());

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

}
