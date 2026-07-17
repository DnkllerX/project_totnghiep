package com.shareholder.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT dung cho phien dang nhap (nhat la co dong ky nhan co phan qua API).
 * Secret key doc tu bien moi truong / system property, KHONG hardcode trong source
 * (neu khong tim thay, sinh ngau nhien moi lan start app - chi phu hop cho dev/test).
 */
public class JwtUtil {

    private static final long EXPIRATION_MS = 30 * 60 * 1000L; // 30 phut
    private static final SecretKey SECRET_KEY = resolveKey();

    private JwtUtil() {}

    private static SecretKey resolveKey() {
        String configured = System.getenv("JWT_SECRET");
        if (configured == null) configured = System.getProperty("jwt.secret");
        if (configured != null && configured.length() >= 32) {
            return Keys.hmacShaKeyFor(configured.getBytes());
        }
        // Fallback chi danh cho moi truong dev - CANH BAO: token se khong hop le sau khi restart app
        return Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
    }

    public static String generateToken(int userId, String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_MS);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(SECRET_KEY)
                .compact();
    }

    /** Tra ve Claims neu token hop le, null neu het han hoac sai chu ky/ dinh dang. */
    public static Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return null; // het han
        } catch (JwtException | IllegalArgumentException e) {
            return null; // token sai dinh dang / bi gia mao
        }
    }
}
