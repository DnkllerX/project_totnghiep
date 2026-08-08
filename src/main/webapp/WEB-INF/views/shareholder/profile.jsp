<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<style>
    :root {
        --card-bg: #ffffff; --text-main: #0f172a; --text-muted: #64748b;
        --primary: #2f6fed; --primary-hover: #2563eb; --border-color: #e2e8f0;
        --danger: #ef4444; --danger-bg: #fef2f2; --success-bg: #f0fdf4; --success: #15803d; --info-bg: #eff6ff;
    }
    .page-title { font-size: 24px; font-weight: 700; color: var(--text-main); margin: 0 0 6px 0; letter-spacing: -0.5px; }
    .fpt-accent {
        display: inline-block; width: 34px; height: 6px; border-radius: 4px;
        background: linear-gradient(90deg, #F58220 0 33%, #005BAA 33% 66%, #00A651 66% 100%);
        margin-bottom: 20px;
    }
    .error-box {
        background-color: var(--danger-bg); color: var(--danger); border: 1px solid #fca5a5;
        padding: 14px 20px; border-radius: 8px; font-size: 13.5px; font-weight: 500; margin-bottom: 20px;
    }
    .success-box {
        background-color: var(--success-bg); color: var(--success); border: 1px solid #86efac;
        padding: 14px 20px; border-radius: 8px; font-size: 13.5px; font-weight: 500; margin-bottom: 20px;
    }
    .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
    @media (max-width: 900px) { .grid-2 { grid-template-columns: 1fr; } }

    .card {
        background: var(--card-bg); border-radius: 12px; padding: 24px 32px;
        border: 1px solid var(--border-color); box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        margin-bottom: 24px; overflow: hidden;
    }
    .card h2 {
        font-size: 16px; font-weight: 700; color: var(--text-main); margin: 0 0 20px 0;
        padding-bottom: 16px; border-bottom: 1px solid var(--border-color);
        display: flex; align-items: center; gap: 8px;
    }
    .card h2::before { content: ""; display: block; width: 4px; height: 16px; background-color: var(--primary); border-radius: 4px; }

    .info-table { width: 100%; border-collapse: collapse; margin-bottom: 8px; }
    .info-table th {
        text-align: left; color: var(--text-muted); font-weight: 500; font-size: 12.5px;
        padding: 8px 0; width: 130px; vertical-align: top;
    }
    .info-table td { padding: 8px 0; font-size: 13.5px; color: var(--text-main); font-weight: 500; }

    .form-group { margin-bottom: 14px; }
    .form-group label { display: block; font-size: 12.5px; font-weight: 600; color: var(--text-muted); margin-bottom: 6px; }
    .input-field {
        width: 100%; border: 1px solid var(--border-color); border-radius: 6px; padding: 10px 12px;
        font-size: 13.5px; font-family: 'Inter', sans-serif; box-sizing: border-box;
    }
    .input-field:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--info-bg); }
    .field-note { color: var(--text-muted); font-size: 11.5px; margin: 8px 0 16px; }
    .btn-submit {
        background: var(--primary); color: #fff; border: none; padding: 10px 20px;
        border-radius: 6px; font-size: 13.5px; font-weight: 600; cursor: pointer; transition: all .2s;
    }
    .btn-submit:hover { background: var(--primary-hover); transform: translateY(-1px); }
</style>

<h1 class="page-title">Tài khoản của tôi</h1>
<span class="fpt-accent"></span>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>
<c:if test="${not empty success}"><div class="success-box"><c:out value="${success}"/></div></c:if>

<div class="grid-2">
    <div class="card">
        <h2>Thông tin tài khoản</h2>
        <table class="info-table">
            <tr><th>Username</th><td><c:out value="${profile.user.username}"/></td></tr>
            <tr><th>Email</th><td><c:out value="${profile.user.email}"/></td></tr>
            <tr><th>Vai trò</th><td><c:out value="${profile.user.role}"/></td></tr>
            <tr><th>Trạng thái</th><td><c:out value="${profile.user.status}"/></td></tr>
        </table>

        <c:if test="${not empty profile.shareholder}">
            <h2>Thông tin cổ đông</h2>
            <form method="post" action="${pageContext.request.contextPath}/app/shareholder/profile"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="update-info">

                <div class="form-group">
                    <label for="fullName">Họ tên</label>
                    <input class="input-field" type="text" id="fullName" name="fullName" required maxlength="150"
                           value='<c:out value="${profile.shareholder.fullName}"/>'>
                </div>
                <div class="form-group">
                    <label for="citizenId">CCCD/CMND</label>
                    <input class="input-field" type="text" id="citizenId" name="citizenId" required pattern="[0-9]{9,12}"
                           title="9-12 chữ số" value='<c:out value="${profile.shareholder.citizenId}"/>'>
                </div>
                <div class="form-group">
                    <label for="phone">Số điện thoại</label>
                    <input class="input-field" type="text" id="phone" name="phone" maxlength="20"
                           value='<c:out value="${profile.shareholder.phone}"/>'>
                </div>
                <div class="form-group">
                    <label for="address">Địa chỉ</label>
                    <input class="input-field" type="text" id="address" name="address" maxlength="255"
                           value='<c:out value="${profile.shareholder.address}"/>'>
                </div>

                <p class="field-note">Quốc tịch: <c:out value="${profile.shareholder.nationality}"/> (liên hệ ADMIN nếu cần đổi mục này)</p>
                <button type="submit" class="btn-submit">Lưu thông tin</button>
            </form>
        </c:if>
    </div>

    <div class="card">
        <h2>Đổi mật khẩu</h2>
        <form method="post" action="${pageContext.request.contextPath}/app/shareholder/profile" autocomplete="off"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <input type="hidden" name="action" value="change-password">

            <div class="form-group">
                <label for="currentPassword">Mật khẩu hiện tại</label>
                <input class="input-field" type="password" id="currentPassword" name="currentPassword" required autocomplete="off">
            </div>
            <div class="form-group">
                <label for="newPassword">Mật khẩu mới</label>
                <input class="input-field" type="password" id="newPassword" name="newPassword" required autocomplete="off" minlength="8">
            </div>
            <div class="form-group">
                <label for="confirmNewPassword">Xác nhận mật khẩu mới</label>
                <input class="input-field" type="password" id="confirmNewPassword" name="confirmNewPassword" required autocomplete="off">
            </div>

            <p class="field-note">Tối thiểu 8 ký tự, gồm cả chữ và số. Sau khi đổi mật khẩu bạn sẽ cần đăng nhập lại.</p>
            <button type="submit" class="btn-submit">Đổi mật khẩu</button>
        </form>
    </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
