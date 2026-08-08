package com.shareholder.filter;

import com.shareholder.model.enums.UserRole;
import com.shareholder.util.CsrfUtil;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Kiem tra dang nhap (session) va phan quyen theo role cho moi request vao /app/*.
 * Bang phan quyen khop dung voi bang "Phan quyen" trong tai lieu nghiep vu.
 * Filter nay duoc dang ky trong web.xml (khong dung @WebFilter de tranh dang ky trung).
 */
public class AuthFilter implements Filter {

    // Moi prefix duong dan -> tap hop role duoc phep truy cap
    private static final Map<String, Set<UserRole>> ROLE_RULES = Map.ofEntries(
            Map.entry("/app/it/user-management", Set.of(UserRole.IT)),
            Map.entry("/app/admin/shareholders", Set.of(UserRole.ADMIN)),
            Map.entry("/app/admin/share-adjust", Set.of(UserRole.ADMIN)),
            Map.entry("/app/admin/share-adjustment-history", Set.of(UserRole.ADMIN)),
            Map.entry("/app/admin/transfer-approval", Set.of(UserRole.ADMIN)),
            Map.entry("/app/admin/share-issue", Set.of(UserRole.ADMIN)),
            Map.entry("/app/admin/resolution", Set.of(UserRole.ADMIN)),
            Map.entry("/app/admin/documents", Set.of(UserRole.ADMIN)),
            Map.entry("/app/it/documents", Set.of(UserRole.IT)),
            Map.entry("/app/it/system-history", Set.of(UserRole.IT)),
            Map.entry("/app/admin/financial-reports/manage", Set.of(UserRole.ADMIN)),
            Map.entry("/app/shareholder/notifications", Set.of(UserRole.SHAREHOLDER)),
            Map.entry("/app/shareholder/sign", Set.of(UserRole.SHAREHOLDER)),
            Map.entry("/app/shareholder/vote", Set.of(UserRole.SHAREHOLDER)),
            Map.entry("/app/shareholder/profile", Set.of(UserRole.SHAREHOLDER)),
            Map.entry("/app/shareholder/transfer-request", Set.of(UserRole.SHAREHOLDER)),
            Map.entry("/app/shareholder/documents", Set.of(UserRole.SHAREHOLDER)),
            Map.entry("/app/shareholder/financial-reports", Set.of(UserRole.SHAREHOLDER))
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Chan browser cache / bfcache cho MOI trang trong /app/* - neu khong, sau khi logout,
        // bam nut Back tren trinh duyet se hien lai trang admin/shareholder cu tu cache thay vi
        // goi lai server (ma le ra phai bi AuthFilter chan vi session da invalidate).
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        HttpSession session = req.getSession(false);
        String requestPath = req.getRequestURI().substring(req.getContextPath().length());

        if (session == null || session.getAttribute("userId") == null) {
            redirectToLogin(req, res);
            return;
        }

        // Tai khoan chua duyet gio dung status=LOCKED (khong con role GUEST), nen bi chan ngay
        // luc dang nhap (AuthService.login tra ACCOUNT_LOCKED) - o day chi con can parse role hop le.
        String roleStr = (String) session.getAttribute("role");
        UserRole role;
        try {
            role = UserRole.valueOf(roleStr);
        } catch (Exception e) {
            invalidateAndRedirect(req, res, session);
            return;
        }

        // Dam bao session da co san 1 CSRF token de JSP lay ra nhung form (getOrCreateToken
        // tu tao neu chua co, vd session vua duoc tao luc login).
        CsrfUtil.getOrCreateToken(session);

        // Chan CSRF cho moi request thay doi du lieu (POST/PUT/DELETE/PATCH). GET/HEAD/OPTIONS
        // khong doi trang thai server nen khong can token.
        String method = req.getMethod();
        if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)
                && !"OPTIONS".equalsIgnoreCase(method)) {
            if (!CsrfUtil.isValid(req, session)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Phien lam viec khong hop le hoac da het han, vui long tai lai trang va thu lai");
                return;
            }
        }

        // Kiem tra rule cu the cho tung nhom duong dan
        for (Map.Entry<String, Set<UserRole>> rule : ROLE_RULES.entrySet()) {
            if (requestPath.startsWith(rule.getKey())) {
                if (!rule.getValue().contains(role)) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "Ban khong co quyen truy cap chuc nang nay");
                    return;
                }
                break;
            }
        }

        chain.doFilter(request, response);
    }

    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.sendRedirect(req.getContextPath() + "/login");
    }

    private void invalidateAndRedirect(HttpServletRequest req, HttpServletResponse res, HttpSession session)
            throws IOException {
        session.invalidate();
        redirectToLogin(req, res);
    }
}
