<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<%@ include file="/WEB-INF/views/common/ga4.jsp" %>
    <meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Xác nhận hành động - VinScape</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <style>
        :root { --primary-purple: rgba(167, 139, 250, 1); }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: 'Roboto', sans-serif;
            color: rgba(243, 244, 246, 1);
            display: flex; align-items: center; justify-content: center; min-height: 100vh;
            -webkit-font-smoothing: antialiased; background-color: #050505;
        }
        .form-container {
            width: 420px;
            border-radius: 0.75rem;
            background-color: rgba(17, 24, 39, 0.95);
            padding: 2.5rem;
            color: rgba(243, 244, 246, 1);
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.7);
            border: 1px solid rgba(55, 65, 81, 0.5);
        }
        .title { text-align: center; font-size: 1.4rem; line-height: 2rem; font-weight: 700; }
        .desc { font-size: 0.8rem; line-height: 1.5; color: rgba(156, 163, 175, 1); margin-top: 12px; text-align: center; }
        .sign {
            display: block; width: 100%; background-color: rgba(167, 139, 250, 1);
            padding: 0.75rem; text-align: center; color: rgba(17, 24, 39, 1); margin-top: 1.5rem;
            border: none; border-radius: 0.375rem; font-weight: 600; cursor: pointer; font-size: 0.9rem;
        }
        .sign:hover { opacity: 0.9; }
        .signup { text-align: center; font-size: 0.75rem; line-height: 1rem; color: rgba(156, 163, 175, 1); margin-top: 1.5rem; }
        .signup a { color: rgba(243, 244, 246, 1); text-decoration: none; }
        .signup a:hover { text-decoration: underline rgba(167, 139, 250, 1); }
        .alert { padding: 12px; font-size: 12.5px; margin-top: 16px; text-align: center; border-radius: 0.375rem; line-height: 1.5; }
        .alert-error { background: rgba(220,38,38,0.2); color: #fca5a5; border: 1px solid rgba(220,38,38,0.4); }
        .alert-success { background: rgba(22,163,74,0.15); color: #86efac; border: 1px solid rgba(22,163,74,0.35); }
        .warn-box {
            margin-top: 16px; padding: 12px; font-size: 11.5px; line-height: 1.6;
            background: rgba(220,38,38,0.08); border: 1px solid rgba(220,38,38,0.25);
            border-radius: 0.375rem; color: #fca5a5;
        }
    </style>
</head>
<body>
<div class="form-container">
    <p class="title">Xác nhận hành động</p>
    <p class="desc">Có yêu cầu tác động lên tài khoản của bạn từ một tài khoản IT. Chỉ xác nhận nếu chính bạn là người đồng ý với yêu cầu này.</p>

    <c:if test="${not empty error}">
        <div class="alert alert-error"><c:out value="${error}"/></div>
    </c:if>

    <c:if test="${not empty success}">
        <div class="alert alert-success"><c:out value="${success}"/></div>
    </c:if>

    <c:if test="${not empty token and empty success}">
        <form method="post" action="${pageContext.request.contextPath}/confirm-account-action">
            <input type="hidden" name="token" value='<c:out value="${token}"/>'>
            <button type="submit" class="sign">Tôi xác nhận, thực hiện hành động này</button>
        </form>
        <div class="warn-box">
            Nếu bạn KHÔNG phải là người yêu cầu hành động này, hãy đóng trang này ngay, đổi mật khẩu
            của bạn, và báo cấp trên hoặc bộ phận quản trị để kiểm tra tài khoản IT liên quan.
        </div>
    </c:if>

    <p class="signup">
        <a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a>
    </p>
</div>
<jsp:include page="/WEB-INF/views/common/ai-chatbox.jsp" />

</body>
</html>
