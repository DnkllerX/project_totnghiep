<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Dashboard - Co dong</h1>
<div class="grid-2">
    <div class="card">
        <h2>Ky nhan co phan / co tuc</h2>
        <p style="color:#94a3b8; font-size:13px;">Xem va ky nhan cac dot phat hanh/co tuc dang cho ban ky.</p>
        <a class="btn" href="${pageContext.request.contextPath}/app/shareholder/sign">Ky nhan</a>
    </div>
    <div class="card">
        <h2>Bieu quyet</h2>
        <p style="color:#94a3b8; font-size:13px;">Tham gia bieu quyet cac nghi quyet dang mo.</p>
        <a class="btn" href="${pageContext.request.contextPath}/app/shareholder/vote">Bieu quyet</a>
    </div>
    <div class="card">
        <h2>Chuyen nhuong co phan</h2>
        <p style="color:#94a3b8; font-size:13px;">Tao yeu cau chuyen nhuong co phan cho co dong khac.</p>
        <a class="btn" href="${pageContext.request.contextPath}/app/shareholder/transfer-request">Chuyen nhuong</a>
    </div>
    <div class="card">
        <h2>Bao cao tai chinh</h2>
        <p style="color:#94a3b8; font-size:13px;">Xem cac bao cao tai chinh da cong bo.</p>
        <a class="btn" href="${pageContext.request.contextPath}/app/shareholder/financial-reports">Xem bao cao</a>
    </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
