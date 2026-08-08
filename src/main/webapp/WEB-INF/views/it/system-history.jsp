<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<%@ include file="/WEB-INF/views/common/ga4.jsp" %>
    <meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Lịch sử hệ thống (IT) - VinScape</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-layout.css">
<style>
        :root {
            --card-bg: #ffffff;
            --text-main: #0f172a;
            --text-muted: #64748b;
            --primary: #2f6fed;
            --primary-hover: #2563eb;
            --border-color: #e2e8f0;
            --danger: #ef4444;
            --danger-bg: #fef2f2;
            --info-bg: #eff6ff;
        }

        .page-title {
            font-size: 24px;
            font-weight: 700;
            color: var(--text-main);
            margin: 0 0 10px 0;
            letter-spacing: -0.5px;
        }
        .page-subtitle {
            color: var(--text-muted);
            font-size: 14px;
            margin: 0 0 24px 0;
            line-height: 1.6;
        }
        .error-box {
            padding: 14px 20px;
            border-radius: 8px;
            font-size: 13.5px;
            font-weight: 500;
            margin-bottom: 24px;
            color: var(--danger);
            background-color: var(--danger-bg);
            border: 1px solid #fca5a5;
        }
        .card {
            background: var(--card-bg);
            border-radius: 12px;
            padding: 24px 32px;
            border: 1px solid var(--border-color);
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            margin-bottom: 32px;
            overflow: hidden;
        }
        .card h2 {
            font-size: 16px;
            font-weight: 700;
            color: var(--text-main);
            margin: 0 0 24px 0;
            padding-bottom: 16px;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .card h2::before {
            content: "";
            display: block;
            width: 4px;
            height: 16px;
            background-color: var(--primary);
            border-radius: 4px;
        }
        .table-wrapper { width: 100%; overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; text-align: left; }
        th {
            background-color: #f1f5f9;
            color: var(--text-muted);
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
            padding: 14px 16px;
            letter-spacing: 0.5px;
            white-space: nowrap;
        }
        th:first-child { border-top-left-radius: 8px; border-bottom-left-radius: 8px; }
        th:last-child { border-top-right-radius: 8px; border-bottom-right-radius: 8px; }
        td {
            padding: 16px;
            border-bottom: 1px solid var(--border-color);
            font-size: 13.5px;
            color: var(--text-main);
            vertical-align: middle;
        }
        tr:last-child td { border-bottom: none; }
        tr:hover td { background-color: #f8fafc; }
        .badge-id {
            font-weight: 700;
            color: var(--primary);
            min-width: 40px;
            background: var(--info-bg);
            padding: 4px 8px;
            border-radius: 6px;
            text-align: center;
            display: inline-block;
            font-size: 12.5px;
        }
        .badge-action {
            font-size: 11px;
            font-weight: 600;
            padding: 5px 10px;
            border-radius: 20px;
            letter-spacing: 0.3px;
            display: inline-block;
            text-align: center;
            background-color: #eef2ff;
            color: #4338ca;
            border: 1px solid #c7d2fe;
            white-space: nowrap;
        }
        .badge-entity {
            font-size: 11.5px;
            font-weight: 600;
            color: var(--text-muted);
            background: #f1f5f9;
            padding: 4px 10px;
            border-radius: 6px;
            display: inline-block;
        }
        .cell-time {
            font-family: 'Courier New', monospace;
            font-size: 13px;
            color: var(--text-main);
            white-space: nowrap;
        }
        .cell-ua {
            max-width: 260px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            color: var(--text-muted);
            font-size: 12.5px;
        }
        .system-tag {
            font-style: italic;
            color: var(--text-muted);
        }
        .empty-state {
            text-align: center;
            color: var(--text-muted);
            padding: 32px !important;
            font-style: italic;
        }
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
            <div class="nav-section">ĐIỀU HÀNH KỸ THUẬT (IT)</div>
            <a href="${pageContext.request.contextPath}/app/dashboard" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path></svg>
                Bảng Điều Khiển
            </a>
            <a href="${pageContext.request.contextPath}/app/it/user-management" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
                Quản lý Người dùng
            </a>
            <a href="${pageContext.request.contextPath}/app/it/documents" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
                Tài liệu Hệ thống
            </a>
            <a href="${pageContext.request.contextPath}/app/it/system-history" class="nav-item active">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                Lịch sử Hệ thống
            </a>
        </div>
    </aside>

    <div class="app-main">
        <header class="app-topbar">
            <div class="tb-breadcrumb">
                <span>VINSCAPE &nbsp;>&nbsp;</span> Lịch sử Hệ thống
            </div>
            <div class="tb-right">
                <div class="tb-user">Xin chào, <strong>itadmin</strong> (IT)</div>
                <a href="${pageContext.request.contextPath}/app/logout" class="tb-logout">Đăng xuất</a>
            </div>
        </header>

        <main class="vertical-dashboard">

            <h1 class="page-title">Lịch sử Hệ thống</h1>
            <p class="page-subtitle">
                Nhật ký (audit log) ghi lại toàn bộ thao tác quan trọng trên hệ thống: đăng nhập/đăng xuất,
                tạo/sửa/xóa cổ đông, chuyển nhượng cổ phần, phát hành cổ phần, nghị quyết, tài liệu...
                Dữ liệu chỉ để xem, không thể chỉnh sửa. Hiển thị tối đa
                <c:out value="${logs != null ? logs.size() : 0}"/> bản ghi gần nhất.
            </p>

            <c:if test="${not empty error}">
                <div class="error-box">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <div class="card">
                <h2>Nhật ký thao tác</h2>
                <div class="table-wrapper">
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Người dùng</th>
                            <th>Hành động</th>
                            <th>Loại đối tượng</th>
                            <th>ID đối tượng</th>
                            <th>User Agent</th>
                            <th>Thời gian</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty logs}">
                                <tr>
                                    <td colspan="7" class="empty-state">Chưa có dữ liệu lịch sử hệ thống.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="log" items="${logs}">
                                    <tr>
                                        <td><span class="badge-id">#<c:out value="${log.logId}"/></span></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${empty log.userId}">
                                                    <span class="system-tag">Hệ thống (tự động)</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out value="${log.userId}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><span class="badge-action"><c:out value="${log.action}"/></span></td>
                                        <td><span class="badge-entity"><c:out value="${log.entityType}"/></span></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${empty log.entityId}">—</c:when>
                                                <c:otherwise><c:out value="${log.entityId}"/></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="cell-ua" title="${log.userAgent}"><c:out value="${log.userAgent}"/></td>
                                        <td class="cell-time"><c:out value="${log.formattedCreatedAt}"/></td>
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
