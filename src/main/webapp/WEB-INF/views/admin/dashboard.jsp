<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-layout.css">
<style>
        /* --- DASHBOARD THÀNH PHẦN --- */
        .vg-hero { background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%); border-radius: 12px; padding: 28px 36px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; color: #ffffff; box-shadow: 0 4px 12px rgba(15, 23, 42, 0.1); position: relative; overflow: hidden; }
        .vg-hero-content { max-width: 600px; position: relative; z-index: 2; }
        .vg-hero-badge { display: inline-flex; align-items: center; gap: 6px; font-size: 11.5px; color: #38bdf8; background: rgba(56, 189, 248, 0.15); border: 1px solid rgba(56, 189, 248, 0.3); padding: 5px 12px; border-radius: 20px; margin-bottom: 14px; font-weight: 500; }
        .vg-hero h1 { font-size: 26px; font-weight: 700; margin: 0 0 10px 0; line-height: 1.3; color: #f8fafc; }
        .vg-hero p { color: #94a3b8; font-size: 13.5px; line-height: 1.5; margin: 0; }
        
        .vg-hero-visual { position: relative; z-index: 1; width: 140px; height: 140px; border-radius: 50%; border: 6px solid rgba(255, 255, 255, 0.05); border-top-color: #38bdf8; transform: rotate(45deg); display: flex; align-items: center; justify-content: center; }
        .vg-hero-visual::before { content: ""; position: absolute; width: 200px; height: 200px; background: radial-gradient(circle, rgba(56, 189, 248, 0.1) 0%, transparent 70%); transform: rotate(-45deg); }
        
        /* Nội dung bên trong hình tròn */
        .vg-hero-visual-inner {
            transform: rotate(-45deg); /* Xoay ngược lại để chữ đứng thẳng */
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            text-align: center;
            z-index: 3;
        }
        .vg-hero-visual-inner .pct {
            font-size: 32px;
            font-weight: 800;
            line-height: 1;
            margin-bottom: 4px;
            text-shadow: 0 2px 4px rgba(0,0,0,0.3);
        }
        .vg-hero-visual-inner .lbl {
            font-size: 10px;
            color: #94a3b8;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .section-head { margin-bottom: 12px; }
        .section-head h2 { font-size: 17px; font-weight: 700; color: #0f172a; margin: 0 0 4px 0; }
        .section-head p { font-size: 12.5px; color: #64748b; margin: 0; }

        .vg-stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 16px; margin-bottom: 28px; }
        .vg-stat-box { background: #ffffff; border-radius: 10px; padding: 20px; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05); display: flex; flex-direction: column; justify-content: space-between; transition: transform 0.2s ease, box-shadow 0.2s ease; }
        .vg-stat-box:hover { transform: translateY(-2px); box-shadow: 0 8px 15px -3px rgba(0,0,0,0.08); }
        .vg-stat-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
        .vg-icon { width: 40px; height: 40px; border-radius: 8px; display: flex; align-items: center; justify-content: center; }
        .vg-icon.blue { background: #eff6ff; color: #2563eb; }
        .vg-icon.danger { background: #fef2f2; color: #dc2626; }
        .vg-icon.warning { background: #fffbeb; color: #d97706; }
        .vg-icon svg { width: 20px; height: 20px; stroke-width: 2; }
        .vg-badge { font-size: 11px; font-weight: 600; padding: 4px 10px; border-radius: 20px; }
        .vg-badge.blue { background: #eff6ff; color: #2563eb; }
        .vg-badge.warning { background: #fffbeb; color: #d97706; }
        .vg-stat-bottom .num { font-size: 26px; font-weight: 700; color: #0f172a; margin: 0 0 4px 0; letter-spacing: -0.5px; }
        .vg-stat-bottom .label { font-size: 13px; font-weight: 500; color: #64748b; margin: 0; }

        .vg-modules { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; padding-bottom: 40px;}
        .vg-card { background: #ffffff; border-radius: 10px; padding: 24px; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05); display: flex; flex-direction: column; transition: border-color 0.2s; }
        .vg-card:hover { border-color: #cbd5e1; }
        .vg-card h3 { font-size: 16px; font-weight: 700; color: #0f172a; margin: 0 0 8px 0; }
        .vg-card p { color: #64748b; font-size: 13px; line-height: 1.6; margin: 0 0 20px 0; flex: 1; }
        .vg-actions { display: flex; gap: 10px; }
        .vg-btn { padding: 9px 16px; border-radius: 6px; font-size: 13px; font-weight: 500; text-decoration: none; text-align: center; transition: all 0.2s; cursor: pointer; }
        .vg-btn.primary { background: #2f6fed; color: #fff; border: 1px solid #2f6fed; box-shadow: 0 2px 4px rgba(47, 111, 237, 0.2); }
        .vg-btn.primary:hover { background: #2563eb; transform: translateY(-1px); }
        .vg-btn.outline { background: transparent; color: #0f172a; border: 1px solid #cbd5e1; }
        .vg-btn.outline:hover { background: #f8fafc; border-color: #94a3b8; }
        .vg-btn.danger-outline { background: transparent; color: #ef4444; border: 1px solid #fca5a5; }
        .vg-btn.danger-outline:hover { background: #fef2f2; }
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
            <a href="${pageContext.request.contextPath}/app/dashboard" class="nav-item active">
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
                <span>VINSCAPE &nbsp;>&nbsp;</span> Bảng Điều Khiển
            </div>
            <div class="tb-right">
                <div class="tb-user">Xin chào, <strong>admin124</strong> (ADMIN)</div>
                <a href="${pageContext.request.contextPath}/app/logout" class="tb-logout">Đăng xuất</a>
            </div>
        </header>

        <!-- DASHBOARD CONTENT (Trắng tinh khôi) -->
        <main class="vertical-dashboard">
            
            <div class="vg-hero">
                <div class="vg-hero-content">
                    <span class="vg-hero-badge">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><line x1="3" y1="9" x2="21" y2="9"></line><line x1="9" y1="21" x2="9" y2="9"></line></svg>
                        Quản trị viên
                    </span>
                    <h1>Trung tâm Quản trị Hệ thống</h1>
                    <p>Kiểm soát và điều hành toàn diện hệ thống Cổ đông theo tiêu chuẩn hiện đại, chuyên nghiệp và minh bạch cao nhất của SVT.</p>
                </div>
                
                <div class="vg-hero-visual">
                    <div class="vg-hero-visual-inner">
                        <!-- TÍNH TOÁN DỮ LIỆU ĐỘNG BẰNG JSTL -->
                        <c:set var="pUsers" value="${pendingUsersCount != null ? pendingUsersCount : 0}" />
                        <c:set var="pTrans" value="${pendingTransfersCount != null ? pendingTransfersCount : 0}" />
                        
                        <span class="pct" style="color: #f59e0b;"><c:out value="${pUsers + pTrans}"/></span>
                        <span class="lbl">Yêu cầu<br>Chờ duyệt</span>
                    </div>
                </div>

            </div>

            <div class="section-head">
                <h2>Chỉ số hệ thống</h2>
                <p>Dữ liệu tổng quan được cập nhật theo thời gian thực</p>
            </div>

            <div class="vg-stats">
                <div class="vg-stat-box">
                    <div class="vg-stat-top">
                        <div class="vg-icon blue"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg></div>
                        <div class="vg-badge blue">ĐÃ DUYỆT</div>
                    </div>
                    <div class="vg-stat-bottom">
                        <div class="num"><c:out value="${totalShareholders != null ? totalShareholders : '0'}"/></div>
                        <div class="label">Tổng số Cổ đông</div>
                    </div>
                </div>

                <div class="vg-stat-box">
                    <div class="vg-stat-top">
                        <div class="vg-icon danger"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg></div>
                        <div class="vg-badge warning">CẦN XỬ LÝ</div>
                    </div>
                    <div class="vg-stat-bottom">
                        <div class="num"><c:out value="${pendingUsersCount != null ? pendingUsersCount : '0'}"/></div>
                        <div class="label">Cổ đông chờ duyệt</div>
                    </div>
                </div>

                <div class="vg-stat-box">
                    <div class="vg-stat-top">
                        <div class="vg-icon warning"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><polyline points="17 1 21 5 17 9"></polyline><path d="M3 11V9a4 4 0 0 1 4-4h14"></path><polyline points="7 23 3 19 7 15"></polyline><path d="M21 13v2a4 4 0 0 1-4 4H3"></path></svg></div>
                        <div class="vg-badge warning">CẦN XỬ LÝ</div>
                    </div>
                    <div class="vg-stat-bottom">
                        <div class="num"><c:out value="${pendingTransfersCount != null ? pendingTransfersCount : '0'}"/></div>
                        <div class="label">Giao dịch chờ XL</div>
                    </div>
                </div>
            </div>

            <div class="vg-modules">
                <div class="vg-card">
                    <h3>Cổ đông & Giao dịch</h3>
                    <p>Quản lý danh sách cổ đông, cấp phát cổ phần ban đầu và xét duyệt các giao dịch chuyển nhượng bảo mật.</p>
                    <div class="vg-actions">
                        <a href="${pageContext.request.contextPath}/app/admin/shareholders" class="vg-btn primary">Danh sách</a>
                        <a href="${pageContext.request.contextPath}/app/admin/transfer-approval" class="vg-btn outline">Duyệt Giao dịch</a>
                    </div>
                </div>

                <div class="vg-card">
                    <h3>Phát hành & Cổ tức</h3>
                    <p>Khởi tạo các đợt phát hành cổ phần mới, chia cổ tức định kỳ và thiết lập các kỳ Đại hội đồng cổ đông.</p>
                    <div class="vg-actions">
                        <a href="${pageContext.request.contextPath}/app/admin/share-issue" class="vg-btn primary">Phát hành mới</a>
                        <a href="${pageContext.request.contextPath}/app/admin/resolution" class="vg-btn outline">Nghị quyết</a>
                    </div>
                </div>

                <div class="vg-card">
                    <h3>Báo cáo TC & Tài liệu</h3>
                    <p>Hệ thống công bố thông tin minh bạch: Báo cáo tài chính thường niên, quý và các tài liệu nội bộ.</p>
                    <div class="vg-actions">
                        <a href="${pageContext.request.contextPath}/app/admin/financial-reports/manage" class="vg-btn primary">Báo cáo TC</a>
                        <a href="${pageContext.request.contextPath}/app/admin/documents" class="vg-btn outline">Tài liệu</a>
                    </div>
                </div>

                <div class="vg-card">
                    <h3 style="color: #ef4444;">Can thiệp Kỹ thuật</h3>
                    <p>Khu vực đặc quyền Quản trị viên cấp cao: Điều chỉnh trực tiếp số dư cổ phần. Yêu cầu tính chính xác tuyệt đối.</p>
                    <div class="vg-actions">
                        <a href="${pageContext.request.contextPath}/app/admin/share-adjust" class="vg-btn danger-outline">Điều chỉnh Cổ phần</a>
                    </div>
                </div>
            </div>

        </main>
    </div>
</div>

</body>
</html>