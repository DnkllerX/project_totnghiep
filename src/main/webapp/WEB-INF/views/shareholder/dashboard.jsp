<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<style>
    :root {
        --card-bg: #ffffff; --text-main: #0f172a; --text-muted: #64748b;
        --primary: #2f6fed; --primary-hover: #2563eb; --border-color: #e2e8f0; --info-bg: #eff6ff;
    }

    .vg-hero {
        background: linear-gradient(135deg, #0f172a, #1e293b);
        border-radius: 12px; padding: 40px 32px; text-align: center; margin-bottom: 28px;
        box-shadow: 0 6px 18px rgba(15,23,42,0.15);
    }
    .vg-hero h1 {
        color: #ffffff; font-size: 26px; font-weight: 700; margin: 0 0 12px 0;
        letter-spacing: .3px;
    }
    .vg-hero p { color: #cbd5e1; font-size: 14px; margin: 0 auto; max-width: 720px; line-height: 1.65; }

    .vg-modules { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }
    @media (max-width: 800px) { .vg-modules { grid-template-columns: 1fr; } }

    .vg-card {
        background: var(--card-bg); border-radius: 12px; overflow: hidden;
        border: 1px solid var(--border-color); display: flex; flex-direction: column;
        box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }
    .vg-card-header { padding: 18px 24px; border-bottom: 1px solid var(--border-color); background: #f8fafc; }
    .vg-card-header h2 {
        margin: 0; color: var(--text-main); font-size: 16px; font-weight: 700;
        display: flex; align-items: center; gap: 8px;
    }
    .vg-card-header h2::before {
        content: ""; display: block; width: 4px; height: 16px;
        background: var(--primary); border-radius: 4px;
    }
    .vg-card-body { padding: 24px; flex: 1; display: flex; flex-direction: column; }
    .vg-card-body p { color: var(--text-muted); font-size: 13.5px; line-height: 1.6; margin: 0 0 20px 0; flex: 1; }

    .vg-btn {
        display: inline-block; text-align: center; padding: 10px 16px; border-radius: 6px;
        font-size: 13.5px; font-weight: 600; text-decoration: none; transition: all .2s;
        background: var(--primary); color: #fff;
    }
    .vg-btn:hover { background: var(--primary-hover); transform: translateY(-1px); }
</style>

<div class="vg-hero">
    <h1>Cổng Thông Tin Cổ Đông</h1>
    <p>Theo dõi tài sản cổ phần, tham gia biểu quyết và quản lý các giao dịch chuyển nhượng trực tuyến
       một cách an toàn và minh bạch.</p>
</div>

<div class="vg-modules">
    <div class="vg-card">
        <div class="vg-card-header"><h2>Ký nhận Cổ phần / Cổ tức</h2></div>
        <div class="vg-card-body">
            <p>Xem chi tiết và xác nhận ký nhận các đợt phát hành cổ phần hoặc chi trả cổ tức mới nhất
               đang chờ bạn xử lý.</p>
            <a href="${pageContext.request.contextPath}/app/shareholder/sign" class="vg-btn">Thực hiện Ký nhận</a>
        </div>
    </div>

    <div class="vg-card">
        <div class="vg-card-header"><h2>Biểu quyết Đại hội đồng</h2></div>
        <div class="vg-card-body">
            <p>Thực hiện quyền cổ đông bằng cách tham gia bỏ phiếu cho các nghị quyết đang trong thời
               gian mở biểu quyết.</p>
            <a href="${pageContext.request.contextPath}/app/shareholder/vote" class="vg-btn">Vào phòng Biểu quyết</a>
        </div>
    </div>

    <div class="vg-card">
        <div class="vg-card-header"><h2>Chuyển nhượng Cổ phần</h2></div>
        <div class="vg-card-body">
            <p>Tạo yêu cầu chuyển nhượng số lượng cổ phần đang sở hữu cho các cổ đông khác trong cùng
               hệ thống.</p>
            <a href="${pageContext.request.contextPath}/app/shareholder/transfer-request" class="vg-btn">Tạo Yêu cầu Giao dịch</a>
        </div>
    </div>

    <div class="vg-card">
        <div class="vg-card-header"><h2>Báo cáo Tài chính &amp; Tài liệu</h2></div>
        <div class="vg-card-body">
            <p>Tra cứu và tải xuống các báo cáo tài chính minh bạch và tài liệu hệ thống đã được ban
               quản trị công bố.</p>
            <a href="${pageContext.request.contextPath}/app/shareholder/financial-reports" class="vg-btn">Xem Báo cáo TC</a>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
