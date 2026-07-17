<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dang ky tai khoan - He thong Quan ly Co dong</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body { display: flex; align-items: center; justify-content: center; min-height: 100vh; }
        .register-box { width: 460px; }
    </style>
</head>
<body>
<div class="card register-box">
    <h1 style="text-align:center;">Dang ky tai khoan co dong</h1>
    <p style="text-align:center; color:#94a3b8; font-size:13px; margin-top:-8px;">
        Tai khoan cua ban se can ADMIN duyet truoc khi su dung duoc.
    </p>

    <c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>
    <c:if test="${not empty success}"><div class="flash-box"><c:out value="${success}"/></div></c:if>

    <form method="post" action="${pageContext.request.contextPath}/register" autocomplete="off">
        <label>Username</label>
        <input type="text" name="username" required autocomplete="off">
        <label>Email</label>
        <input type="email" name="email" required autocomplete="off">
        <label>Mat khau</label>
        <input type="password" id="registerPassword" name="password" required autocomplete="new-password">
        <div style="height:6px; border-radius:3px; background:#334155; margin-top:6px; overflow:hidden;">
            <div id="registerPasswordBar" style="height:100%; width:0%; transition:width .2s, background .2s;"></div>
        </div>
        <div id="registerPasswordLabel" style="font-size:12px; color:#94a3b8; margin-top:4px;"></div>
        <p style="font-size:11px; color:#64748b; margin-top:4px;">
            Chi mang tinh goi y - mat khau yeu van tao tai khoan duoc binh thuong.
        </p>
        <label>Ho ten</label>
        <input type="text" name="fullName" required>
        <label>So CCCD/CMND</label>
        <input type="text" name="citizenId" required>
        <label>So dien thoai</label>
        <input type="text" name="phone">
        <label>Dia chi (khong bat buoc)</label>
        <input type="text" name="address">
        <label>Ngay sinh</label>
        <input type="date" name="birthDate">
        <label>Quoc tich</label>
        <input type="text" name="nationality" value="Viet Nam">
        <button type="submit" style="width:100%;">Dang ky</button>
    </form>
    <p style="text-align:center; margin-top:14px; font-size:13px;">
        Da co tai khoan? <a href="${pageContext.request.contextPath}/login.jsp" style="color:#38bdf8;">Dang nhap</a>
    </p>
</div>
<script src="${pageContext.request.contextPath}/js/password-strength.js"></script>
<script>
    initPasswordStrengthMeter('registerPassword', 'registerPasswordBar', 'registerPasswordLabel');
</script>
</body>
</html>
