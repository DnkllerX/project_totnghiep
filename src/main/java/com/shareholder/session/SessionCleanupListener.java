package com.shareholder.session;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * Don SessionRegistry moi khi 1 session bi huy - du la do logout thu cong, het han tu nhien
 * (30 phut khong hoat dong, xem web.xml <session-timeout>), hay bi ghi de boi 1 lan dang nhap
 * moi o noi khac (xem SessionRegistry.registerAndInvalidateOld). Khong can sua LogoutServlet
 * rieng - listener nay xu ly DONG NHAT ca 3 truong hop, vi ca 3 deu di qua sessionDestroyed().
 */
@WebListener
public class SessionCleanupListener implements HttpSessionListener {

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Object userIdAttr = se.getSession().getAttribute("userId");
        if (userIdAttr instanceof Integer userId) {
            SessionRegistry.unregister(userId, se.getSession());
        }
    }
}
