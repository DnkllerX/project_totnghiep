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
    private static final long RESET_EXPIRATION_MS = 30 * 60 * 1000L; // 30 phut, cho link "quen mat khau"
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

    /**
     * Token cho tinh nang "Quen mat khau". Nhung claim "pwdHash" = hash mat khau HIEN TAI cua user
     * luc sinh token - day la meo lam token "chi dung duoc 1 lan" MA KHONG CAN luu gi vao DB:
     * sau khi doi mat khau, hash thay doi -> so sanh o validatePasswordResetToken() se lech ->
     * token cu tu dong bi coi la khong hop le, du chua het han 30 phut.
     */
    public static String generatePasswordResetToken(int userId, String currentPasswordHash) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + RESET_EXPIRATION_MS);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("purpose", "password-reset")
                .claim("pwdHash", currentPasswordHash)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Payload da parse tu token "Quen mat khau" - SAU KHI da kiem tra chu ky/han dung/purpose,
     * NHUNG CHUA so sanh pwdHash voi DB (viec do la cua ben goi, vi phai query DB de lay hash
     * hien tai truoc). Dung class rieng thay vi tra ve Claims tho de tranh nham lan ten claim.
     */
    public static class ResetTokenPayload {
        public final int userId;
        public final String pwdHashClaim;
        public ResetTokenPayload(int userId, String pwdHashClaim) {
            this.userId = userId;
            this.pwdHashClaim = pwdHashClaim;
        }
    }
    /**
     * Parse + kiem tra chu ky/han dung/purpose cua token "Quen mat khau". KHONG kiem tra pwdHash
     * o day (ben goi phai tu query DB roi so sanh voi payload.pwdHashClaim - xem class ResetTokenPayload).
     * @return null neu token sai dinh dang / bi gia mao / het han / khong dung purpose
     */
    public static ResetTokenPayload parsePasswordResetToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return null; // het han
        } catch (JwtException | IllegalArgumentException e) {
            return null; // token sai dinh dang / bi gia mao
        }

        if (!"password-reset".equals(claims.get("purpose", String.class))) return null;
        String pwdHashClaim = claims.get("pwdHash", String.class);
        if (pwdHashClaim == null) return null;

        try {
            return new ResetTokenPayload(Integer.parseInt(claims.getSubject()), pwdHashClaim);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Token "xac nhan hanh dong nhay cam" (step-up confirmation) - dung khi IT thao tac len tai
     * khoan ADMIN/IT khac (dat lai mat khau, khoa tai khoan): thay vi thuc hien ngay, gui link
     * nay toi email cua CHINH tai khoan bi tac dong, chi khi ho tu bam xac nhan thi hanh dong moi
     * that su co hieu luc. Dung lai dung meo "anchor value" nhu token reset mat khau (khong can
     * bang DB rieng): anchorValue la 1 gia tri se THAY DOI ngay sau khi hanh dong duoc thuc hien
     * that (vd hash mat khau cho RESET_PASSWORD, status cho LOCK) - token cu tu dong het hieu luc
     * neu bi dung lai lan 2, hoac neu tinh trang tai khoan da doi khac di truoc khi ai do bam xac nhan.
     */
    public static String generateActionConfirmToken(int targetUserId, int actorUserId,
                                                      String action, String anchorValue) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + RESET_EXPIRATION_MS); // dung chung 30 phut voi reset mat khau
        return Jwts.builder()
                .subject(String.valueOf(targetUserId))
                .claim("purpose", "account-action-confirm")
                .claim("actorUserId", actorUserId)
                .claim("action", action)
                .claim("anchor", anchorValue)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static class ActionConfirmPayload {
        public final int targetUserId;
        public final int actorUserId;
        public final String action;
        public final String anchorClaim;
        public ActionConfirmPayload(int targetUserId, int actorUserId, String action, String anchorClaim) {
            this.targetUserId = targetUserId;
            this.actorUserId = actorUserId;
            this.action = action;
            this.anchorClaim = anchorClaim;
        }
    }

    /**
     * Parse + kiem tra chu ky/han dung/purpose cua token xac nhan hanh dong. KHONG so sanh anchor
     * voi DB o day (ben goi phai tu query DB roi so sanh voi payload.anchorClaim).
     * @return null neu token sai dinh dang / bi gia mao / het han / khong dung purpose
     */
    public static ActionConfirmPayload parseActionConfirmToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return null;
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }

        if (!"account-action-confirm".equals(claims.get("purpose", String.class))) return null;
        String action = claims.get("action", String.class);
        String anchorClaim = claims.get("anchor", String.class);
        Integer actorUserId = claims.get("actorUserId", Integer.class);
        if (action == null || anchorClaim == null || actorUserId == null) return null;

        try {
            return new ActionConfirmPayload(Integer.parseInt(claims.getSubject()), actorUserId, action, anchorClaim);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
