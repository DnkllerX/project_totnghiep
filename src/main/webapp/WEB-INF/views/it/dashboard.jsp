<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Dashboard - IT</h1>
<div class="card">
    <h2>Quan ly tai khoan</h2>
    <p style="color:#94a3b8; font-size:13px;">
        Tao tai khoan moi, khoa/mo khoa, reset mat khau. IT khong co quyen dieu chinh co phan,
        tao phat hanh, bieu quyet hay xem bao cao tai chinh.
    </p>
    <a class="btn" href="${pageContext.request.contextPath}/app/admin/user-management">Quan ly tai khoan</a>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
