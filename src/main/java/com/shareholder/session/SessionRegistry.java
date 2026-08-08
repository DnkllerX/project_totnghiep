package com.shareholder.session;

import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Theo doi session HIEN TAI (moi nhat) cua tung userId, de gioi han 1 phien dang nhap dong thoi
 * cho moi tai khoan. Neu 1 tai khoan dang nhap o trinh duyet/thiet bi khac trong khi da co 1
 * phien dang hoat dong, phien CU se bi invalidate ngay lap tuc (bi dang xuat khoi noi cu).
 *
 * LUU Y VE GIOI HAN: day la in-memory singleton (ConcurrentHashMap), chi dung dung khi Tomcat
 * chay DUY NHAT 1 instance (dung kien truc hien tai: 1 VPS Ubuntu, khong load-balance nhieu
 * server). Neu sau nay scale ra nhieu Tomcat instance (vd qua load balancer), Map nay phai
 * chuyen sang luu o noi dung chung (DB rieng 1 bang SESSIONS, hoac Redis) - moi Map trong tung
 * instance rieng le se KHONG thay session dang o instance khac.
 */
public final class SessionRegistry {

    private static final Map<Integer, HttpSession> ACTIVE_SESSIONS = new ConcurrentHashMap<>();

    private SessionRegistry() {}

    /**
     * Dang ky session moi cho userId. Neu userId da co 1 session KHAC dang hoat dong (dang nhap
     * o noi khac), session cu se bi invalidate() ngay - dam bao tai moi thoi diem chi co dung
     * 1 phien dang nhap hop le cho 1 tai khoan.
     */
    public static void registerAndInvalidateOld(int userId, HttpSession newSession) {
        HttpSession old = ACTIVE_SESSIONS.put(userId, newSession);
        if (old != null && !old.getId().equals(newSession.getId())) {
            try {
                old.invalidate();
            } catch (IllegalStateException ignored) {
                // Session cu da bi invalidate/het han tu truoc do (vd timeout tu nhien) - bo qua.
            }
        }
    }

    /**
     * Goi tu SessionCleanupListener khi 1 session bi huy (logout, timeout, hoac bi ghi de boi
     * login moi o noi khac) de don Map, tranh giu rac session da chet.
     * Chi xoa neu dung la session nay dang duoc dang ky (tranh truong hop session cu bi huy
     * SAU KHI da bi ghi de boi session moi, lam mat nham session moi hon).
     */
    public static void unregister(int userId, HttpSession session) {
        ACTIVE_SESSIONS.remove(userId, session);
    }
}
