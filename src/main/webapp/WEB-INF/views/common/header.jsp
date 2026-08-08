<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<%@ include file="/WEB-INF/views/common/ga4.jsp" %>
    <meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Hệ thống Quản lý Cổ đông - VinScape</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <!-- Giu lai stylesheet cu de khong pha vo cac trang shareholder khac (dashboard, sign, vote,
         profile, financial-reports) dang con dung class cua no cho phan NOI DUNG trang.
         Sidebar/topbar o duoi da doi sang thiet ke moi (giong ADMIN/IT), doc lap voi file nay. -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* Dung CHUNG 1 he thong thiet ke voi sidebar cua ADMIN/IT (cung class, cung font Inter,
           cung mau sac) - chi khac phan menu (SHAREHOLDER) va noi dung trang. */
        body, html {
            margin: 0; padding: 0; box-sizing: border-box;
            font-family: 'Inter', sans-serif;
            background-color: #f8fafc;
            height: 100vh;
            overflow: hidden;
        }
        .app-layout { display: flex; height: 100vh; width: 100vw; }

        .app-sidebar {
            width: 250px; background-color: #0f172a;
            display: flex; flex-direction: column; flex-shrink: 0;
            border-right: 1px solid #1e293b;
        }
        .sb-brand {
            padding: 24px; font-size: 20px; font-weight: 800; letter-spacing: 1px;
            color: #38bdf8; border-bottom: 1px solid rgba(255,255,255,0.05);
        }
        .sb-nav { flex: 1; overflow-y: auto; padding-top: 16px; }
        .sb-nav::-webkit-scrollbar { width: 4px; }
        .sb-nav::-webkit-scrollbar-thumb { background: #334155; border-radius: 4px; }
        .nav-section {
            padding: 16px 24px 8px; font-size: 11px; font-weight: 600;
            color: #64748b; text-transform: uppercase; letter-spacing: 0.5px;
        }
        .nav-item {
            display: flex; align-items: center; gap: 12px;
            padding: 12px 24px; color: #cbd5e1; text-decoration: none;
            font-size: 13.5px; font-weight: 500; transition: all 0.2s;
            border-left: 3px solid transparent;
        }
        .nav-item:hover { background-color: rgba(255,255,255,0.03); color: #ffffff; }
        .nav-item.active {
            background-color: rgba(56, 189, 248, 0.1); color: #38bdf8;
            border-left-color: #38bdf8; font-weight: 600;
        }
        .nav-icon { width: 18px; height: 18px; opacity: 0.8; flex-shrink: 0; }

        .app-main { flex: 1; display: flex; flex-direction: column; overflow: hidden; background-color: #f8fafc; }

        .app-topbar {
            height: 64px; background-color: #ffffff; border-bottom: 1px solid #e2e8f0;
            display: flex; align-items: center; justify-content: flex-end;
            padding: 0 32px; flex-shrink: 0;
        }
        .tb-user { font-size: 13.5px; color: #64748b; margin-right: 20px; }
        .tb-user strong { color: #0f172a; font-weight: 600; }
        .btn-logout {
            border: 1px solid #ef4444; color: #ef4444; background: transparent;
            padding: 6px 16px; border-radius: 6px; font-size: 13px; font-weight: 600;
            text-decoration: none; transition: all 0.2s;
        }
        .btn-logout:hover { background-color: #fef2f2; }

        .vertical-dashboard {
            flex: 1; overflow-y: auto; padding: 32px 40px; box-sizing: border-box;
            background-color: #f8fafc;
        }
        .vertical-dashboard::-webkit-scrollbar { width: 6px; }
        .vertical-dashboard::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }

        /* An navbar ngang cu neu style.css cu con dinh nghia */
        .navbar { display: none !important; }
    </style>
</head>
<body>

<c:set var="uri" value="${requestScope['jakarta.servlet.forward.request_uri']}" />
<c:if test="${empty uri}"><c:set var="uri" value="${pageContext.request.requestURI}" /></c:if>

<div class="app-layout">
    <aside class="app-sidebar">
        <div class="sb-brand" style="display:flex; align-items:center; gap:10px;">
            <img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo" style="width:26px; height:26px; object-fit:contain; border-radius:4px;">
            <span>VINSCAPE</span>
        </div>

        <div class="sb-nav">
            <!-- ADMIN MENU -->
            <c:if test="${sessionScope.role == 'ADMIN'}">
                <div class="nav-section">TỔNG QUAN</div>
                <a href="${pageContext.request.contextPath}/app/dashboard" class="nav-item ${fn:contains(uri, '/dashboard') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path></svg>
                    Bảng Điều Khiển
                </a>
                <div class="nav-section">QUẢN TRỊ CỔ ĐÔNG</div>
                <a href="${pageContext.request.contextPath}/app/admin/shareholders" class="nav-item ${fn:contains(uri, '/shareholders') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle></svg>
                    Quản lý Cổ đông
                </a>
                <a href="${pageContext.request.contextPath}/app/admin/transfer-approval" class="nav-item ${fn:contains(uri, '/transfer-approval') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="17 1 21 5 17 9"></polyline><path d="M3 11V9a4 4 0 0 1 4-4h14"></path><polyline points="7 23 3 19 7 15"></polyline><path d="M21 13v2a4 4 0 0 1-4 4H3"></path></svg>
                    Duyệt Chuyển nhượng
                </a>
                <a href="${pageContext.request.contextPath}/app/admin/share-issue" class="nav-item ${fn:contains(uri, '/share-issue') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"></rect><line x1="12" y1="8" x2="12" y2="16"></line><line x1="8" y1="12" x2="16" y2="12"></line></svg>
                    Phát hành & Cổ tức
                </a>
                <a href="${pageContext.request.contextPath}/app/admin/resolution" class="nav-item ${fn:contains(uri, '/resolution') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
                    Nghị quyết Biểu quyết
                </a>
                <a href="${pageContext.request.contextPath}/app/admin/share-adjust" class="nav-item ${fn:contains(uri, '/share-adjust') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82"></path></svg>
                    Điều chỉnh Cổ phần
                </a>
                <div class="nav-section">DỮ LIỆU HỆ THỐNG</div>
                <a href="${pageContext.request.contextPath}/app/admin/documents" class="nav-item ${fn:contains(uri, '/documents') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline></svg>
                    Tài liệu Hệ thống
                </a>
                <a href="${pageContext.request.contextPath}/app/admin/financial-reports/manage" class="nav-item ${fn:contains(uri, '/financial-reports') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"></line><line x1="12" y1="20" x2="12" y2="4"></line><line x1="6" y1="20" x2="6" y2="14"></line></svg>
                    Báo cáo Tài chính
                </a>
            </c:if>

            <!-- IT MENU -->
            <c:if test="${sessionScope.role == 'IT'}">
                <div class="nav-section">ĐIỀU HÀNH KỸ THUẬT (IT)</div>
                <a href="${pageContext.request.contextPath}/app/dashboard" class="nav-item ${fn:contains(uri, '/dashboard') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path></svg>
                    Bảng Điều Khiển
                </a>
                <a href="${pageContext.request.contextPath}/app/it/user-management" class="nav-item ${fn:contains(uri, '/user-management') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle></svg>
                    Quản lý Người dùng
                </a>
                <a href="${pageContext.request.contextPath}/app/admin/documents" class="nav-item ${fn:contains(uri, '/documents') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline></svg>
                    Tài liệu Hệ thống
                </a>
                <a href="${pageContext.request.contextPath}/app/it/system-history" class="nav-item ${fn:contains(uri, '/system-history') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                    Lịch sử Hệ thống
                </a>
            </c:if>

            <!-- SHAREHOLDER MENU -->
            <c:if test="${sessionScope.role == 'SHAREHOLDER'}">
                <div class="nav-section">TỔNG QUAN</div>
                <a href="${pageContext.request.contextPath}/app/dashboard" class="nav-item ${fn:contains(uri, '/dashboard') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path></svg>
                    Bảng Điều Khiển
                </a>
                <div class="nav-section">CỔ PHẦN CỦA TÔI</div>
                <a href="${pageContext.request.contextPath}/app/shareholder/notifications" class="nav-item ${fn:contains(uri, '/notifications') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8a6 6 0 0 0-12 0c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
                    Thông báo
                </a>
                <a href="${pageContext.request.contextPath}/app/shareholder/sign" class="nav-item ${fn:contains(uri, '/sign') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"></path><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z"></path></svg>
                    Ký nhận Cổ phần
                </a>
                <a href="${pageContext.request.contextPath}/app/shareholder/vote" class="nav-item ${fn:contains(uri, '/vote') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
                    Biểu quyết
                </a>
                <a href="${pageContext.request.contextPath}/app/shareholder/transfer-request" class="nav-item ${fn:contains(uri, '/transfer-request') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="17 1 21 5 17 9"></polyline><path d="M3 11V9a4 4 0 0 1 4-4h14"></path><polyline points="7 23 3 19 7 15"></polyline><path d="M21 13v2a4 4 0 0 1-4 4H3"></path></svg>
                    Chuyển nhượng
                </a>
                <div class="nav-section">DỮ LIỆU HỆ THỐNG</div>
                <a href="${pageContext.request.contextPath}/app/shareholder/financial-reports" class="nav-item ${fn:contains(uri, '/financial-reports') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"></line><line x1="12" y1="20" x2="12" y2="4"></line><line x1="6" y1="20" x2="6" y2="14"></line></svg>
                    Báo cáo Tài chính
                </a>
                <a href="${pageContext.request.contextPath}/app/shareholder/documents" class="nav-item ${fn:contains(uri, '/documents') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline></svg>
                    Tài liệu Hệ thống
                </a>
                <div class="nav-section">TÀI KHOẢN</div>
                <a href="${pageContext.request.contextPath}/app/shareholder/profile" class="nav-item ${fn:contains(uri, '/profile') ? 'active' : ''}">
                    <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                    Tài khoản Cá nhân
                </a>
            </c:if>
        </div>
    </aside>

    <div class="app-main">
        <header class="app-topbar">
            <c:if test="${not empty sessionScope.username}">
                <div class="tb-user">Xin chào, <strong><c:out value="${sessionScope.username}"/></strong> (<c:out value="${sessionScope.role}"/>)</div>
                <a href="${pageContext.request.contextPath}/app/logout" class="btn-logout">Đăng xuất</a>
            </c:if>
        </header>

        <main class="vertical-dashboard">
