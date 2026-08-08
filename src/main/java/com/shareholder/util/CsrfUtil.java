package com.shareholder.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Sinh va kiem tra CSRF token, luu trong session (KHONG can luu DB - token chi
 * can song trong pham vi 1 session, giong cach userId/role dang duoc luu).
 * Moi session dung DUY NHAT 1 token trong suot vong doi (synchronizer token pattern
 * co dien), sinh 1 lan luc session duoc tao/dang nhap thanh cong.
 */
public final class CsrfUtil {

    public static final String SESSION_ATTR = "csrfToken";
    public static final String PARAM_NAME = "csrfToken";

    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfUtil() {
    }

    /** Lay token hien co trong session, sinh moi neu chua co (goi luc render form). */
    public static String getOrCreateToken(HttpSession session) {
        String token = (String) session.getAttribute(SESSION_ATTR);
        if (token == null) {
            token = generateToken();
            session.setAttribute(SESSION_ATTR, token);
        }
        return token;
    }

    private static String generateToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Kiem tra token gui len (tu param form, hoac header X-CSRF-TOKEN cho request AJAX/JSON)
     * co khop voi token luu trong session khong. Dung so sanh constant-time de tranh timing attack.
     */
    public static boolean isValid(HttpServletRequest req, HttpSession session) {
        if (session == null) {
            return false;
        }
        String sessionToken = (String) session.getAttribute(SESSION_ATTR);
        if (sessionToken == null) {
            return false;
        }
        String submitted = req.getParameter(PARAM_NAME);
        if (submitted == null) {
            submitted = req.getHeader("X-CSRF-TOKEN");
        }
        if (submitted == null) {
            return false;
        }
        return constantTimeEquals(sessionToken, submitted);
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }
}
