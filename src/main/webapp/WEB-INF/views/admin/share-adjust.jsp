<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<%@ include file="/WEB-INF/views/common/ga4.jsp" %>
    <meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Điều chỉnh Cổ phần - VinScape</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-layout.css">
<style>
        /* =========================================================
           3. SHARE ADJUST CONTENT STYLES
           ========================================================= */
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
            font-size: 24px; font-weight: 700; color: var(--text-main);
            margin: 0 0 24px 0; letter-spacing: -0.5px;
        }

        .error-box {
            background-color: var(--danger-bg); color: var(--danger);
            border: 1px solid #fca5a5; padding: 14px 20px; border-radius: 8px;
            font-size: 13.5px; font-weight: 500; margin-bottom: 24px;
            display: flex; align-items: center; gap: 8px;
        }

        .card {
            background: var(--card-bg); border-radius: 12px; padding: 24px 32px;
            border: 1px solid var(--border-color); box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            margin-bottom: 32px; overflow: hidden;
        }
        
        .card h2 {
            font-size: 16px; font-weight: 700; color: var(--text-main);
            margin: 0 0 24px 0; padding-bottom: 16px;
            border-bottom: 1px solid var(--border-color);
            display: flex; align-items: center; gap: 8px;
        }
        
        .card h2::before {
            content: ""; display: block; width: 4px; height: 16px;
            background-color: var(--primary); border-radius: 4px;
        }

        /* --- FORM STYLES --- */
        .form-group { margin-bottom: 16px; max-width: 600px; }
        
        label {
            display: block; font-size: 13px; font-weight: 600;
            color: var(--text-main); margin-bottom: 8px;
        }
        
        .input-field {
            width: 100%; border: 1px solid var(--border-color); border-radius: 6px;
            padding: 10px 14px; font-size: 13.5px; color: var(--text-main);
            background-color: #f8fafc; outline: none; transition: all 0.2s;
            box-sizing: border-box; font-family: inherit;
        }
        .input-field:focus {
            background-color: #ffffff; border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(47, 111, 237, 0.15);
        }
        textarea.input-field { resize: vertical; min-height: 80px; }

        .btn-submit {
            background: var(--primary); color: #ffffff; border: none;
            padding: 12px 24px; border-radius: 6px; font-size: 14px; font-weight: 600;
            cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 4px rgba(47, 111, 237, 0.2);
            margin-top: 8px; display: inline-flex; align-items: center; gap: 6px;
        }
        .btn-submit:hover {
            background: var(--primary-hover); transform: translateY(-1px);
            box-shadow: 0 4px 6px rgba(47, 111, 237, 0.3);
        }

        /* --- TABLE STYLES --- */
        .table-wrapper { width: 100%; overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; text-align: left; }
        
        th {
            background-color: #f1f5f9; color: var(--text-muted);
            font-size: 12px; font-weight: 600; text-transform: uppercase;
            padding: 14px 16px; letter-spacing: 0.5px;
        }
        th:first-child { border-top-left-radius: 8px; border-bottom-left-radius: 8px; }
        th:last-child { border-top-right-radius: 8px; border-bottom-right-radius: 8px; }
        
        td {
            padding: 16px; border-bottom: 1px solid var(--border-color);
            font-size: 13.5px; color: var(--text-main); vertical-align: middle;
        }
        tr:last-child td { border-bottom: none; }
        tr:hover td { background-color: #f8fafc; }

        .badge-id {
            font-weight: 700; color: var(--primary); min-width: 40px;
            background: var(--info-bg); padding: 4px 8px; border-radius: 6px; 
            text-align: center; display: inline-block; font-size: 12.5px;
        }
        
        .quantity-text {
            font-weight: 700; color: #10b981; font-size: 14.5px;
        }

        .empty-state { text-align: center; color: var(--text-muted); padding: 32px !important; font-style: italic; }
    </style>
</head>
<body>

<div class="app-layout">
    
    <!-- ================= SIDEBAR (MENU TRÁI) ================= -->
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
            
            <!-- Đã thêm class "active" để tô sáng mục này -->
            <a href="${pageContext.request.contextPath}/app/admin/share-adjust" class="nav-item active">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>
                Điều chỉnh Cổ phần
            </a>
            <a href="${pageContext.request.contextPath}/app/admin/share-adjustment-history" class="nav-item">
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

    <!-- ================= MAIN AREA ================= -->
    <div class="app-main">
        
        <!-- TOPBAR -->
        <header class="app-topbar">
            <div class="tb-breadcrumb">
                <span>VINSCAPE &nbsp;>&nbsp;</span> Điều chỉnh Cổ phần
            </div>
            <div class="tb-right">
                <div class="tb-user">Xin chào, <strong>admin124</strong> (ADMIN)</div>
                <a href="${pageContext.request.contextPath}/app/logout" class="tb-logout">Đăng xuất</a>
            </div>
        </header>

        <!-- CONTENT -->
        <main class="vertical-dashboard">
            
            <h1 class="page-title">Điều chỉnh Cổ phần</h1>

            <c:if test="${not empty error}">
                <div class="error-box">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <!-- Thẻ 1: Form điều chỉnh -->
            <div class="card">
                <h2>Nhập số cổ phần mới</h2>
                <form method="post" action="${pageContext.request.contextPath}/app/admin/share-adjust"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    
                    <div class="form-group">
                        <label>Mã Cổ đông (Shareholder ID)</label>
                        <input type="number" name="shareholderId" class="input-field" required min="1" placeholder="VD: 1">
                    </div>
                    
                    <div class="form-group">
                        <label>Số cổ phần mới (Không phải số thay đổi)</label>
                        <input type="number" name="newQuantity" class="input-field" required min="0" placeholder="VD: 50000">
                    </div>
                    
                    <div class="form-group">
                        <label>Lý do điều chỉnh</label>
                        <textarea name="reason" class="input-field" rows="3" required placeholder="VD: Mua thêm cổ phần đợt phát hành mới..."></textarea>
                    </div>
                    
                    <button type="submit" class="btn-submit">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"></path><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path></svg>
                        Cập nhật
                    </button>
                </form>
            </div>

            <!-- Thẻ 2: Bảng hiển thị số dư -->
            <div class="card">
                <h2>Số dư hiện tại</h2>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Mã Cổ đông</th>
                                <th>Số cổ phần</th>
                                <th>Cập nhật lúc</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${shares}">
                                <tr>
                                    <td><span class="badge-id">#<c:out value="${s.shareholderId}"/></span></td>
                                    <td class="quantity-text"><c:out value="${s.quantity}"/> CP</td>
                                    <td style="color: #64748b; font-size: 13px;"><c:out value="${s.updatedAt}"/></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty shares}">
                                <tr>
                                    <td colspan="3" class="empty-state">Hệ thống chưa có dữ liệu cổ phần nào.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

        </main>
    </div>
</div>

</body>
</html>