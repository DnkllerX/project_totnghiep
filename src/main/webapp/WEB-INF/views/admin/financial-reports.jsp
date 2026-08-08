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
    <title>Báo cáo Tài chính - VinScape</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-layout.css">
<style>
        /* =========================================================
           3. FINANCIAL REPORTS CONTENT STYLES
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
        .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; margin-bottom: 8px;}
        @media (max-width: 900px) { .grid-2 { grid-template-columns: 1fr; } }
        
        .form-group { margin-bottom: 16px; }
        
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

        .btn-submit {
            background: var(--primary); color: #ffffff; border: none;
            padding: 12px 24px; border-radius: 6px; font-size: 14px; font-weight: 600;
            cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 4px rgba(47, 111, 237, 0.2);
            margin-top: 8px; display: inline-flex; align-items: center; gap: 8px;
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

        .badge-year {
            font-weight: 700; color: var(--primary); min-width: 40px;
            background: var(--info-bg); padding: 4px 8px; border-radius: 6px; 
            text-align: center; display: inline-block; font-size: 12.5px;
        }

        .text-money { font-weight: 600; color: #10b981; }
        .text-bold { font-weight: 600; }

        /* --- OVERVIEW: HERO + METRIC CARDS + CHART (giong trang cua shareholder, tong mau sang) --- */
        .fr-hero {
            background: #ffffff; border: 1px solid var(--border-color); border-radius: 12px;
            padding: 24px 28px; margin-bottom: 24px;
            display: grid; grid-template-columns: minmax(0, 1.5fr) minmax(240px, 0.8fr);
            gap: 24px; align-items: center;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        }
        .fr-eyebrow {
            color: var(--primary); font-size: 12px; font-weight: 700;
            letter-spacing: 1px; text-transform: uppercase; margin-bottom: 8px;
        }
        .fr-hero h2 { margin: 0 0 8px 0; color: var(--text-main); font-size: 22px; }
        .fr-hero p { margin: 0; color: var(--text-muted); font-size: 13px; line-height: 1.6; }
        .fr-latest-box { background: var(--info-bg); border-radius: 8px; padding: 16px; }
        .fr-latest-label { color: var(--text-muted); font-size: 12px; margin-bottom: 6px; }
        .fr-latest-period { color: var(--primary); font-size: 22px; font-weight: 800; margin-bottom: 10px; }

        .metric-grid {
            display: grid; grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 16px; margin-bottom: 24px;
        }
        @media (max-width: 1000px) { .metric-grid { grid-template-columns: repeat(2, 1fr); } }
        .metric-card {
            background: #fff; border: 1px solid var(--border-color); border-radius: 10px;
            padding: 16px 18px; box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        }
        .metric-card span { display: block; color: var(--text-muted); font-size: 12px; margin-bottom: 6px; }
        .metric-card strong { color: var(--text-main); font-size: 19px; font-weight: 800; }
        .metric-card small { display: block; color: #94a3b8; font-size: 11px; margin-top: 6px; }
        .metric-card strong.mc-up { color: #16a34a; }
        .metric-card strong.mc-down { color: #dc2626; }
        .mc-delta { display: inline-flex; align-items: center; gap: 3px; font-size: 11.5px; font-weight: 700; margin-left: 8px; vertical-align: middle; }
        .mc-delta.mc-up { color: #16a34a; }
        .mc-delta.mc-down { color: #dc2626; }
        .mc-delta svg { width: 12px; height: 12px; }

        .chart-canvas-box { height: 300px; position: relative; }
        #financialChartAdmin { width: 100%; height: 100%; display: block; }
        .chart-legend { display: flex; flex-wrap: wrap; gap: 14px; margin-top: 14px; color: var(--text-muted); font-size: 12px; }
        .legend-item { display: inline-flex; align-items: center; gap: 6px; }
        .legend-dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; }

        /* --- PIE CHART: co cau doanh thu theo quy, chon nam dong (kieu fireant.vn) --- */
        .fr-pie-layout { display: grid; grid-template-columns: minmax(0, 260px) 1fr; gap: 28px; align-items: center; }
        @media (max-width: 760px) { .fr-pie-layout { grid-template-columns: 1fr; } }
        .fr-year-tabs { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 18px; }
        .fr-year-tab {
            border: 1px solid var(--border-color); background: #f8fafc; color: var(--text-muted);
            font-size: 12.5px; font-weight: 600; padding: 6px 14px; border-radius: 999px;
            cursor: pointer; transition: all .15s;
        }
        .fr-year-tab:hover { border-color: var(--primary); color: var(--primary); }
        .fr-year-tab.active { background: var(--primary); border-color: var(--primary); color: #fff; }
        .fr-pie-canvas-box { position: relative; width: 220px; height: 220px; margin: 0 auto; }
        #financialPieAdmin { width: 100%; height: 100%; display: block; }
        .fr-pie-center { position: absolute; inset: 0; display: flex; flex-direction: column; align-items: center; justify-content: center; pointer-events: none; }
        .fr-pie-center .fr-pie-total { font-size: 17px; font-weight: 800; color: var(--text-main); }
        .fr-pie-center .fr-pie-total-label { font-size: 10.5px; color: var(--text-muted); margin-top: 2px; }
        .fr-pie-legend { display: flex; flex-direction: column; gap: 12px; }
        .fr-pie-legend-item { display: flex; align-items: center; justify-content: space-between; gap: 12px; font-size: 13px; }
        .fr-pie-legend-left { display: flex; align-items: center; gap: 9px; color: var(--text-main); font-weight: 600; }
        .fr-pie-legend-dot { width: 11px; height: 11px; border-radius: 3px; flex-shrink: 0; }
        .fr-pie-legend-pct { font-weight: 800; color: var(--text-main); }
        .fr-pie-legend-value { color: var(--text-muted); font-size: 11.5px; }
        .fr-pie-empty { color: var(--text-muted); font-size: 13px; font-style: italic; text-align: center; padding: 20px; }

        .empty-state { text-align: center; color: var(--text-muted); padding: 32px !important; font-style: italic; }

        .btn-edit-row {
            display: inline-flex; align-items: center; gap: 5px; background: #fff; color: var(--primary);
            border: 1px solid var(--border-color); padding: 6px 12px; border-radius: 6px;
            font-size: 12px; font-weight: 600; cursor: pointer; transition: all .15s ease; white-space: nowrap;
        }
        .btn-edit-row:hover { background: var(--info-bg); border-color: var(--primary); transform: translateY(-1px); }
        tr.fr-editing-row td { background-color: var(--info-bg) !important; }
        #frFormCard.fr-editing { border-color: var(--primary); box-shadow: 0 0 0 3px rgba(47,111,237,0.12); }
    </style>
</head>
<body>

<c:forEach var="r" items="${reports}" varStatus="status">
    <c:if test="${status.first}"><c:set var="latestReport" value="${r}" /></c:if>
    <c:if test="${status.index == 1}"><c:set var="prevReport" value="${r}" /></c:if>
</c:forEach>

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
            <a href="${pageContext.request.contextPath}/app/admin/share-adjust" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>
                Điều chỉnh Cổ phần
            </a>

            <div class="nav-section">DỮ LIỆU HỆ THỐNG</div>
            <a href="${pageContext.request.contextPath}/app/admin/documents" class="nav-item">
                <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
                Tài liệu Hệ thống
            </a>
            
            <!-- Đã thêm class "active" để tô sáng mục này -->
            <a href="${pageContext.request.contextPath}/app/admin/financial-reports/manage" class="nav-item active">
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
                <span>VINSCAPE &nbsp;>&nbsp;</span> Báo cáo Tài chính
            </div>
            <div class="tb-right">
                <div class="tb-user">Xin chào, <strong><c:out value="${sessionScope.username}"/></strong> (<c:out value="${sessionScope.role}"/>)</div>
                <a href="${pageContext.request.contextPath}/app/logout" class="tb-logout">Đăng xuất</a>
            </div>
        </header>

        <!-- CONTENT -->
        <main class="vertical-dashboard">
            
            <h1 class="page-title">Báo cáo Tài chính</h1>

            <c:if test="${not empty error}">
                <div class="error-box">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <!-- TỔNG QUAN: hero + metric card + bieu do (giong man hinh cua co dong) -->
            <div class="fr-hero">
                <div>
                    <div class="fr-eyebrow">Tổng quan tài chính</div>
                    <h2>Sức khỏe tài chính công ty</h2>
                    <p>Theo dõi doanh thu, lợi nhuận và các chỉ số hiệu quả sinh lời qua từng quý.
                       Dữ liệu lấy trực tiếp từ các báo cáo đã nhập bên dưới.</p>
                </div>
                <div class="fr-latest-box">
                    <c:choose>
                        <c:when test="${not empty latestReport}">
                            <div class="fr-latest-label">Kỳ báo cáo mới nhất</div>
                            <div class="fr-latest-period">Q<c:out value="${latestReport.reportQuarter}"/>/<c:out value="${latestReport.reportYear}"/></div>
                        </c:when>
                        <c:otherwise>
                            <div class="fr-latest-label">Kỳ báo cáo mới nhất</div>
                            <div class="fr-latest-period" style="font-size:15px;">Chưa có dữ liệu</div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="metric-grid">
                <div class="metric-card">
                    <span>Doanh thu</span>
                    <c:choose>
                        <c:when test="${not empty latestReport and not empty prevReport and not empty latestReport.revenue and not empty prevReport.revenue}">
                            <c:choose>
                                <c:when test="${latestReport.revenue gt prevReport.revenue}">
                                    <strong class="mc-up"><fmt:formatNumber value="${latestReport.revenue}" type="number" maxFractionDigits="0"/></strong>
                                    <span class="mc-delta mc-up"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="12" y1="19" x2="12" y2="5"></line><polyline points="5 12 12 5 19 12"></polyline></svg>Tăng</span>
                                </c:when>
                                <c:when test="${latestReport.revenue lt prevReport.revenue}">
                                    <strong class="mc-down"><fmt:formatNumber value="${latestReport.revenue}" type="number" maxFractionDigits="0"/></strong>
                                    <span class="mc-delta mc-down"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="12" y1="5" x2="12" y2="19"></line><polyline points="19 12 12 19 5 12"></polyline></svg>Giảm</span>
                                </c:when>
                                <c:otherwise>
                                    <strong><fmt:formatNumber value="${latestReport.revenue}" type="number" maxFractionDigits="0"/></strong>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <strong><fmt:formatNumber value="${latestReport.revenue}" type="number" maxFractionDigits="0"/></strong>
                        </c:otherwise>
                    </c:choose>
                    <small>Kỳ mới nhất</small>
                </div>
                <div class="metric-card">
                    <span>Lợi nhuận sau thuế</span>
                    <c:choose>
                        <c:when test="${not empty latestReport and not empty prevReport and not empty latestReport.profitAfterTax and not empty prevReport.profitAfterTax}">
                            <c:choose>
                                <c:when test="${latestReport.profitAfterTax gt prevReport.profitAfterTax}">
                                    <strong class="mc-up"><fmt:formatNumber value="${latestReport.profitAfterTax}" type="number" maxFractionDigits="0"/></strong>
                                    <span class="mc-delta mc-up"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="12" y1="19" x2="12" y2="5"></line><polyline points="5 12 12 5 19 12"></polyline></svg>Tăng</span>
                                </c:when>
                                <c:when test="${latestReport.profitAfterTax lt prevReport.profitAfterTax}">
                                    <strong class="mc-down"><fmt:formatNumber value="${latestReport.profitAfterTax}" type="number" maxFractionDigits="0"/></strong>
                                    <span class="mc-delta mc-down"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="12" y1="5" x2="12" y2="19"></line><polyline points="19 12 12 19 5 12"></polyline></svg>Giảm</span>
                                </c:when>
                                <c:otherwise>
                                    <strong><fmt:formatNumber value="${latestReport.profitAfterTax}" type="number" maxFractionDigits="0"/></strong>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <strong><fmt:formatNumber value="${latestReport.profitAfterTax}" type="number" maxFractionDigits="0"/></strong>
                        </c:otherwise>
                    </c:choose>
                    <small>Kỳ mới nhất</small>
                </div>
                <div class="metric-card">
                    <span>Lợi nhuận trước thuế</span>
                    <strong><fmt:formatNumber value="${latestReport.profitBeforeTax}" type="number" maxFractionDigits="0"/></strong>
                    <small>LNTT · Kỳ mới nhất</small>
                </div>
                <div class="metric-card">
                    <span>ROE / ROA</span>
                    <strong><fmt:formatNumber value="${latestReport.roe}" type="number" maxFractionDigits="2"/>% / <fmt:formatNumber value="${latestReport.roa}" type="number" maxFractionDigits="2"/>%</strong>
                    <small>Hiệu quả sinh lời</small>
                </div>
                <div class="metric-card">
                    <span>EPS</span>
                    <strong><fmt:formatNumber value="${latestReport.eps}" type="number" maxFractionDigits="0"/></strong>
                    <small>Lợi nhuận / cổ phần</small>
                </div>
                <div class="metric-card">
                    <span>Nợ ngắn hạn / dài hạn</span>
                    <strong><fmt:formatNumber value="${latestReport.shortTermDebt}" type="number" maxFractionDigits="0"/> / <fmt:formatNumber value="${latestReport.longTermDebt}" type="number" maxFractionDigits="0"/></strong>
                    <small>Cơ cấu nợ</small>
                </div>
            </div>

            <div class="card">
                <h2>Biểu đồ doanh thu và lợi nhuận</h2>
                <div class="chart-canvas-box">
                    <canvas id="financialChartAdmin" aria-label="Biểu đồ tài chính" role="img"></canvas>
                </div>
                <div class="chart-legend">
                    <span class="legend-item"><i class="legend-dot" style="background:#2f6fed;"></i>Doanh thu</span>
                    <span class="legend-item"><i class="legend-dot" style="background:#10b981;"></i>Lợi nhuận sau thuế</span>
                    <span class="legend-item"><i class="legend-dot" style="background:#f59e0b;"></i>EPS</span>
                </div>
            </div>

            <div class="chart-data" id="chartDataAdmin" style="display:none;">
                <c:forEach var="r" items="${reports}">
                    <span class="report-point-admin"
                          data-label="Q${r.reportQuarter}/${r.reportYear}"
                          data-year="${r.reportYear}"
                          data-quarter="${r.reportQuarter}"
                          data-revenue="${empty r.revenue ? 0 : r.revenue}"
                          data-profit="${empty r.profitAfterTax ? 0 : r.profitAfterTax}"
                          data-eps="${empty r.eps ? 0 : r.eps}"></span>
                </c:forEach>
            </div>

            <div class="card">
                <h2>Cơ cấu doanh thu theo quý</h2>
                <p style="margin: -14px 0 18px; color: var(--text-muted); font-size: 12.5px;">
                    Tỷ trọng doanh thu 4 quý trong một năm, quy về tổng 100%. Chọn năm để xem — danh sách năm lấy trực tiếp từ dữ liệu đã nhập, không cố định.
                </p>
                <div id="frYearTabs" class="fr-year-tabs"></div>
                <div class="fr-pie-layout">
                    <div class="fr-pie-canvas-box">
                        <canvas id="financialPieAdmin" aria-label="Biểu đồ tỷ trọng doanh thu theo quý" role="img"></canvas>
                        <div class="fr-pie-center">
                            <div class="fr-pie-total" id="frPieTotal">--</div>
                            <div class="fr-pie-total-label">Tổng doanh thu năm</div>
                        </div>
                    </div>
                    <div class="fr-pie-legend" id="frPieLegend"></div>
                </div>
            </div>

            <!-- Thẻ 1: Form Thêm / Sửa báo cáo -->
            <div class="card" id="frFormCard">
                <h2 id="frFormTitle" style="display:flex; align-items:center; justify-content:space-between; width:100%;">
                    <span>Thêm báo cáo quý mới</span>
                    <a href="#" id="frCancelEdit" style="display:none; font-size:12px; font-weight:600; color:var(--text-muted); text-decoration:none; border-bottom:1px dashed var(--text-muted); padding-bottom:2px;">✕ Hủy chỉnh sửa</a>
                </h2>
                <form method="post" action="${pageContext.request.contextPath}/app/admin/financial-reports/manage" id="frReportForm"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"><input type="hidden" name="reportId" id="frReportId" value="">
                    <div class="grid-2">
                        <!-- Cột 1 -->
                        <div>
                            <div class="form-group">
                                <label>Năm</label>
                                <input type="text" name="reportYear" id="frReportYear" class="input-field" list="year-list"
                                       autocomplete="off" required pattern="[0-9]{4}" placeholder="VD: 2026">
                                <jsp:useBean id="nowDate" class="java.util.Date" />
                                <fmt:formatDate value="${nowDate}" pattern="yyyy" var="currentYear" />
                                <datalist id="year-list">
                                    <c:forEach var="i" begin="0" end="${currentYear - 1940}">
                                        <c:set var="y" value="${currentYear - i}" />
                                        <option value="${y}">
                                    </c:forEach>
                                </datalist>
                            </div>
                            <div class="form-group">
                                <label>Quý (1-4)</label>
                                <select name="reportQuarter" id="frReportQuarter" class="input-field" required>
                                    <option value="" disabled selected>-- Chọn quý --</option>
                                    <option value="1">Quý 1</option>
                                    <option value="2">Quý 2</option>
                                    <option value="3">Quý 3</option>
                                    <option value="4">Quý 4</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Doanh thu (VNĐ)</label>
                                <input type="text" name="revenue" id="frRevenue" class="input-field" placeholder="VD: 5000000000">
                            </div>
                            <div class="form-group">
                                <label>Lợi nhuận trước thuế (VNĐ)</label>
                                <input type="text" name="profitBeforeTax" id="frProfitBeforeTax" class="input-field" placeholder="VD: 1500000000">
                            </div>
                            <div class="form-group">
                                <label>Lợi nhuận sau thuế (VNĐ)</label>
                                <input type="text" name="profitAfterTax" id="frProfitAfterTax" class="input-field" placeholder="VD: 1200000000">
                            </div>
                        </div>
                        
                        <!-- Cột 2 -->
                        <div>
                            <div class="form-group">
                                <label>Nợ ngắn hạn (VNĐ)</label>
                                <input type="text" name="shortTermDebt" id="frShortTermDebt" class="input-field" placeholder="VD: 500000000">
                            </div>
                            <div class="form-group">
                                <label>Nợ dài hạn (VNĐ)</label>
                                <input type="text" name="longTermDebt" id="frLongTermDebt" class="input-field" placeholder="VD: 2000000000">
                            </div>
                            <div class="form-group">
                                <label>EPS</label>
                                <input type="text" name="eps" id="frEps" class="input-field" placeholder="VD: 2500">
                            </div>
                            <div class="form-group">
                                <label>P/E</label>
                                <input type="text" name="pe" id="frPe" class="input-field" placeholder="VD: 15.5">
                            </div>
                            <div class="form-group">
                                <label>ROE (%)</label>
                                <input type="text" name="roe" id="frRoe" class="input-field" placeholder="VD: 18.5">
                            </div>
                            <div class="form-group">
                                <label>ROA (%)</label>
                                <input type="text" name="roa" id="frRoa" class="input-field" placeholder="VD: 12.0">
                            </div>
                        </div>
                    </div>
                    
                    <button type="submit" class="btn-submit" id="frSubmitBtn">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
                        <span>Thêm báo cáo</span>
                    </button>
                </form>
            </div>

            <!-- Thẻ 2: Danh sách Báo cáo -->
            <div class="card">
                <h2>Danh sách báo cáo</h2>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Năm</th>
                                <th>Quý</th>
                                <th>Doanh thu</th>
                                <th>LNTT</th>
                                <th>LNST</th>
                                <th>Nợ ngắn hạn</th>
                                <th>Nợ dài hạn</th>
                                <th>EPS</th>
                                <th>ROE (%)</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="r" items="${reports}">
                                <tr>
                                    <td><span class="badge-year"><c:out value="${r.reportYear}"/></span></td>
                                    <td class="text-bold"><c:out value="${r.reportQuarter}"/></td>
                                    <td class="text-money"><fmt:formatNumber value="${r.revenue}" type="number" maxFractionDigits="0"/></td>
                                    <td class="text-money"><fmt:formatNumber value="${r.profitBeforeTax}" type="number" maxFractionDigits="0"/></td>
                                    <td class="text-money"><fmt:formatNumber value="${r.profitAfterTax}" type="number" maxFractionDigits="0"/></td>
                                    <td><fmt:formatNumber value="${r.shortTermDebt}" type="number" maxFractionDigits="0"/></td>
                                    <td><fmt:formatNumber value="${r.longTermDebt}" type="number" maxFractionDigits="0"/></td>
                                    <td class="text-bold"><fmt:formatNumber value="${r.eps}" type="number" maxFractionDigits="0"/></td>
                                    <td><fmt:formatNumber value="${r.roe}" type="number" maxFractionDigits="2"/></td>
                                    <td>
                                        <button type="button" class="btn-edit-row"
                                                data-id="${r.reportId}" data-year="${r.reportYear}" data-quarter="${r.reportQuarter}"
                                                data-revenue="${r.revenue}" data-pbt="${r.profitBeforeTax}" data-pat="${r.profitAfterTax}"
                                                data-std="${r.shortTermDebt}" data-ltd="${r.longTermDebt}" data-eps="${r.eps}"
                                                data-pe="${r.pe}" data-roe="${r.roe}" data-roa="${r.roa}">
                                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4z"></path></svg>
                                            Sửa
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty reports}">
                                <tr>
                                    <td colspan="10" class="empty-state">Chưa có báo cáo tài chính nào trên hệ thống.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

        </main>
    </div>
</div>

<script>
    (function () {
        const canvas = document.getElementById('financialChartAdmin');
        if (!canvas) return;

        const points = Array.from(document.querySelectorAll('.report-point-admin')).map(function (node) {
            return {
                label: node.dataset.label,
                revenue: Number(node.dataset.revenue || 0),
                profit: Number(node.dataset.profit || 0),
                eps: Number(node.dataset.eps || 0)
            };
        }).reverse();

        const ctx = canvas.getContext('2d');
        const box = canvas.parentElement;
        const ratio = window.devicePixelRatio || 1;

        function formatShort(value) {
            const abs = Math.abs(value);
            if (abs >= 1000000000000) return (value / 1000000000000).toFixed(1) + 'T';
            if (abs >= 1000000000) return (value / 1000000000).toFixed(1) + 'B';
            if (abs >= 1000000) return (value / 1000000).toFixed(1) + 'M';
            if (abs >= 1000) return (value / 1000).toFixed(1) + 'K';
            return String(value || 0);
        }

        function drawEmpty(width, height) {
            ctx.clearRect(0, 0, width, height);
            ctx.fillStyle = '#94a3b8';
            ctx.font = '14px Inter, sans-serif';
            ctx.textAlign = 'center';
            ctx.fillText('Chưa có dữ liệu để vẽ biểu đồ', width / 2, height / 2);
        }

        function drawChart() {
            const width = box.clientWidth;
            const height = box.clientHeight;
            canvas.width = width * ratio;
            canvas.height = height * ratio;
            canvas.style.width = width + 'px';
            canvas.style.height = height + 'px';
            ctx.setTransform(ratio, 0, 0, ratio, 0, 0);

            if (!points.length) { drawEmpty(width, height); return; }

            ctx.clearRect(0, 0, width, height);

            const pad = { top: 20, right: 20, bottom: 46, left: 64 };
            const chartW = width - pad.left - pad.right;
            const chartH = height - pad.top - pad.bottom;
            const maxMoney = Math.max.apply(null, points.map(function (p) { return Math.max(p.revenue, p.profit); })) || 1;
            const maxEps = Math.max.apply(null, points.map(function (p) { return p.eps; })) || 1;
            const groupW = chartW / points.length;
            const barW = Math.max(8, Math.min(22, groupW * 0.22));

            ctx.strokeStyle = '#e2e8f0';
            ctx.lineWidth = 1;
            ctx.fillStyle = '#94a3b8';
            ctx.font = '11px Inter, sans-serif';
            ctx.textAlign = 'right';

            for (let i = 0; i <= 4; i++) {
                const y = pad.top + chartH - (chartH * i / 4);
                ctx.beginPath();
                ctx.moveTo(pad.left, y);
                ctx.lineTo(width - pad.right, y);
                ctx.stroke();
                ctx.fillText(formatShort(maxMoney * i / 4), pad.left - 10, y + 4);
            }

            const epsLine = [];

            points.forEach(function (p, index) {
                const centerX = pad.left + groupW * index + groupW / 2;
                const revenueH = chartH * (p.revenue / maxMoney);
                const profitH = chartH * (p.profit / maxMoney);
                const epsY = pad.top + chartH - chartH * (p.eps / maxEps);

                ctx.fillStyle = '#2f6fed';
                ctx.fillRect(centerX - barW - 3, pad.top + chartH - revenueH, barW, revenueH);

                ctx.fillStyle = '#10b981';
                ctx.fillRect(centerX + 3, pad.top + chartH - profitH, barW, profitH);

                ctx.fillStyle = '#64748b';
                ctx.textAlign = 'center';
                ctx.save();
                ctx.translate(centerX, height - 24);
                ctx.rotate(-0.45);
                ctx.fillText(p.label, 0, 0);
                ctx.restore();

                epsLine.push({ x: centerX, y: epsY });
            });

            ctx.strokeStyle = '#f59e0b';
            ctx.lineWidth = 2.5;
            ctx.beginPath();
            epsLine.forEach(function (point, index) {
                if (index === 0) ctx.moveTo(point.x, point.y);
                else ctx.lineTo(point.x, point.y);
            });
            ctx.stroke();

            ctx.fillStyle = '#f59e0b';
            epsLine.forEach(function (point) {
                ctx.beginPath();
                ctx.arc(point.x, point.y, 4, 0, Math.PI * 2);
                ctx.fill();
            });
        }

        drawChart();
        window.addEventListener('resize', drawChart);
    })();
</script>

<script>
    (function () {
        const pieCanvas = document.getElementById('financialPieAdmin');
        if (!pieCanvas) return;

        const rawPoints = Array.from(document.querySelectorAll('.report-point-admin')).map(function (node) {
            return {
                year: Number(node.dataset.year),
                quarter: Number(node.dataset.quarter),
                revenue: Number(node.dataset.revenue || 0)
            };
        });

        // Gom theo nam: { 2025: {1: rev, 2: rev, 3: rev, 4: rev}, 2026: {...} }
        const byYear = {};
        rawPoints.forEach(function (p) {
            if (!p.year) return;
            if (!byYear[p.year]) byYear[p.year] = { 1: 0, 2: 0, 3: 0, 4: 0 };
            byYear[p.year][p.quarter] = (byYear[p.year][p.quarter] || 0) + p.revenue;
        });
        const years = Object.keys(byYear).map(Number).sort(function (a, b) { return b - a; });

        const tabsBox = document.getElementById('frYearTabs');
        const legendBox = document.getElementById('frPieLegend');
        const totalEl = document.getElementById('frPieTotal');
        const ctx = pieCanvas.getContext('2d');
        const ratio = window.devicePixelRatio || 1;
        const QUARTER_COLORS = ['#2f6fed', '#10b981', '#f59e0b', '#8b5cf6'];

        function formatMoney(value) {
            return new Intl.NumberFormat('vi-VN').format(Math.round(value));
        }

        function sizeCanvas() {
            const box = pieCanvas.parentElement;
            const size = box.clientWidth;
            pieCanvas.width = size * ratio;
            pieCanvas.height = size * ratio;
            pieCanvas.style.width = size + 'px';
            pieCanvas.style.height = size + 'px';
            ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
            return size;
        }

        function drawEmptyPie(size) {
            ctx.clearRect(0, 0, size, size);
            ctx.strokeStyle = '#e2e8f0';
            ctx.lineWidth = size * 0.16;
            ctx.beginPath();
            ctx.arc(size / 2, size / 2, (size - ctx.lineWidth) / 2, 0, Math.PI * 2);
            ctx.stroke();
        }

        function renderYear(year) {
            const size = sizeCanvas();
            const quarters = byYear[year] || { 1: 0, 2: 0, 3: 0, 4: 0 };
            const total = quarters[1] + quarters[2] + quarters[3] + quarters[4];

            legendBox.innerHTML = '';

            if (!total) {
                drawEmptyPie(size);
                totalEl.textContent = '--';
                const empty = document.createElement('div');
                empty.className = 'fr-pie-empty';
                empty.textContent = 'Chưa có doanh thu nào được công bố cho năm ' + year + '.';
                legendBox.appendChild(empty);
                return;
            }

            totalEl.textContent = formatMoney(total);

            ctx.clearRect(0, 0, size, size);
            const cx = size / 2, cy = size / 2;
            const lineWidth = size * 0.16;
            const radius = (size - lineWidth) / 2;
            let startAngle = -Math.PI / 2;

            [1, 2, 3, 4].forEach(function (q, idx) {
                const value = quarters[q] || 0;
                const slice = total ? (value / total) * Math.PI * 2 : 0;
                if (slice > 0) {
                    ctx.beginPath();
                    ctx.strokeStyle = QUARTER_COLORS[idx];
                    ctx.lineWidth = lineWidth;
                    ctx.lineCap = value === total ? 'butt' : 'round';
                    ctx.arc(cx, cy, radius, startAngle, startAngle + slice);
                    ctx.stroke();
                }
                startAngle += slice;

                const pct = total ? (value / total) * 100 : 0;
                const item = document.createElement('div');
                item.className = 'fr-pie-legend-item';
                item.innerHTML =
                    '<span class="fr-pie-legend-left"><span class="fr-pie-legend-dot" style="background:' + QUARTER_COLORS[idx] + ';"></span>Quý ' + q + '</span>' +
                    '<span><span class="fr-pie-legend-pct">' + pct.toFixed(1) + '%</span> ' +
                    '<span class="fr-pie-legend-value">(' + formatMoney(value) + ')</span></span>';
                legendBox.appendChild(item);
            });
        }

        function selectYear(year) {
            Array.from(tabsBox.children).forEach(function (btn) {
                btn.classList.toggle('active', Number(btn.dataset.year) === year);
            });
            renderYear(year);
        }

        if (!years.length) {
            drawEmptyPie(sizeCanvas());
            const empty = document.createElement('div');
            empty.className = 'fr-pie-empty';
            empty.textContent = 'Chưa có báo cáo tài chính nào trên hệ thống.';
            legendBox.appendChild(empty);
        } else {
            years.forEach(function (year, idx) {
                const btn = document.createElement('button');
                btn.type = 'button';
                btn.className = 'fr-year-tab' + (idx === 0 ? ' active' : '');
                btn.dataset.year = year;
                btn.textContent = year;
                btn.addEventListener('click', function () { selectYear(year); });
                tabsBox.appendChild(btn);
            });
            selectYear(years[0]);
            window.addEventListener('resize', function () {
                const active = tabsBox.querySelector('.fr-year-tab.active');
                if (active) renderYear(Number(active.dataset.year));
            });
        }
    })();
</script>

<script>
    (function () {
        var formCard = document.getElementById('frFormCard');
        var form = document.getElementById('frReportForm');
        var titleEl = document.getElementById('frFormTitle').querySelector('span');
        var cancelLink = document.getElementById('frCancelEdit');
        var submitBtn = document.getElementById('frSubmitBtn');
        var submitLabel = submitBtn.querySelector('span');
        var reportIdInput = document.getElementById('frReportId');
        var currentEditRow = null;

        function fillField(id, value) {
            var el = document.getElementById(id);
            if (el) el.value = (value === undefined || value === 'null') ? '' : value;
        }

        function enterEditMode(btn) {
            fillField('frReportId', btn.dataset.id);
            fillField('frReportYear', btn.dataset.year);
            fillField('frReportQuarter', btn.dataset.quarter);
            fillField('frRevenue', btn.dataset.revenue);
            fillField('frProfitBeforeTax', btn.dataset.pbt);
            fillField('frProfitAfterTax', btn.dataset.pat);
            fillField('frShortTermDebt', btn.dataset.std);
            fillField('frLongTermDebt', btn.dataset.ltd);
            fillField('frEps', btn.dataset.eps);
            fillField('frPe', btn.dataset.pe);
            fillField('frRoe', btn.dataset.roe);
            fillField('frRoa', btn.dataset.roa);

            titleEl.textContent = 'Sửa báo cáo Q' + btn.dataset.quarter + '/' + btn.dataset.year;
            submitLabel.textContent = 'Cập nhật báo cáo';
            cancelLink.style.display = 'inline';
            formCard.classList.add('fr-editing');

            if (currentEditRow) currentEditRow.classList.remove('fr-editing-row');
            currentEditRow = btn.closest('tr');
            currentEditRow.classList.add('fr-editing-row');

            formCard.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }

        function exitEditMode() {
            form.reset();
            reportIdInput.value = '';
            titleEl.textContent = 'Thêm báo cáo quý mới';
            submitLabel.textContent = 'Thêm báo cáo';
            cancelLink.style.display = 'none';
            formCard.classList.remove('fr-editing');
            if (currentEditRow) { currentEditRow.classList.remove('fr-editing-row'); currentEditRow = null; }
        }

        document.querySelectorAll('.btn-edit-row').forEach(function (btn) {
            btn.addEventListener('click', function () { enterEditMode(btn); });
        });

        cancelLink.addEventListener('click', function (e) {
            e.preventDefault();
            exitEditMode();
        });
    })();
</script>

</body>
</html>