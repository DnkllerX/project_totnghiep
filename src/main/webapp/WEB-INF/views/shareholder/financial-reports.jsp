<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<c:forEach var="r" items="${reports}" varStatus="status">
    <c:if test="${status.first}">
        <c:set var="latestReport" value="${r}" />
    </c:if>
    <c:if test="${status.index == 1}">
        <c:set var="prevReport" value="${r}" />
    </c:if>
</c:forEach>

<style>
    :root {
        --card-bg: #ffffff; --text-main: #0f172a; --text-muted: #64748b;
        --primary: #2f6fed; --primary-hover: #2563eb; --border-color: #e2e8f0; --info-bg: #eff6ff;
    }

    .financial-hero {
        background: #ffffff;
        border: 1px solid var(--border-color);
        border-radius: 12px;
        padding: 28px 30px;
        margin-bottom: 24px;
        display: grid;
        grid-template-columns: minmax(0, 1.5fr) minmax(260px, 0.8fr);
        gap: 24px;
        align-items: center;
        box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }

    .financial-eyebrow {
        color: var(--primary);
        font-size: 12px;
        font-weight: 700;
        letter-spacing: 1px;
        text-transform: uppercase;
        margin-bottom: 10px;
    }

    .financial-hero h1 {
        margin: 0 0 12px 0;
        color: var(--text-main);
        font-size: 26px;
        line-height: 1.2;
    }

    .financial-hero p {
        margin: 0;
        color: var(--text-muted);
        font-size: 13.5px;
        line-height: 1.7;
        max-width: 760px;
    }

    .latest-box {
        background: var(--info-bg);
        border: 1px solid #dbeafe;
        border-radius: 8px;
        padding: 18px;
    }

    .latest-label { color: var(--text-muted); font-size: 12px; margin-bottom: 8px; }
    .latest-period { color: var(--primary); font-size: 22px; font-weight: 800; margin-bottom: 14px; }

    .latest-meta { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; }
    .latest-meta span { color: var(--text-muted); display: block; font-size: 11px; margin-bottom: 3px; }
    .latest-meta strong { color: var(--text-main); font-size: 13px; }

    .metric-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 16px; margin-bottom: 24px; }

    .metric-card, .financial-card {
        background: #ffffff;
        border: 1px solid var(--border-color);
        border-radius: 10px;
        box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }

    .metric-card { padding: 18px; }
    .metric-card span { display: block; color: var(--text-muted); font-size: 12px; margin-bottom: 8px; }
    .metric-card strong { color: var(--text-main); font-size: 19px; font-weight: 800; }
    .metric-card small { display: block; color: #94a3b8; font-size: 11px; margin-top: 7px; }
    .metric-card strong.mc-up { color: #16a34a; }
    .metric-card strong.mc-down { color: #dc2626; }
    .mc-delta { display: inline-flex; align-items: center; gap: 3px; font-size: 11.5px; font-weight: 700; margin-left: 8px; vertical-align: middle; }
    .mc-delta.mc-up { color: #16a34a; }
    .mc-delta.mc-down { color: #dc2626; }
    .mc-delta svg { width: 12px; height: 12px; }

    /* --- PIE CHART: co cau doanh thu theo quy, chon nam dong (dong bo voi trang admin) --- */
    .fr-pie-layout { display: grid; grid-template-columns: minmax(0, 240px) 1fr; gap: 28px; align-items: center; padding: 22px; }
    @media (max-width: 760px) { .fr-pie-layout { grid-template-columns: 1fr; } }
    .fr-year-tabs { display: flex; flex-wrap: wrap; gap: 8px; margin: 0 22px 4px; }
    .fr-year-tab {
        border: 1px solid var(--border-color); background: #f8fafc; color: var(--text-muted);
        font-size: 12.5px; font-weight: 600; padding: 6px 14px; border-radius: 999px;
        cursor: pointer; transition: all .15s;
    }
    .fr-year-tab:hover { border-color: var(--primary); color: var(--primary); }
    .fr-year-tab.active { background: var(--primary); border-color: var(--primary); color: #fff; }
    .fr-pie-canvas-box { position: relative; width: 200px; height: 200px; margin: 0 auto; }
    #financialPieShareholder { width: 100%; height: 100%; display: block; }
    .fr-pie-center { position: absolute; inset: 0; display: flex; flex-direction: column; align-items: center; justify-content: center; pointer-events: none; }
    .fr-pie-center .fr-pie-total { font-size: 16px; font-weight: 800; color: var(--text-main); }
    .fr-pie-center .fr-pie-total-label { font-size: 10px; color: var(--text-muted); margin-top: 2px; text-align: center; }
    .fr-pie-legend { display: flex; flex-direction: column; gap: 12px; }
    .fr-pie-legend-item { display: flex; align-items: center; justify-content: space-between; gap: 12px; font-size: 13px; }
    .fr-pie-legend-left { display: flex; align-items: center; gap: 9px; color: var(--text-main); font-weight: 600; }
    .fr-pie-legend-dot { width: 11px; height: 11px; border-radius: 3px; flex-shrink: 0; }
    .fr-pie-legend-pct { font-weight: 800; color: var(--text-main); }
    .fr-pie-legend-value { color: var(--text-muted); font-size: 11.5px; }
    .fr-pie-empty { color: var(--text-muted); font-size: 13px; font-style: italic; text-align: center; padding: 20px; }

    .financial-card { margin-bottom: 24px; overflow: hidden; }
    .financial-card-header {
        padding: 18px 22px; border-bottom: 1px solid var(--border-color);
        display: flex; justify-content: space-between; gap: 16px; align-items: center;
    }
    .financial-card-header h2 {
        margin: 0; color: var(--text-main); font-size: 16px; font-weight: 700;
        display: flex; align-items: center; gap: 8px;
    }
    .financial-card-header h2::before {
        content: ""; width: 4px; height: 16px; border-radius: 4px;
        background: var(--primary); display: inline-block;
    }
    .financial-note { color: var(--text-muted); font-size: 12px; }

    .chart-wrap { padding: 22px; }
    .chart-canvas-box { height: 340px; position: relative; }
    #financialChart { width: 100%; height: 100%; display: block; }

    .chart-legend { display: flex; flex-wrap: wrap; gap: 14px; margin-top: 16px; color: var(--text-muted); font-size: 12px; }
    .legend-item { display: inline-flex; align-items: center; gap: 7px; }
    .legend-dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; }

    .table-wrapper { width: 100%; overflow-x: auto; padding: 0 22px 22px; box-sizing: border-box; }
    .financial-table { width: 100%; border-collapse: collapse; min-width: 1120px; }
    .financial-table th {
        background: #f1f5f9; color: var(--text-muted); font-size: 11px; letter-spacing: 0.5px;
        text-transform: uppercase; text-align: left; padding: 13px 12px; border-bottom: 1px solid var(--border-color);
    }
    .financial-table td {
        color: var(--text-main); font-size: 13px; padding: 14px 12px;
        border-bottom: 1px solid var(--border-color); white-space: nowrap;
    }
    .financial-table tr:hover td { background: #f8fafc; }

    .period-badge {
        color: var(--primary); background: var(--info-bg); border: 1px solid #dbeafe;
        padding: 4px 8px; border-radius: 6px; font-weight: 700;
    }

    .money { color: #10b981; font-weight: 600; }
    .ratio { color: var(--text-main); font-weight: 600; }

    .empty-state { text-align: center; color: var(--text-muted) !important; padding: 34px 12px !important; }
    .chart-data { display: none; }

    @media (max-width: 1100px) {
        .financial-hero { grid-template-columns: 1fr; }
        .metric-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
    }
    @media (max-width: 700px) {
        .financial-hero { padding: 22px; }
        .financial-hero h1 { font-size: 22px; }
        .metric-grid { grid-template-columns: 1fr; }
    }
</style>

<section class="financial-hero">
    <div>
        <div class="financial-eyebrow">Báo cáo tài chính</div>
        <h1>Tổng quan báo cáo tài chính</h1>
        <p>
            Theo dõi các chỉ số tài chính được công bố theo từng quý: doanh thu, lợi nhuận,
            nợ, EPS, P/E, ROE và ROA. Toàn bộ dữ liệu hiển thị lấy trực tiếp từ các báo cáo
            đã được công ty công bố.
        </p>
    </div>

    <div class="latest-box">
        <c:choose>
            <c:when test="${not empty latestReport}">
                <div class="latest-label">Kỳ báo cáo mới nhất</div>
                <div class="latest-period">Q<c:out value="${latestReport.reportQuarter}"/>/<c:out value="${latestReport.reportYear}"/></div>
                <div class="latest-meta">
                    <div>
                        <span>Doanh thu</span>
                        <strong><fmt:formatNumber value="${latestReport.revenue}" type="number" maxFractionDigits="0"/></strong>
                    </div>
                    <div>
                        <span>LNST</span>
                        <strong><fmt:formatNumber value="${latestReport.profitAfterTax}" type="number" maxFractionDigits="0"/></strong>
                    </div>
                    <div>
                        <span>EPS</span>
                        <strong><fmt:formatNumber value="${latestReport.eps}" type="number" maxFractionDigits="0"/></strong>
                    </div>
                    <div>
                        <span>P/E</span>
                        <strong><fmt:formatNumber value="${latestReport.pe}" type="number" maxFractionDigits="2"/></strong>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="latest-label">Kỳ báo cáo mới nhất</div>
                <div class="latest-period">Chưa có dữ liệu</div>
                <div class="financial-note">Admin cần thêm báo cáo để bạn xem tại đây.</div>
            </c:otherwise>
        </c:choose>
    </div>
</section>

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
                    <c:otherwise><strong><fmt:formatNumber value="${latestReport.revenue}" type="number" maxFractionDigits="0"/></strong></c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise><strong><fmt:formatNumber value="${latestReport.revenue}" type="number" maxFractionDigits="0"/></strong></c:otherwise>
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
                    <c:otherwise><strong><fmt:formatNumber value="${latestReport.profitAfterTax}" type="number" maxFractionDigits="0"/></strong></c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise><strong><fmt:formatNumber value="${latestReport.profitAfterTax}" type="number" maxFractionDigits="0"/></strong></c:otherwise>
        </c:choose>
        <small>Kỳ mới nhất</small>
    </div>
    <div class="metric-card">
        <span>ROE / ROA</span>
        <strong><fmt:formatNumber value="${latestReport.roe}" type="number" maxFractionDigits="2"/>% / <fmt:formatNumber value="${latestReport.roa}" type="number" maxFractionDigits="2"/>%</strong>
        <small>Hiệu quả sinh lời</small>
    </div>
    <div class="metric-card">
        <span>Nợ ngắn hạn / dài hạn</span>
        <strong><fmt:formatNumber value="${latestReport.shortTermDebt}" type="number" maxFractionDigits="0"/> / <fmt:formatNumber value="${latestReport.longTermDebt}" type="number" maxFractionDigits="0"/></strong>
        <small>Cơ cấu nợ</small>
    </div>
</div>

<section class="financial-card">
    <div class="financial-card-header">
        <h2>Biểu đồ doanh thu và lợi nhuận</h2>
        <span class="financial-note">Đường EPS dùng trục tham chiếu riêng trong cùng vùng biểu đồ</span>
    </div>
    <div class="chart-wrap">
        <div class="chart-canvas-box">
            <canvas id="financialChart" aria-label="Biểu đồ tài chính" role="img"></canvas>
        </div>
        <div class="chart-legend">
            <span class="legend-item"><i class="legend-dot" style="background:#2f6fed;"></i>Doanh thu</span>
            <span class="legend-item"><i class="legend-dot" style="background:#10b981;"></i>Lợi nhuận sau thuế</span>
            <span class="legend-item"><i class="legend-dot" style="background:#f59e0b;"></i>EPS</span>
        </div>
    </div>
</section>

<section class="financial-card">
    <div class="financial-card-header">
        <h2>Cơ cấu doanh thu theo quý</h2>
        <span class="financial-note">Tỷ trọng 4 quý trong năm, quy về tổng 100%</span>
    </div>
    <div class="fr-year-tabs" id="frYearTabsShareholder"></div>
    <div class="fr-pie-layout">
        <div class="fr-pie-canvas-box">
            <canvas id="financialPieShareholder" aria-label="Biểu đồ tỷ trọng doanh thu theo quý" role="img"></canvas>
            <div class="fr-pie-center">
                <div class="fr-pie-total" id="frPieTotalShareholder">--</div>
                <div class="fr-pie-total-label">Tổng doanh thu năm</div>
            </div>
        </div>
        <div class="fr-pie-legend" id="frPieLegendShareholder"></div>
    </div>
</section>

<section class="financial-card">
    <div class="financial-card-header">
        <h2>Bảng số liệu tài chính</h2>
        <span class="financial-note">Đầy đủ các trường trong bảng SQL hiện tại</span>
    </div>
    <div class="table-wrapper">
        <table class="financial-table">
            <thead>
                <tr>
                    <th>Kỳ</th>
                    <th>Doanh thu</th>
                    <th>LN trước thuế</th>
                    <th>LN sau thuế</th>
                    <th>Nợ ngắn hạn</th>
                    <th>Nợ dài hạn</th>
                    <th>EPS</th>
                    <th>P/E</th>
                    <th>ROE</th>
                    <th>ROA</th>
                    <th>Ngày tạo</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${reports}">
                    <tr>
                        <td><span class="period-badge">Q<c:out value="${r.reportQuarter}"/>/<c:out value="${r.reportYear}"/></span></td>
                        <td class="money"><fmt:formatNumber value="${r.revenue}" type="number" maxFractionDigits="0"/></td>
                        <td class="money"><fmt:formatNumber value="${r.profitBeforeTax}" type="number" maxFractionDigits="0"/></td>
                        <td class="money"><fmt:formatNumber value="${r.profitAfterTax}" type="number" maxFractionDigits="0"/></td>
                        <td><fmt:formatNumber value="${r.shortTermDebt}" type="number" maxFractionDigits="0"/></td>
                        <td><fmt:formatNumber value="${r.longTermDebt}" type="number" maxFractionDigits="0"/></td>
                        <td class="ratio"><fmt:formatNumber value="${r.eps}" type="number" maxFractionDigits="0"/></td>
                        <td class="ratio"><fmt:formatNumber value="${r.pe}" type="number" maxFractionDigits="2"/></td>
                        <td class="ratio"><fmt:formatNumber value="${r.roe}" type="number" maxFractionDigits="2"/>%</td>
                        <td class="ratio"><fmt:formatNumber value="${r.roa}" type="number" maxFractionDigits="2"/>%</td>
                        <td><c:out value="${r.createdAt}"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty reports}">
                    <tr>
                        <td colspan="11" class="empty-state">Chưa có báo cáo nào được công bố.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</section>

<div class="chart-data" id="chartData">
    <c:forEach var="r" items="${reports}">
        <span class="report-point"
              data-label="Q${r.reportQuarter}/${r.reportYear}"
              data-year="${r.reportYear}"
              data-quarter="${r.reportQuarter}"
              data-revenue="${empty r.revenue ? 0 : r.revenue}"
              data-profit="${empty r.profitAfterTax ? 0 : r.profitAfterTax}"
              data-eps="${empty r.eps ? 0 : r.eps}"></span>
    </c:forEach>
</div>

<script>
    (function () {
        const canvas = document.getElementById('financialChart');
        if (!canvas) return;

        const points = Array.from(document.querySelectorAll('.report-point')).map(function (node) {
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

            if (!points.length) {
                drawEmpty(width, height);
                return;
            }

            ctx.clearRect(0, 0, width, height);

            const pad = { top: 24, right: 26, bottom: 54, left: 70 };
            const chartW = width - pad.left - pad.right;
            const chartH = height - pad.top - pad.bottom;
            const maxMoney = Math.max.apply(null, points.map(function (p) {
                return Math.max(p.revenue, p.profit);
            })) || 1;
            const maxEps = Math.max.apply(null, points.map(function (p) { return p.eps; })) || 1;
            const groupW = chartW / points.length;
            const barW = Math.max(8, Math.min(24, groupW * 0.22));

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
                ctx.translate(centerX, height - 28);
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

            ctx.fillStyle = '#64748b';
            ctx.font = '11px Inter, sans-serif';
            ctx.textAlign = 'left';
            ctx.fillText('EPS max: ' + formatShort(maxEps), pad.left, 14);
        }

        drawChart();
        window.addEventListener('resize', drawChart);
    })();
</script>

<script>
    (function () {
        const pieCanvas = document.getElementById('financialPieShareholder');
        if (!pieCanvas) return;

        const rawPoints = Array.from(document.querySelectorAll('.report-point')).map(function (node) {
            return {
                year: Number(node.dataset.year),
                quarter: Number(node.dataset.quarter),
                revenue: Number(node.dataset.revenue || 0)
            };
        });

        const byYear = {};
        rawPoints.forEach(function (p) {
            if (!p.year) return;
            if (!byYear[p.year]) byYear[p.year] = { 1: 0, 2: 0, 3: 0, 4: 0 };
            byYear[p.year][p.quarter] = (byYear[p.year][p.quarter] || 0) + p.revenue;
        });
        const years = Object.keys(byYear).map(Number).sort(function (a, b) { return b - a; });

        const tabsBox = document.getElementById('frYearTabsShareholder');
        const legendBox = document.getElementById('frPieLegendShareholder');
        const totalEl = document.getElementById('frPieTotalShareholder');
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

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
