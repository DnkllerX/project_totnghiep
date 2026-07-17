<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dang nhap - He thong Quan ly Co dong</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body { display: flex; align-items: center; justify-content: center; min-height: 100vh; }
        .login-box { width: 360px; }
    </style>
</head>
<body>
<div class="card login-box">
    <h1 style="text-align:center;">SnapshotDB</h1>
    <p style="text-align:center; color:#94a3b8; font-size:13px; margin-top:-8px;">
        He thong Quan ly Co dong
    </p>

    <c:if test="${not empty error}">
        <div class="error-box"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login" autocomplete="off">
        <label for="username">Username hoac Email</label>
        <input type="text" id="username" name="username" required autofocus autocomplete="off">

        <label for="password">Mat khau</label>
        <input type="password" id="password" name="password" required autocomplete="off">

        <button type="submit" style="width:100%;">Dang nhap</button>
    </form>
    <p style="text-align:center; margin-top:14px; font-size:13px;">
        Chua co tai khoan? <a href="${pageContext.request.contextPath}/register.jsp" style="color:#38bdf8;">Dang ky co dong</a>
    </p>
</div>
</body>
</html>
