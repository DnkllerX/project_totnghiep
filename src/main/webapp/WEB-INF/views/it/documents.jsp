<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<%@ include file="/WEB-INF/views/common/ga4.jsp" %>
    <meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tài liệu hệ thống (IT) - VinScape</title>
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
            --danger-hover: #dc2626;
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
        .action-group { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
        .action-group form { margin: 0; }
        .btn-action {
            padding: 7px 12px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
            border: 1px solid transparent;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-family: inherit;
        }
        .btn-action.primary { background: var(--primary); color: #fff; }
        .btn-action.primary:hover { background: var(--primary-hover); transform: translateY(-1px); }
        .btn-action.outline { background: transparent; color: var(--text-main); border-color: #cbd5e1; }
        .btn-action.outline:hover { background: #f1f5f9; border-color: #94a3b8; transform: translateY(-1px); }
        .btn-action.preview-theme { background:linear-gradient(135deg,#f8fafc,#cbd5e1); color:#1e293b; border-color:#94a3b8; box-shadow:0 1px 1px rgba(5,5,5,.22),inset 0 1px rgba(255,255,255,.9); }
        .btn-action.preview-theme:hover { background:#323232; color:#fff; border-color:#323232; transform:translateY(-1px); }
        .btn-action.download-theme { position:relative; overflow:visible; background:linear-gradient(160deg,#2f5dc5 0%,#2e56c2 20%,#3bbee6 68%,#00d4ff 100%); color:#fff; border-color:transparent; box-shadow:1px 1px rgba(107,221,215,.37); filter:drop-shadow(0 0 6px rgba(59,190,230,.3)); }
        .btn-action.download-theme:hover { background:#323232; transform:translateY(-1px); }
        .btn-action.danger { background: var(--danger); color: #fff; }
        .btn-action.danger:hover { background: var(--danger-hover); transform: translateY(-1px); }
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
            <a href="${pageContext.request.contextPath}/app/it/documents" class="nav-item active">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
                Tài liệu Hệ thống
            </a>
            <a href="${pageContext.request.contextPath}/app/it/system-history" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                Lịch sử Hệ thống
            </a>
        </div>
    </aside>

    <div class="app-main">
        <header class="app-topbar">
            <div class="tb-breadcrumb">
                <span>VINSCAPE &nbsp;>&nbsp;</span> Tài liệu Hệ thống
            </div>
            <div class="tb-right">
                <div class="tb-user">Xin chào, <strong><c:out value="${sessionScope.username}"/></strong> (IT)</div>
                <a href="${pageContext.request.contextPath}/app/logout" class="tb-logout">Đăng xuất</a>
            </div>
        </header>

        <main class="vertical-dashboard">
            <h1 class="page-title">Tài liệu Hệ thống (IT)</h1>
            <p class="page-subtitle">IT có quyền xem, tải xuống và xóa tài liệu. Chức năng tải lên tài liệu vẫn thuộc quyền Admin.</p>

            <c:if test="${not empty error}">
                <div class="error-box"><c:out value="${error}"/></div>
            </c:if>

            <section class="card">
                <h2>Danh sách tài liệu</h2>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tiêu đề</th>
                                <th>Ngày tải lên</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="d" items="${documents}">
                                <tr>
                                    <td><span class="badge-id">#<c:out value="${d.documentId}"/></span></td>
                                    <td style="font-weight: 500;"><c:out value="${d.title}"/></td>
                                    <td><c:out value="${d.uploadedAtDisplay}"/></td>
                                    <td>
                                        <div class="action-group">
                                            <c:if test="${fn:endsWith(fn:toLowerCase(d.fileUrl), '.pdf')}">
                                                <a class="btn-action outline preview-theme" target="_blank" rel="noopener" href="${pageContext.request.contextPath}/app/documents/view?id=${d.documentId}">Xem</a>
                                            </c:if>
                                            <a class="btn-action primary download-theme" href="${pageContext.request.contextPath}/app/documents/download?id=${d.documentId}">Tải xuống</a>
                                            <form method="post" action="${pageContext.request.contextPath}/app/it/documents" onsubmit="return confirm('Bạn có chắc chắn muốn xóa tài liệu này?');"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="documentId" value="${d.documentId}">
                                                <button class="btn-action danger" type="submit">Xóa</button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty documents}">
                                <tr>
                                    <td colspan="4" class="empty-state">Chưa có tài liệu nào.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>
</div>
</body>
</html>
