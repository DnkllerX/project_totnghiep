<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<%@ include file="/WEB-INF/views/common/ga4.jsp" %>
    <meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý Tài khoản (IT) - VinScape</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-layout.css">
<style>
        /* =========================================================
           3. USER MANAGEMENT CONTENT STYLES
           ========================================================= */
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
            --success-bg: #ecfdf5;
            --success-text: #10b981;
        }

        .page-title {
            font-size: 24px; font-weight: 700; color: var(--text-main);
            margin: 0 0 24px 0; letter-spacing: -0.5px;
        }

        /* Flash Messages */
        .error-box, .flash-box, .flash-temp {
            padding: 14px 20px; border-radius: 8px; font-size: 13.5px;
            font-weight: 500; margin-bottom: 24px; display: flex; align-items: center; gap: 8px;
        }
        .error-box { background-color: var(--danger-bg); color: var(--danger); border: 1px solid #fca5a5; }
        .flash-box { background-color: var(--success-bg); color: var(--success-text); border: 1px solid #a7f3d0; }
        .flash-temp { background-color: #e0f2fe; color: #1e3a8a; border: 1px solid #bfdbfe; font-size: 14px; }
        .flash-temp b { font-size: 16px; background: #ffffff; padding: 4px 10px; border-radius: 6px; border: 1px dashed #93c5fd; margin-left: 8px; color: #1d4ed8; }

        /* Card Styles */
        .card {
            background: var(--card-bg); border-radius: 12px; padding: 24px 32px;
            border: 1px solid var(--border-color); box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            margin-bottom: 32px; overflow: hidden;
        }
        .card h2 {
            font-size: 16px; font-weight: 700; color: var(--text-main);
            margin: 0 0 24px 0; padding-bottom: 16px; border-bottom: 1px solid var(--border-color);
            display: flex; align-items: center; gap: 8px;
        }
        .card h2::before {
            content: ""; display: block; width: 4px; height: 16px;
            background-color: var(--primary); border-radius: 4px;
        }

        /* Form Styles */
        .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
        @media (max-width: 900px) { .grid-2 { grid-template-columns: 1fr; } }
        
        .form-group { margin-bottom: 16px; }
        label { display: block; font-size: 13px; font-weight: 600; color: var(--text-main); margin-bottom: 8px; }
        .input-field {
            width: 100%; border: 1px solid var(--border-color); border-radius: 6px;
            padding: 10px 14px; font-size: 13.5px; color: var(--text-main);
            background-color: #f8fafc; outline: none; transition: all 0.2s; box-sizing: border-box; font-family: inherit;
        }
        .input-field:focus { background-color: #ffffff; border-color: var(--primary); box-shadow: 0 0 0 3px rgba(47, 111, 237, 0.15); }

        /* Buttons */
        .btn-group { display: flex; gap: 12px; margin-top: 20px; }
        .btn-submit, .btn-outline {
            padding: 10px 20px; border-radius: 6px; font-size: 13.5px; font-weight: 600;
            cursor: pointer; transition: all 0.2s; display: inline-flex; align-items: center; justify-content: center; text-decoration: none; box-sizing: border-box;
        }
        .btn-submit { background: var(--primary); color: #ffffff; border: none; box-shadow: 0 2px 4px rgba(47, 111, 237, 0.2); }
        .btn-submit:hover { background: var(--primary-hover); transform: translateY(-1px); box-shadow: 0 4px 6px rgba(47, 111, 237, 0.3); }
        .btn-outline { background: transparent; color: var(--text-main); border: 1px solid #cbd5e1; }
        .btn-outline:hover { background: #f1f5f9; border-color: #94a3b8; transform: translateY(-1px); }

        /* Table Styles */
        .table-wrapper { width: 100%; overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; text-align: left; }
        th { background-color: #f1f5f9; color: var(--text-muted); font-size: 12px; font-weight: 600; text-transform: uppercase; padding: 14px 16px; letter-spacing: 0.5px; }
        th:first-child { border-top-left-radius: 8px; border-bottom-left-radius: 8px; }
        th:last-child { border-top-right-radius: 8px; border-bottom-right-radius: 8px; }
        td { padding: 16px; border-bottom: 1px solid var(--border-color); font-size: 13.5px; color: var(--text-main); vertical-align: middle; }
        tr:last-child td { border-bottom: none; }
        tr:hover td { background-color: #f8fafc; }

        .badge-id { font-weight: 700; color: var(--primary); min-width: 40px; background: var(--info-bg); padding: 4px 8px; border-radius: 6px; text-align: center; display: inline-block; font-size: 12.5px; }
        .badge { font-size: 11px; font-weight: 600; padding: 5px 12px; border-radius: 20px; letter-spacing: 0.5px; display: inline-block; text-align: center; }
        .badge-active { background-color: #ecfdf5; color: #10b981; border: 1px solid #a7f3d0; }
        .badge-locked { background-color: #fef2f2; color: #ef4444; border: 1px solid #fca5a5; }

        /* Action Buttons in Table */
        .action-group { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
        .action-group form { margin: 0; }
        .btn-action { padding: 7px 12px; border-radius: 6px; font-size: 12px; font-weight: 600; cursor: pointer; transition: all 0.2s; border: 1px solid transparent; }
        .btn-action.primary { background: var(--primary); color: #fff; }
        .btn-action.primary:hover { background: var(--primary-hover); transform: translateY(-1px); }
        .btn-action.outline { background: transparent; color: var(--text-main); border-color: #cbd5e1; }
        .btn-action.outline:hover { background: #f1f5f9; border-color: #94a3b8; transform: translateY(-1px); }
        .btn-action.danger { background: var(--danger); color: #fff; }
        .btn-action.danger:hover { background: var(--danger-hover); transform: translateY(-1px); }

        .empty-state { text-align: center; color: var(--text-muted); padding: 32px !important; font-style: italic; }

        /* Modal Styles */
        .modal-overlay { display: none; position: fixed; inset: 0; background: rgba(15, 23, 42, 0.6); backdrop-filter: blur(2px); align-items: center; justify-content: center; z-index: 100; }
        .modal-content { background: #fff; border-radius: 12px; width: 100%; max-width: 450px; padding: 24px 32px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04); }
        .modal-content h2 { margin-top: 0; font-size: 18px; margin-bottom: 20px; color: var(--text-main); display: flex; justify-content: space-between; align-items: center; }
    </style>
</head>
<body>

<div class="app-layout">
    
    <!-- ================= SIDEBAR (MENU TRÁI - DÀNH CHO IT) ================= -->
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
            <!-- Đã thêm class "active" để tô sáng mục này -->
            <a href="${pageContext.request.contextPath}/app/it/user-management" class="nav-item active">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
                Quản lý Người dùng
            </a>
            <a href="${pageContext.request.contextPath}/app/it/documents" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
                Tài liệu Hệ thống
            </a>
            <a href="${pageContext.request.contextPath}/app/it/system-history" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                Lịch sử Hệ thống
            </a>
        </div>
    </aside>

    <!-- ================= MAIN AREA ================= -->
    <div class="app-main">
        
        <!-- TOPBAR -->
        <header class="app-topbar">
            <div class="tb-breadcrumb">
                <span>VINSCAPE &nbsp;>&nbsp;</span> Quản lý Người dùng
            </div>
            <div class="tb-right">
                <div class="tb-user">Xin chào, <strong>itadmin</strong> (IT)</div>
                <a href="${pageContext.request.contextPath}/app/logout" class="tb-logout">Đăng xuất</a>
            </div>
        </header>

        <!-- CONTENT -->
        <main class="vertical-dashboard">
            
            <h1 class="page-title">Quản lý Tài khoản (IT)</h1>

            <!-- Xử lý biến Tab -->
            <c:set var="currentTab" value="${param.tab}" />
            <c:if test="${empty currentTab}">
                <c:set var="currentTab" value="search" />
            </c:if>

            <!-- Các khung thông báo -->
            <c:if test="${not empty error}">
                <div class="error-box">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
                    <c:out value="${error}"/>
                </div>
            </c:if>
            <c:if test="${not empty sessionScope.flashMessage}">
                <div class="flash-box">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
                    <c:out value="${sessionScope.flashMessage}"/>
                </div>
                <c:remove var="flashMessage" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.flashTempPassword}">
                <div class="flash-temp">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                    <span>Mật khẩu tạm thời (chỉ hiển thị 1 lần, hãy gửi cho người dùng ngay):</span>
                    <b><c:out value="${sessionScope.flashTempPassword}"/></b>
                </div>
                <c:remove var="flashTempPassword" scope="session"/>
            </c:if>

            <!-- Khối nội dung Form dựa theo Tab -->
            <c:choose>
                <%-- TAB: TẠO TÀI KHOẢN --%>
                <c:when test="${currentTab == 'create'}">
                    <div class="card" style="max-width: 600px;">
                        <h2>Tạo tài khoản Cổ đông mới</h2>
                        <p style="font-size:13px; color:#64748b; margin:-8px 0 16px;">
                            Trang này chỉ tạo được tài khoản <b>SHAREHOLDER</b> (cổ đông). Tài khoản tạo xong
                            ở trạng thái <b>chờ duyệt</b> — cần ADMIN phê duyệt ở trang Quản lý Cổ đông thì
                            mới đăng nhập được. Muốn tạo tài khoản ADMIN/IT, liên hệ quản trị hệ thống trực tiếp.
                        </p>
                        <form method="post" action="${pageContext.request.contextPath}/app/it/user-management" autocomplete="off"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="action" value="create">
                            <input type="hidden" name="tab" value="create">

                            <div class="form-group">
                                <label>Tên đăng nhập (Username)</label>
                                <input type="text" name="username" class="input-field" required autocomplete="off" placeholder="Ví dụ: nguyenvana">
                            </div>
                            <div class="form-group">
                                <label>Email</label>
                                <input type="email" name="email" class="input-field" required autocomplete="off" placeholder="Ví dụ: a@gmail.com">
                            </div>
                            <div class="form-group">
                                <label>Mật khẩu tạm</label>
                                <input type="password" id="createPassword" name="password" class="input-field" required autocomplete="new-password" placeholder="Bảo mật tối đa">
                            </div>
                            <div class="form-group">
                                <label>Họ và tên</label>
                                <input type="text" name="fullName" class="input-field" required placeholder="Ví dụ: Nguyễn Văn A">
                            </div>
                            <div class="form-group">
                                <label>Số CCCD/CMND</label>
                                <input type="text" name="citizenId" class="input-field" required placeholder="Ví dụ: 001234567890">
                            </div>
                            <div class="form-group">
                                <label>Số điện thoại</label>
                                <input type="text" name="phone" class="input-field" required placeholder="Ví dụ: 0912345678">
                            </div>
                            <div class="form-group">
                                <label>Địa chỉ</label>
                                <input type="text" name="address" class="input-field" placeholder="Địa chỉ thường trú">
                            </div>
                            <div class="form-group">
                                <label>Ngày sinh</label>
                                <input type="date" name="birthDate" class="input-field">
                            </div>
                            <div class="form-group">
                                <label>Quốc tịch</label>
                                <input type="text" name="nationality" class="input-field" value="Việt Nam">
                            </div>

                            <button type="submit" class="btn-submit" style="margin-top: 10px;">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right: 6px;"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="8.5" cy="7" r="4"/><line x1="20" y1="8" x2="20" y2="14"/><line x1="23" y1="11" x2="17" y2="11"/></svg>
                                Tạo tài khoản (chờ ADMIN duyệt)
                            </button>
                        </form>
                    </div>
                </c:when>

                <%-- TAB: LỌC TÀI KHOẢN --%>
                <c:when test="${currentTab == 'filter'}">
                    <div class="card">
                        <h2>Lọc tài khoản</h2>
                        <form method="get" action="${pageContext.request.contextPath}/app/it/user-management">
                            <input type="hidden" name="tab" value="filter">
                            <div class="grid-2">
                                <div>
                                    <div class="form-group">
                                        <label>Lọc theo Vai trò</label>
                                        <select name="role" class="input-field">
                                            <option value="">-- Tất cả hệ thống --</option>
                                            <option value="ADMIN" ${filterRole == 'ADMIN' ? 'selected' : ''}>ADMIN (Quản trị Cổ đông)</option>
                                            <option value="IT" ${filterRole == 'IT' ? 'selected' : ''}>IT (Kỹ thuật)</option>
                                            <option value="SHAREHOLDER" ${filterRole == 'SHAREHOLDER' ? 'selected' : ''}>SHAREHOLDER (Cổ đông)</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label>Lọc theo Trạng thái</label>
                                        <select name="status" class="input-field">
                                            <option value="">-- Tất cả --</option>
                                            <option value="ACTIVE" ${filterStatus == 'ACTIVE' ? 'selected' : ''}>Hoạt động (ACTIVE)</option>
                                            <option value="LOCKED" ${filterStatus == 'LOCKED' ? 'selected' : ''}>Đã Khóa (LOCKED)</option>
                                        </select>
                                    </div>
                                </div>
                                <div>
                                    <div class="form-group">
                                        <label>Tiêu chí sắp xếp</label>
                                        <select name="sort" class="input-field">
                                            <option value="NEWEST_FIRST" ${filterSort == 'NEWEST_FIRST' ? 'selected' : ''}>Ngày tạo mới nhất</option>
                                            <option value="USERNAME_ASC" ${filterSort == 'USERNAME_ASC' ? 'selected' : ''}>Username theo bảng chữ cái (A-Z)</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="btn-group">
                                <button type="submit" class="btn-submit">Áp dụng Lọc</button>
                                <a class="btn-outline" href="?tab=filter">Xóa bộ lọc</a>
                            </div>
                        </form>
                    </div>
                    <c:set var="showTable" value="true" />
                </c:when>

                <%-- TAB: TÌM KIẾM TÀI KHOẢN (Mặc định) --%>
                <c:otherwise>
                    <div class="card">
                        <h2>Tìm kiếm tài khoản</h2>
                        <form method="get" action="${pageContext.request.contextPath}/app/it/user-management">
                            <input type="hidden" name="tab" value="search">
                            <div class="grid-2">
                                <div>
                                    <div class="form-group">
                                        <label>Tìm theo Username</label>
                                        <input type="text" name="username" class="input-field" value="${filterUsername}" placeholder="Ví dụ: Nguyen Van A">
                                    </div>
                                    <div class="form-group">
                                        <label>Tìm theo Email</label>
                                        <input type="text" name="email" class="input-field" value="${filterEmail}" placeholder="Ví dụ: a@gmail.com">
                                    </div>
                                </div>
                                <div>
                                    <div class="form-group">
                                        <label>Tiêu chí sắp xếp</label>
                                        <select name="sort" class="input-field">
                                            <option value="NEWEST_FIRST" ${filterSort == 'NEWEST_FIRST' ? 'selected' : ''}>Ngày tạo mới nhất</option>
                                            <option value="USERNAME_ASC" ${filterSort == 'USERNAME_ASC' ? 'selected' : ''}>Username theo bảng chữ cái (A-Z)</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="btn-group">
                                <button type="submit" class="btn-submit">Áp dụng Tìm kiếm</button>
                                <a class="btn-outline" href="?tab=search">Xóa tìm kiếm</a>
                            </div>
                        </form>
                    </div>
                    <c:set var="showTable" value="true" />
                </c:otherwise>
            </c:choose>

            <!-- Bảng hiển thị danh sách Tài khoản -->
            <c:if test="${showTable == 'true'}">
                <div class="card">
                    <h2>Danh sách tài khoản</h2>
                    <div class="table-wrapper">
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Username</th>
                                    <th>Email</th>
                                    <th>Vai trò</th>
                                    <th>Trạng thái</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="u" items="${users}">
                                    <tr>
                                        <td><span class="badge-id">#<c:out value="${u.userId}"/></span></td>
                                        <td style="font-weight: 500;"><c:out value="${u.username}"/></td>
                                        <td><c:out value="${u.email}"/></td>
                                        <td><span style="font-size:12px; font-weight:600; color:#475569;"><c:out value="${u.role}"/></span></td>
                                        <td>
                                            <span class="badge badge-${u.status == 'ACTIVE' ? 'active' : 'locked'}">
                                                <c:out value="${u.status}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <div class="action-group">
                                                <!-- Nút Sửa -->
                                                <button type="button" class="btn-action outline"
                                                        onclick="openEditModal('${u.userId}', '<c:out value="${u.username}" escapeXml="true"/>', '<c:out value="${u.email}" escapeXml="true"/>', '${u.role}')">
                                                    Sửa
                                                </button>
                                                
                                                <!-- Nút Khóa / Mở khóa -->
                                                <c:choose>
                                                    <c:when test="${u.status == 'ACTIVE'}">
                                                        <form method="post" action="${pageContext.request.contextPath}/app/it/user-management"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                            <input type="hidden" name="userId" value="${u.userId}">
                                                            <input type="hidden" name="action" value="lock">
                                                            <button type="submit" class="btn-action danger">Khóa</button>
                                                        </form>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <form method="post" action="${pageContext.request.contextPath}/app/it/user-management"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                            <input type="hidden" name="userId" value="${u.userId}">
                                                            <input type="hidden" name="action" value="unlock">
                                                            <button type="submit" class="btn-action primary">Mở khóa</button>
                                                        </form>
                                                    </c:otherwise>
                                                </c:choose>
                                                
                                                <!-- Nút Đặt lại Mật khẩu -->
                                                <form method="post" action="${pageContext.request.contextPath}/app/it/user-management"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                    <input type="hidden" name="userId" value="${u.userId}">
                                                    <input type="hidden" name="action" value="reset-password">
                                                    <button type="submit" class="btn-action outline" onclick="return confirm('Xác nhận đặt lại mật khẩu cho tài khoản này?');">Đặt lại MK</button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty users}">
                                    <tr>
                                        <td colspan="6" class="empty-state">Không có tài khoản nào khớp với điều kiện lọc/tìm kiếm.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

        </main>
    </div>
</div>

<!-- ================= MODAL SỬA USER ================= -->
<div id="editModal" class="modal-overlay">
    <div class="modal-content">
        <h2>
            Chỉnh sửa Tài khoản
            <svg onclick="closeEditModal()" style="cursor: pointer; color: #94a3b8;" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        </h2>
        <form method="post" action="${pageContext.request.contextPath}/app/it/user-management"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <input type="hidden" name="action" value="update">
            <input type="hidden" id="editUserId" name="userId">
            
            <div class="form-group">
                <label>Tên đăng nhập (Username)</label>
                <input type="text" id="editUsername" name="username" class="input-field" required>
            </div>
            
            <div class="form-group">
                <label>Email</label>
                <input type="email" id="editEmail" name="email" class="input-field" required>
            </div>
            
            <div class="form-group">
                <label>Vai trò hệ thống</label>
                <select id="editRole" name="role" class="input-field" required>
                    <option value="ADMIN">ADMIN</option>
                    <option value="IT">IT</option>
                    <option value="SHAREHOLDER">SHAREHOLDER</option>
                </select>
            </div>
            
            <div class="btn-group" style="justify-content: flex-end; margin-top: 24px;">
                <button type="button" class="btn-outline" onclick="closeEditModal()">Hủy bỏ</button>
                <button type="submit" class="btn-submit">Lưu thay đổi</button>
            </div>
        </form>
    </div>
</div>

<script>
    function openEditModal(userId, username, email, role) {
        document.getElementById('editUserId').value = userId;
        document.getElementById('editUsername').value = username;
        document.getElementById('editEmail').value = email;
        document.getElementById('editRole').value = role;
        document.getElementById('editModal').style.display = 'flex';
    }
    function closeEditModal() {
        document.getElementById('editModal').style.display = 'none';
    }
</script>

</body>
</html>
