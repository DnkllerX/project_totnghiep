<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Dashboard - ADMIN</h1>
<div class="grid-2">
    <div class="card">
        <h2>Quan ly co dong</h2>
        <p style="color:#94a3b8; font-size:13px;">Them moi, xem danh sach co dong va so du co phan.</p>
        <a class="btn" href="${pageContext.request.contextPath}/app/admin/shareholders">Xem danh sach</a>
    </div>
    <div class="card">
        <h2>Dieu chinh co phan</h2>
        <p style="color:#94a3b8; font-size:13px;">Dieu chinh thu cong so co phan cua tung co dong.</p>
        <a class="btn" href="${pageContext.request.contextPath}/app/admin/share-adjust">Dieu chinh</a>
    </div>
    <div class="card">
        <h2>Duyet chuyen nhuong</h2>
        <p style="color:#94a3b8; font-size:13px;">Xu ly cac yeu cau chuyen nhuong co phan dang cho.</p>
        <a class="btn" href="${pageContext.request.contextPath}/app/admin/transfer-approval">Xem yeu cau</a>
    </div>
    <div class="card">
        <h2>Phat hanh / Co tuc</h2>
        <p style="color:#94a3b8; font-size:13px;">Tao dot phat hanh co phan hoac co tuc moi.</p>
        <a class="btn" href="${pageContext.request.contextPath}/app/admin/share-issue">Tao dot phat hanh</a>
    </div>
    <div class="card">
        <h2>Nghi quyet</h2>
        <p style="color:#94a3b8; font-size:13px;">Tao nghi quyet va theo doi ket qua bieu quyet.</p>
        <a class="btn" href="${pageContext.request.contextPath}/app/admin/resolution">Xem nghi quyet</a>
    </div>
    <div class="card">
        <h2>Tai lieu & Bao cao</h2>
        <p style="color:#94a3b8; font-size:13px;">Upload tai lieu va quan ly bao cao tai chinh.</p>
        <a class="btn" href="${pageContext.request.contextPath}/app/admin/documents">Tai lieu</a>
        <a class="btn secondary" href="${pageContext.request.contextPath}/app/admin/financial-reports/manage">Bao cao TC</a>
    </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
