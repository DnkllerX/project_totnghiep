<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<%@ include file="/WEB-INF/views/common/ga4.jsp" %>
    <meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Lịch sử Điều chỉnh Cổ phần - VinScape</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-layout.css">
<style>
        :root {
            --card-bg: #ffffff; --text-main: #0f172a; --text-muted: #64748b;
            --primary: #2f6fed; --border-color: #e2e8f0;
            --danger: #ef4444; --danger-bg: #fef2f2; --info-bg: #eff6ff;
            --up: #16a34a; --up-bg: #f0fdf4; --down: #dc2626; --down-bg: #fef2f2;
        }
        .page-title { font-size: 24px; font-weight: 700; color: var(--text-main); margin: 0 0 10px 0; letter-spacing: -0.5px; }
        .page-subtitle { color: var(--text-muted); font-size: 14px; margin: 0 0 24px 0; line-height: 1.6; }
        .error-box { background-color: var(--danger-bg); color: var(--danger); border: 1px solid #fca5a5; padding: 14px 20px; border-radius: 8px; font-size: 13.5px; font-weight: 500; margin-bottom: 24px; }
        .card { background: var(--card-bg); border-radius: 12px; padding: 24px 32px; border: 1px solid var(--border-color); box-shadow: 0 1px 3px rgba(0,0,0,0.05); overflow: hidden; }
        .card h2 { font-size: 16px; font-weight: 700; color: var(--text-main); margin: 0 0 24px 0; padding-bottom: 16px; border-bottom: 1px solid var(--border-color); display: flex; align-items: center; gap: 8px; }
        .card h2::before { content: ""; display: block; width: 4px; height: 16px; background-color: var(--primary); border-radius: 4px; }
        .table-wrapper { width: 100%; overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; text-align: left; }
        th { background-color: #f1f5f9; color: var(--text-muted); font-size: 12px; font-weight: 600; text-transform: uppercase; padding: 14px 16px; letter-spacing: 0.5px; white-space: nowrap; }
        th:first-child { border-top-left-radius: 8px; border-bottom-left-radius: 8px; }
        th:last-child { border-top-right-radius: 8px; border-bottom-right-radius: 8px; }
        td { padding: 16px; border-bottom: 1px solid var(--border-color); font-size: 13.5px; color: var(--text-main); vertical-align: middle; }
        tr:last-child td { border-bottom: none; }
        tr:hover td { background-color: #f8fafc; }
        .qty-change { display: flex; align-items: center; gap: 6px; font-family: 'Courier New', monospace; white-space: nowrap; }
        .qty-old { color: var(--text-muted); }
        .qty-delta { font-size: 11px; font-weight: 700; padding: 2px 8px; border-radius: 20px; }
        .qty-up { color: var(--up); background: var(--up-bg); }
        .qty-down { color: var(--down); background: var(--down-bg); }
        .cell-time { font-family: 'Courier New', monospace; font-size: 13px; white-space: nowrap; }
        .cell-reason { max-width: 260px; color: var(--text-muted); }
        .empty-state { text-align: center; color: var(--text-muted); padding: 32px !important; font-style: italic; }
    </style>
</head>
<body>

<div class="app-layout">
    <aside class="app-sidebar">
        <div class="sb-brand" style="display:flex; align-items:center; gap:10px;">
            <img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo" style="width:26px; height:26px; object-fit:contain; border-radius:4px;">
            <span>VINSCAPE</span>
        </div>

        <div class="sb-nav">
            <div class="nav-section">TỔNG QUAN</div>
            <a href="${pageContext.request.contextPath}/app/dashboard" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path></svg>
                Bảng Điều Khiển
            </a>

            <div class="nav-section">QUẢN TRỊ CỔ ĐÔNG</div>
            <a href="${pageContext.request.contextPath}/app/admin/shareholders" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
                Quản lý Cổ đông
            </a>
            <a href="${pageContext.request.contextPath}/app/admin/transfer-approval" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="17 1 21 5 17 9"></polyline><path d="M3 11V9a4 4 0 0 1 4-4h14"></path><polyline points="7 23 3 19 7 15"></polyline><path d="M21 13v2a4 4 0 0 1-4 4H3"></path></svg>
                Duyệt Chuyển nhượng
            </a>
            <a href="${pageContext.request.contextPath}/app/admin/share-issue" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><line x1="12" y1="8" x2="12" y2="16"></line><line x1="8" y1="12" x2="16" y2="12"></line></svg>
                Phát hành & Cổ tức
            </a>
            <a href="${pageContext.request.contextPath}/app/admin/resolution" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
                Nghị quyết Biểu quyết
            </a>
            <a href="${pageContext.request.contextPath}/app/admin/share-adjust" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>
                Điều chỉnh Cổ phần
            </a>
            <a href="${pageContext.request.contextPath}/app/admin/share-adjustment-history" class="nav-item active">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                Lịch sử Điều chỉnh
            </a>

            <div class="nav-section">DỮ LIỆU HỆ THỐNG</div>
            <a href="${pageContext.request.contextPath}/app/admin/documents" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
                Tài liệu Hệ thống
            </a>
            <a href="${pageContext.request.contextPath}/app/admin/financial-reports/manage" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"></line><line x1="12" y1="20" x2="12" y2="4"></line><line x1="6" y1="20" x2="6" y2="14"></line></svg>
                Báo cáo Tài chính
            </a>
        </div>
    </aside>

    <div class="app-main">
        <header class="app-topbar">
            <div class="tb-breadcrumb">
                <span>VINSCAPE &nbsp;>&nbsp;</span> Lịch sử Điều chỉnh Cổ phần
            </div>
            <div class="tb-right">
                <div class="tb-user">Xin chào, <strong>admin</strong> (ADMIN)</div>
                <a href="${pageContext.request.contextPath}/app/logout" class="tb-logout">Đăng xuất</a>
            </div>
        </header>

        <main class="vertical-dashboard">

            <h1 class="page-title">Lịch sử Điều chỉnh Cổ phần</h1>
            <p class="page-subtitle">
                Toàn bộ thao tác điều chỉnh trực tiếp số lượng cổ phần (SHARE_ADJUSTMENT_LOGS) do ADMIN
                thực hiện, không bao gồm giao dịch chuyển nhượng hoặc phát hành thông thường.
            </p>

            <c:if test="${not empty error}">
                <div class="error-box"><c:out value="${error}"/></div>
            </c:if>

            <div class="card">
                <h2>Nhật ký điều chỉnh</h2>
                <div class="table-wrapper">
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Cổ đông</th>
                            <th>Thay đổi</th>
                            <th>Lý do</th>
                            <th>Người thực hiện</th>
                            <th>Thời gian</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty logs}">
                                <tr><td colspan="6" class="empty-state">Chưa có dữ liệu điều chỉnh cổ phần nào.</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="log" items="${logs}">
                                    <c:set var="delta" value="${log.newValue - log.oldValue}"/>
                                    <tr>
                                        <td>#${log.id}</td>
                                        <td><c:out value="${shareholderNameById[log.shareholderId]}" default="Cổ đông #${log.shareholderId}"/></td>
                                        <td>
                                            <span class="qty-change">
                                                <span class="qty-old"><fmt:formatNumber value="${log.oldValue}" type="number"/></span>
                                                →
                                                <fmt:formatNumber value="${log.newValue}" type="number"/>
                                                <c:choose>
                                                    <c:when test="${delta > 0}"><span class="qty-delta qty-up">+<fmt:formatNumber value="${delta}" type="number"/></span></c:when>
                                                    <c:otherwise><span class="qty-delta qty-down"><fmt:formatNumber value="${delta}" type="number"/></span></c:otherwise>
                                                </c:choose>
                                            </span>
                                        </td>
                                        <td class="cell-reason"><c:out value="${log.reason}"/></td>
                                        <td><c:out value="${userNameById[log.adjustedBy]}" default="User #${log.adjustedBy}"/></td>
                                        <td class="cell-time"><c:out value="${log.formattedAdjustedAt}"/></td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

        </main>
    </div>
</div>

</body>
</html>
