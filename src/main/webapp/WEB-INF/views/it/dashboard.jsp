<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<%@ include file="/WEB-INF/views/common/ga4.jsp" %>
    <meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bảng Điều Khiển IT - VinScape</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-layout.css">
<style>
        /* =========================================================
           3. IT DASHBOARD CONTENT STYLES
           ========================================================= */
        .vg-hero {
            position: relative; overflow: hidden; border-radius: 20px;
            background: radial-gradient(120% 140% at 14% 8%, #173561 0%, #0a1424 46%, #060b16 100%);
            padding: 44px 40px; text-align: left; margin-bottom: 30px;
            box-shadow: 0 10px 30px -10px rgba(0,0,0,0.5);
        }
        
        .vg-hero-hexfield {
            position: absolute; inset: 0; opacity: 0.5; pointer-events: none;
            width: 100%; height: 100%;
        }
        
        .vg-hero h1 {
            position: relative; z-index: 1; color: #fff; font-size: 32px; font-weight: 800;
            margin: 0 0 15px 0; letter-spacing: 1px;
        }
        
        .vg-hero p {
            position: relative; z-index: 1; color: #aebedd; font-size: 15px; margin: 0;
            max-width: 750px; line-height: 1.6;
        }

        .vg-modules { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
        @media (max-width: 800px) { .vg-modules { grid-template-columns: 1fr; } }

        .vg-card {
            background: #fff; border-radius: 14px; overflow: hidden;
            border: 1px solid #e2e7f0;
            box-shadow: 0 1px 3px rgba(15,27,45,.04), 0 8px 24px -12px rgba(15,27,45,.12);
            display: flex; flex-direction: column; transition: transform 0.2s, box-shadow 0.2s;
        }
        .vg-card:hover { transform: translateY(-2px); box-shadow: 0 12px 28px -12px rgba(15,27,45,.18); }

        .vg-card-header { padding: 20px 25px; border-bottom: 1px solid #e2e7f0; background: #fff; }
        .vg-card-header h2 {
            margin: 0; color: #0f1b2d; font-size: 17px; font-weight: 700;
            display: flex; align-items: center; gap: 10px;
        }
        .vg-card-header h2::before {
            content: ""; display: block; width: 5px; height: 18px;
            background: #38bdf8; border-radius: 3px;
        }

        .vg-card-body { padding: 25px; flex: 1; display: flex; flex-direction: column; }
        .vg-card-body p { color: #6d7c94; font-size: 14px; line-height: 1.6; margin-top: 0; margin-bottom: 20px; flex: 1; }

        .vg-actions { display: flex; gap: 12px; }

        .vg-btn.primary {
            flex: 1; text-align: center; position: relative; overflow: hidden;
            background: linear-gradient(135deg, #38bdf8 0%, #0284c7 100%);
            color: #fff !important; border: none; border-radius: 8px; padding: 12px 20px;
            font-weight: 700; font-size: 13.5px; text-transform: uppercase;
            letter-spacing: 0.5px; transition: all 0.3s ease; text-decoration: none;
            box-shadow: 0 4px 15px rgba(2, 132, 199, 0.3); z-index: 1; display: block;
        }
        .vg-btn.primary::before {
            content: ""; position: absolute; top: 0; left: 0; right: 0; bottom: 0;
            background: linear-gradient(135deg, #0284c7 0%, #0369a1 100%);
            z-index: -1; opacity: 0; transition: opacity 0.3s ease;
        }
        .vg-btn.primary:hover::before { opacity: 1; }
        .vg-btn.primary:hover {
            transform: translateY(-2px); box-shadow: 0 8px 20px rgba(2, 132, 199, 0.5);
        }
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
            <div class="nav-section">ĐIỀU HÀNH KỸ THUẬT (IT)</div>
            <!-- Đã thêm class "active" để tô sáng mục này -->
            <a href="${pageContext.request.contextPath}/app/dashboard" class="nav-item active">
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
                <span>VINSCAPE &nbsp;>&nbsp;</span> Bảng Điều Khiển (IT)
            </div>
            <div class="tb-right">
                <div class="tb-user">Xin chào, <strong>itadmin</strong> (IT)</div>
                <a href="${pageContext.request.contextPath}/app/logout" class="tb-logout">Đăng xuất</a>
            </div>
        </header>

        <!-- DASHBOARD CONTENT -->
        <main class="vertical-dashboard">
            
            <div class="vg-hero">
                <svg class="vg-hero-hexfield" viewBox="0 0 600 400" preserveAspectRatio="xMidYMid slice">
                    <g opacity="0.5" stroke="#38bdf8" stroke-width="1.5">
                        <polygon points="480,30 520,50 520,90 480,110 440,90 440,50" fill="none"/>
                        <polygon points="540,90 580,110 580,150 540,170 500,150 500,110" fill="none"/>
                        <polygon points="470,150 510,170 510,210 470,230 430,210 430,170" fill="none"/>
                        <polygon points="530,210 570,230 570,270 530,290 490,270 490,230" fill="none"/>
                    </g>
                </svg>
                <h1>Quản Trị Kỹ Thuật (IT)</h1>
                <p>Trung tâm quản trị người dùng, bảo mật và hỗ trợ kỹ thuật chuyên sâu cho toàn bộ hệ thống VinScape.</p>
            </div>

            <div class="vg-modules">
                
                <div class="vg-card">
                    <div class="vg-card-header">
                        <h2>
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:20px;height:20px;color:#38bdf8;"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="8.5" cy="7" r="4"/><line x1="20" y1="8" x2="20" y2="14"/><line x1="23" y1="11" x2="17" y2="11"/></svg>
                            Tạo Tài khoản mới
                        </h2>
                    </div>
                    <div class="vg-card-body">
                        <p>Khởi tạo tài khoản mới cho Cổ đông hoặc Quản trị viên hệ thống một cách an toàn và bảo mật.</p>
                        <div class="vg-actions">
                            <a href="${pageContext.request.contextPath}/app/it/user-management?tab=create" class="vg-btn primary">Đến trang Tạo</a>
                        </div>
                    </div>
                </div>

                <div class="vg-card">
                    <div class="vg-card-header">
                        <h2>
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:20px;height:20px;color:#38bdf8;"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                            Tìm kiếm Tài khoản
                        </h2>
                    </div>
                    <div class="vg-card-body">
                        <p>Tra cứu nhanh thông tin, hỗ trợ mở khóa và đặt lại mật khẩu cho người dùng hiện tại trên hệ thống.</p>
                        <div class="vg-actions">
                            <a href="${pageContext.request.contextPath}/app/it/user-management?tab=search" class="vg-btn primary">Đến Tìm kiếm</a>
                        </div>
                    </div>
                </div>

                <div class="vg-card">
                    <div class="vg-card-header">
                        <h2>
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:20px;height:20px;color:#38bdf8;"><polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"/></svg>
                            Lọc Tài khoản
                        </h2>
                    </div>
                    <div class="vg-card-body">
                        <p>Phân loại và truy xuất hàng loạt tài khoản dựa trên Vai trò (Role) và Trạng thái bảo mật (Status).</p>
                        <div class="vg-actions">
                            <a href="${pageContext.request.contextPath}/app/it/user-management?tab=filter" class="vg-btn primary">Đến trang Lọc</a>
                        </div>
                    </div>
                </div>

                <div class="vg-card">
                    <div class="vg-card-header">
                        <h2>
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:20px;height:20px;color:#38bdf8;"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                            Tài liệu Hệ thống
                        </h2>
                    </div>
                    <div class="vg-card-body">
                        <p>Quản lý tài liệu và các tệp đính kèm. Dọn dẹp tài liệu lỗi hoặc quá hạn để giải phóng dung lượng máy chủ.</p>
                        <div class="vg-actions">
                            <a href="${pageContext.request.contextPath}/app/it/documents" class="vg-btn primary">Đến trang Tài liệu</a>
                        </div>
                    </div>
                </div>

                <div class="vg-card">
                    <div class="vg-card-header">
                        <h2>
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:20px;height:20px;color:#38bdf8;"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                            Lịch sử Hệ thống
                        </h2>
                    </div>
                    <div class="vg-card-body">
                        <p>Xem lại toàn bộ nhật ký (audit log) các thao tác quan trọng đã diễn ra trên hệ thống.</p>
                        <div class="vg-actions">
                            <a href="${pageContext.request.contextPath}/app/it/system-history" class="vg-btn primary">Xem Lịch sử</a>
                        </div>
                    </div>
                </div>

            </div>

        </main>
    </div>
</div>

</body>
</html>
