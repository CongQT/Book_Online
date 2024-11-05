package com.example.bookreadingonline.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Log4j2
public class JwtUtils {

  public static String generateJwtToken(
      String subject,
      LocalDateTime expiryTime,
      String jwtSecret,
      Map<String, Object> claims
  ) {
    SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    return Jwts.builder().subject(subject).claims(claims).issuedAt(new Date())
        .expiration(Timestamp.valueOf(expiryTime))
        .signWith(secretKey)
        .compact();
  }

  public static String generateJwtToken(
      String subject,
      LocalDateTime expiryTime,
      String jwtSecret
  ) {
    return generateJwtToken(subject, expiryTime, jwtSecret, Collections.emptyMap());
  }

  public static String getSubjectFromJwtToken(String token, String jwtSecret) {
    return getClaimsFromJwtToken(token, jwtSecret).getSubject();
  }

  public static Claims getClaimsFromJwtToken(String token, String jwtSecret) {
    SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    return (Claims) Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parse(token)
        .getPayload();
  }

}
