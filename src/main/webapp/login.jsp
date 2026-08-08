<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<%@ include file="/WEB-INF/views/common/ga4.jsp" %>
    <meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng nhập - VinScape</title>
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
            display: flex; flex-direction: column; min-height: 100vh;
            -webkit-font-smoothing: antialiased; background-color: #000;
        }

        /* --- BẦU TRỜI SAO RƠI --- */
        .uiverse-midnight-sky {
            margin: 0; padding: 0; box-sizing: border-box;
            width: 100%; height: 100%; min-height: 500px;
            overflow: hidden; background-color: #050505;
            position: fixed; top: 0; left: 0; z-index: -1;
            display: flex; align-items: center; justify-content: center;
        }
        .uiverse-midnight-sky .sky-canvas { width: 100%; height: 100%; position: absolute; inset: 0; background: #050505; }
        .uiverse-midnight-sky .stars { position: absolute; inset: 0; background-repeat: repeat; pointer-events: none; }
        .uiverse-midnight-sky .stars-1 { background-image: radial-gradient(1px 1px at 10% 10%, #fff, transparent), radial-gradient(1px 1px at 30% 20%, #fff, transparent), radial-gradient(1px 1px at 50% 50%, #fff, transparent), radial-gradient(1px 1px at 70% 30%, #fff, transparent), radial-gradient(1px 1px at 90% 10%, #fff, transparent); background-size: 80px 80px; animation: twinkle 3s ease-in-out infinite; }
        .uiverse-midnight-sky .stars-2 { background-image: radial-gradient(1.5px 1.5px at 20% 40%, #fff, transparent), radial-gradient(1.5px 1.5px at 60% 85%, #fff, transparent), radial-gradient(1.5px 1.5px at 85% 65%, #fff, transparent); background-size: 120px 120px; animation: twinkle 5s ease-in-out infinite 1s; }
        .uiverse-midnight-sky .stars-3 { background-image: radial-gradient(2px 2px at 40% 70%, #fff, transparent), radial-gradient(2px 2px at 10% 80%, #fff, transparent), radial-gradient(2px 2px at 80% 40%, #fff, transparent); background-size: 160px 160px; animation: twinkle 7s ease-in-out infinite 2s; }
        .uiverse-midnight-sky .meteor { position: absolute; width: 2px; height: 2px; background: #fff; border-radius: 50%; box-shadow: 0 0 10px 2px rgba(255, 255, 255, 0.5); opacity: 0; pointer-events: none; }
        .uiverse-midnight-sky .meteor::after { content: ""; position: absolute; top: 50%; transform: translateY(-50%); width: 80px; height: 1px; background: linear-gradient(90deg, #fff, transparent); }
        .uiverse-midnight-sky .m1 { top: 10%; left: 110%; animation: shoot 8s linear infinite; }
        .uiverse-midnight-sky .m2 { top: 30%; left: 110%; animation: shoot 12s linear infinite 4s; }
        .uiverse-midnight-sky .m3 { top: 50%; left: 110%; animation: shoot 10s linear infinite 2s; }
        .uiverse-midnight-sky .m4 { top: 20%; left: 110%; animation: shoot 9s linear infinite 1s; }
        .uiverse-midnight-sky .m5 { top: 60%; left: 110%; animation: shoot 11s linear infinite 5s; }
        .uiverse-midnight-sky .m6 { top: 80%; left: 110%; animation: shoot 13s linear infinite 7s; }
        .uiverse-midnight-sky .moon { position: absolute; top: 15%; right: 15%; width: 80px; height: 80px; border-radius: 50%; background: transparent; box-shadow: 15px 15px 0 0 #fdfbd3; filter: drop-shadow(0 0 15px rgba(253, 251, 211, 0.4)); z-index: 10; }
        .uiverse-midnight-sky .aurora { position: absolute; border-radius: 50%; filter: blur(90px); opacity: 0.4; pointer-events: none; mix-blend-mode: screen; animation: aurora-drift 18s ease-in-out infinite; }
        .uiverse-midnight-sky .aurora-1 { width: 460px; height: 460px; background: radial-gradient(circle, rgba(167, 139, 250, 0.55), transparent 70%); top: -12%; left: 8%; }
        .uiverse-midnight-sky .aurora-2 { width: 400px; height: 400px; background: radial-gradient(circle, rgba(234, 179, 8, 0.32), transparent 70%); bottom: -16%; right: 4%; animation-delay: -9s; animation-direction: reverse; }
        @keyframes twinkle { 0%, 100% { opacity: 1; } 50% { opacity: 0.2; } }
        @keyframes shoot { 0% { transform: translateX(0) translateY(0) rotate(-35deg); opacity: 0; } 5% { opacity: 1; } 15% { transform: translateX(-1500px) translateY(1000px) rotate(-35deg); opacity: 0; } 100% { transform: translateX(-1500px) translateY(1000px) rotate(-35deg); opacity: 0; } }
        @keyframes aurora-drift { 0%, 100% { transform: translate(0, 0) scale(1); } 50% { transform: translate(40px, -30px) scale(1.15); } }

        /* --- HEADER --- */
        header {
            display: flex; justify-content: space-between; align-items: center;
            padding: 0 40px; height: 60px; background: #000; 
            border-bottom: 2px solid var(--accent-yellow); 
        }
        .header-logo { font-size: 16px; font-weight: 500; letter-spacing: 0.5px; color: #fff; }
        .header-logo-link { display: flex; align-items: center; text-decoration: none; color: inherit; transition: opacity .2s; }
        .header-logo-link:hover { opacity: .82; }
        .header-logo img { animation: logo-glow 4s ease-in-out infinite; }
        @keyframes logo-glow { 0%, 100% { box-shadow: 0 0 6px rgba(167,139,250,0.15); } 50% { box-shadow: 0 0 14px rgba(167,139,250,0.5); } }
        .header-right { display: flex; align-items: center; gap: 16px; }
        .header-exit {
            display: inline-flex; align-items: center; gap: 6px;
            font-size: 12.5px; font-weight: 500; color: #aaa; text-decoration: none;
            padding: 6px 12px; border-radius: 999px; border: 1px solid rgba(255,255,255,0.12);
            transition: color .2s, border-color .2s, background .2s;
        }
        .header-exit svg { width: 14px; height: 14px; stroke: currentColor; }
        .header-exit:hover { color: #fff; border-color: var(--primary-purple); background: rgba(167,139,250,0.1); }
        .header-icon svg { width: 20px; height: 20px; stroke: #aaa; fill: none; stroke-width: 1.5; cursor: pointer; transition: stroke 0.3s; }
        .header-icon svg:hover { stroke: #fff; }

        @media (prefers-reduced-motion: reduce) {
            .uiverse-midnight-sky .aurora, .uiverse-midnight-sky .stars, .uiverse-midnight-sky .meteor,
            .form-container, .form-container::before, .header-logo img, .sign::after {
                animation: none !important;
            }
        }

        /* --- UIVERSE CSS STYLES CHO LOGIN --- */
        main {
            flex: 1; display: flex; align-items: center; justify-content: center;
            padding: 40px 20px; position: relative; z-index: 20;
        }
        .form-container {
            width: 380px;
            border-radius: 0.75rem;
            background-color: rgba(17, 24, 39, 0.95);
            backdrop-filter: blur(10px);
            padding: 2.5rem;
            color: rgba(243, 244, 246, 1);
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.7);
            border: 1px solid rgba(55, 65, 81, 0.5);
            position: relative;
            overflow: hidden;
            animation: card-in 0.6s cubic-bezier(.16,1,.3,1);
        }
        .form-container::before {
            content: ""; position: absolute; top: 0; left: 0; right: 0; height: 3px;
            background: linear-gradient(90deg, var(--accent-yellow), var(--primary-purple) 55%, var(--accent-yellow));
            background-size: 200% 100%;
            animation: bar-sheen 6s linear infinite;
        }
        @keyframes card-in { from { opacity: 0; transform: translateY(26px) scale(0.97); } to { opacity: 1; transform: translateY(0) scale(1); } }
        @keyframes bar-sheen { 0% { background-position: 0% 0; } 100% { background-position: 200% 0; } }
        .title {
            text-align: center; font-size: 1.5rem; line-height: 2rem; font-weight: 700;
        }
        .form { margin-top: 1.5rem; }
        .input-group { margin-top: 0.75rem; font-size: 0.875rem; line-height: 1.25rem; }
        .input-group label { display: block; color: rgba(156, 163, 175, 1); margin-bottom: 4px; }
        .input-group input {
            width: 100%; border-radius: 0.375rem; border: 1px solid rgba(55, 65, 81, 1);
            outline: 0; background-color: rgba(17, 24, 39, 1); padding: 0.75rem 1rem;
            color: rgba(243, 244, 246, 1); transition: border-color 0.2s, box-shadow 0.2s;
        }
        .input-group input:focus { border-color: rgba(167, 139, 250, 1); box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.18), 0 0 16px rgba(167, 139, 250, 0.22); }
        .forgot {
            display: flex; justify-content: flex-end; font-size: 0.75rem; line-height: 1rem;
            color: rgba(156, 163, 175, 1); margin: 8px 0 16px 0;
        }
        .forgot a, .signup a { color: rgba(243, 244, 246, 1); text-decoration: none; font-size: 13px; }
        .forgot a:hover, .signup a:hover { text-decoration: underline rgba(167, 139, 250, 1); }
        .sign {
            display: block; width: 100%; background-color: rgba(167, 139, 250, 1);
            padding: 0.75rem; text-align: center; color: rgba(17, 24, 39, 1);
            border: none; border-radius: 0.375rem; font-weight: 600; cursor: pointer;
            transition: opacity 0.2s, box-shadow 0.2s, transform 0.15s;
            position: relative; overflow: hidden;
        }
        .sign::after {
            content: ""; position: absolute; top: 0; left: -60%; width: 50%; height: 100%;
            background: linear-gradient(120deg, transparent, rgba(255,255,255,0.45), transparent);
            transform: skewX(-20deg); transition: left 0.55s ease;
        }
        .sign:hover { opacity: 0.95; box-shadow: 0 8px 22px rgba(167, 139, 250, 0.35); transform: translateY(-1px); }
        .sign:hover::after { left: 130%; }
        .sign:active { transform: translateY(0); }
        .signup { text-align: center; font-size: 0.75rem; line-height: 1rem; color: rgba(156, 163, 175, 1); margin-top: 1.5rem; }

        .alert { padding: 10px; font-size: 12.5px; margin-bottom: 16px; text-align: center; border-radius: 0.375rem; }
        .alert-error { background: rgba(220,38,38,0.2); color: #fca5a5; border: 1px solid rgba(220,38,38,0.4); }
        .alert-success { background: rgba(16,185,129,0.2); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.4); }

        /* --- FOOTER --- */
        footer { background: #000; padding: 24px 40px; border-top: 1px solid #111; font-size: 11px; color: rgba(156, 163, 175, 1); position: relative; z-index: 10; }
        .footer-top { display: flex; justify-content: center; align-items: center; border-bottom: 1px solid #222; padding-bottom: 20px; margin-bottom: 20px; }
        .slogan { color: var(--accent-yellow); font-weight: 500; letter-spacing: 1px; text-transform: uppercase; font-size: 12px; }
        .footer-bottom { display: flex; justify-content: space-between; align-items: center; }
        .footer-links { display: flex; gap: 16px; }
        .footer-links a { color: rgba(156, 163, 175, 1); text-decoration: none; transition: color 0.3s; }
        .footer-links a:hover { color: #fff; }
        .footer-links span { color: #333; }

        @media (max-width: 768px) {
            header, footer { padding: 16px 20px; }
            .footer-bottom { flex-direction: column; gap: 16px; text-align: center; }
        }
    </style>
<c:if test="${captchaEnabled}">
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>
</c:if>
</head>
<body>

<div class="uiverse-midnight-sky">
    <div class="sky-canvas"></div>
    <div class="stars stars-1"></div>
    <div class="stars stars-2"></div>
    <div class="stars stars-3"></div>
    <div class="meteor m1"></div>
    <div class="meteor m2"></div>
    <div class="meteor m3"></div>
    <div class="meteor m4"></div>
    <div class="meteor m5"></div>
    <div class="meteor m6"></div>
    <div class="moon"></div>
    <div class="aurora aurora-1"></div>
    <div class="aurora aurora-2"></div>
</div>

<header>
    <a class="header-logo-link" href="${pageContext.request.contextPath}/" aria-label="Về trang chủ VinScape">
        <div class="header-logo"><img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo" style="width:22px; height:22px; object-fit:contain; vertical-align:middle; margin-right:8px; border-radius:4px;">VinScape</div>
    </a>
    <div class="header-right">
        <a class="header-exit" href="${pageContext.request.contextPath}/">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
            Thoát
        </a>
        <div class="header-icon">
            <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/><line x1="2" y1="12" x2="22" y2="12"/></svg>
        </div>
    </div>
</header>

<main>
    <div class="form-container">
        <p class="title">Đăng Nhập</p>

        <c:if test="${not empty error}"><div class="alert alert-error"><c:out value="${error}"/></div></c:if>
        <c:if test="${param.passwordChanged == '1'}"><div class="alert alert-success">Đổi mật khẩu thành công. Vui lòng đăng nhập lại.</div></c:if>

        <form class="form" method="post" action="${pageContext.request.contextPath}/login" autocomplete="off">
            <div class="input-group">
                <label for="username">Username hoặc Email</label>
                <input type="text" id="username" name="username" placeholder="admin124" required autofocus autocomplete="off">
            </div>
            
            <div class="input-group">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" placeholder="••••••" required autocomplete="off">
            </div>
            
            <div class="forgot">
                <a href="${pageContext.request.contextPath}/forgot-password">Forgot Password?</a>
            </div>

            <c:if test="${captchaEnabled}">
                <div class="g-recaptcha" style="margin: 14px 0;" data-sitekey="${captchaSiteKey}"></div>
            </c:if>

            <button type="submit" class="sign">Log in</button>
        </form>

        <p class="signup">Don't have an account?
            <a href="${pageContext.request.contextPath}/register">Sign up</a>
        </p>
    </div>
</main>

<footer>
    <div class="footer-top">
        <div class="slogan">CHO CỔ ĐÔNG. VÌ CỔ ĐÔNG.</div>
    </div>
    <div class="footer-bottom">
        <div>Copyright © 2026 VinScape Inc. All rights reserved.</div>
        <div class="footer-links">
            <a href="#">FAQ</a> <span>|</span>
            <a href="#">Legal Terms</a> <span>|</span>
            <a href="#">Privacy Policy</a> <span>|</span>
            <a href="#">Cookie Settings</a>
        </div>
    </div>
</footer>

<jsp:include page="/WEB-INF/views/common/ai-chatbox.jsp" />

</body>
</html>