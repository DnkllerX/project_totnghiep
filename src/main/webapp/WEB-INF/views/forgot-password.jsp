<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<%@ include file="/WEB-INF/views/common/ga4.jsp" %>
    <meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quên mật khẩu - VinScape</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --accent-yellow: #eab308;
            --primary-purple: rgba(167, 139, 250, 1);
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: 'Roboto', sans-serif;
            color: rgba(243, 244, 246, 1);
            display: flex; align-items: center; justify-content: center; min-height: 100vh;
            -webkit-font-smoothing: antialiased; background-color: #050505;
        }
        .form-container {
            width: 380px;
            border-radius: 0.75rem;
            background-color: rgba(17, 24, 39, 0.95);
            padding: 2.5rem;
            color: rgba(243, 244, 246, 1);
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.7);
            border: 1px solid rgba(55, 65, 81, 0.5);
        }
        .title { text-align: center; font-size: 1.5rem; line-height: 2rem; font-weight: 700; }
        .subtitle { text-align: center; font-size: 0.8rem; color: rgba(156, 163, 175, 1); margin-top: 8px; }
        .form { margin-top: 1.5rem; }
        .input-group { margin-top: 0.75rem; font-size: 0.875rem; line-height: 1.25rem; }
        .input-group label { display: block; color: rgba(156, 163, 175, 1); margin-bottom: 4px; }
        .input-group input {
            width: 100%; border-radius: 0.375rem; border: 1px solid rgba(55, 65, 81, 1);
            outline: 0; background-color: rgba(17, 24, 39, 1); padding: 0.75rem 1rem;
            color: rgba(243, 244, 246, 1); transition: border-color 0.2s;
        }
        .input-group input:focus { border-color: rgba(167, 139, 250, 1); }
        .sign {
            display: block; width: 100%; background-color: rgba(167, 139, 250, 1);
            padding: 0.75rem; text-align: center; color: rgba(17, 24, 39, 1); margin-top: 1.25rem;
            border: none; border-radius: 0.375rem; font-weight: 600; cursor: pointer;
        }
        .sign:hover { opacity: 0.9; }
        .signup { text-align: center; font-size: 0.75rem; line-height: 1rem; color: rgba(156, 163, 175, 1); margin-top: 1.5rem; }
        .signup a { color: rgba(243, 244, 246, 1); text-decoration: none; }
        .signup a:hover { text-decoration: underline rgba(167, 139, 250, 1); }
        .alert { padding: 10px; font-size: 12.5px; margin-bottom: 16px; text-align: center; border-radius: 0.375rem; }
        .alert-error { background: rgba(220,38,38,0.2); color: #fca5a5; border: 1px solid rgba(220,38,38,0.4); }
        .alert-success { background: rgba(16,185,129,0.2); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.4); }
    </style>
</head>
<body>
<div class="form-container">
    <p class="title">Quên mật khẩu</p>
    <p class="subtitle">Nhập email đã đăng ký, chúng tôi sẽ gửi link đặt lại mật khẩu (hiệu lực 30 phút).</p>

    <c:if test="${not empty error}"><div class="alert alert-error"><c:out value="${error}"/></div></c:if>
    <c:if test="${not empty success}"><div class="alert alert-success"><c:out value="${success}"/></div></c:if>

    <form class="form" method="post" action="${pageContext.request.contextPath}/forgot-password" autocomplete="off">
        <div class="input-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" required autofocus autocomplete="off">
        </div>
        <button type="submit" class="sign">Gửi link đặt lại mật khẩu</button>
    </form>

    <p class="signup">
        <a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a>
    </p>
</div>
<jsp:include page="/WEB-INF/views/common/ai-chatbox.jsp" />

</body>
</html>
