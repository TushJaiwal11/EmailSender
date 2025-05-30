package com.tj.email.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JwtProvider {


    private static final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    public static String generateJwtToken(Authentication auth){

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String role = populateAuthorities(authorities);

        String jwt = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+86400000))
                .claim("email",auth.getName())
                .claim("authorities",role)
                .signWith(key)
                .compact();

        return jwt;
    }

    public static String getEmailFromJwtToken(String jwt){

        jwt=jwt.substring(7);
        Claims claims =Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

        return String.valueOf(claims.get("email"));
    }

    private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {

        Set<String> auth = new HashSet<>();

        for(GrantedAuthority authority : authorities){
            auth.add(authority.getAuthority());
        }

        return String.join(",", auth);
    }
}
