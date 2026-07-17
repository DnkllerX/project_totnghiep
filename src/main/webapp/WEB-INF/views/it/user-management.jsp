<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Quan ly tai khoan (IT)</h1>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>
<c:if test="${not empty sessionScope.flashTempPassword}">
    <div class="flash-box">
        Mat khau tam thoi (chi hien thi 1 lan, hay gui cho nguoi dung ngay):
        <b><c:out value="${sessionScope.flashTempPassword}"/></b>
    </div>
    <c:remove var="flashTempPassword" scope="session"/>
</c:if>

<div class="card">
    <h2>Tao tai khoan moi</h2>
    <form method="post" action="${pageContext.request.contextPath}/app/admin/user-management" autocomplete="off">
        <input type="hidden" name="action" value="create">
        <label>Username</label>
        <input type="text" name="username" required autocomplete="off">
        <label>Email</label>
        <input type="email" name="email" required autocomplete="off">
        <label>Mat khau</label>
        <input type="password" id="createPassword" name="password" required autocomplete="new-password">
        <div style="height:6px; border-radius:3px; background:#334155; margin-top:6px; overflow:hidden;">
            <div id="createPasswordBar" style="height:100%; width:0%; transition:width .2s, background .2s;"></div>
        </div>
        <div id="createPasswordLabel" style="font-size:12px; color:#94a3b8; margin-top:4px;"></div>
        <label>Vai tro</label>
        <select name="role" required>
            <option value="ADMIN">ADMIN</option>
            <option value="IT">IT</option>
            <option value="SHAREHOLDER">SHAREHOLDER</option>
        </select>
        <button type="submit">Tao tai khoan</button>
    </form>
</div>
<script src="${pageContext.request.contextPath}/js/password-strength.js"></script>
<script>
    initPasswordStrengthMeter('createPassword', 'createPasswordBar', 'createPasswordLabel');
</script>

<div class="card">
    <h2>Tim kiem / Loc / Sap xep</h2>
    <form method="get" action="${pageContext.request.contextPath}/app/admin/user-management">
        <div class="grid-2">
            <div>
                <label>Tim theo username</label>
                <input type="text" name="username" value="${filterUsername}">
                <label>Tim theo email</label>
                <input type="text" name="email" value="${filterEmail}">
            </div>
            <div>
                <label>Loc theo Role</label>
                <select name="role">
                    <option value="">-- Tat ca --</option>
                    <option value="ADMIN" ${filterRole == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                    <option value="IT" ${filterRole == 'IT' ? 'selected' : ''}>IT</option>
                    <option value="SHAREHOLDER" ${filterRole == 'SHAREHOLDER' ? 'selected' : ''}>SHAREHOLDER</option>
                </select>
                <label>Loc theo Status</label>
                <select name="status">
                    <option value="">-- Tat ca --</option>
                    <option value="ACTIVE" ${filterStatus == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                    <option value="LOCKED" ${filterStatus == 'LOCKED' ? 'selected' : ''}>LOCKED</option>
                </select>
                <label>Sap xep</label>
                <select name="sort">
                    <option value="NEWEST_FIRST" ${filterSort == 'NEWEST_FIRST' ? 'selected' : ''}>Ngay tao moi nhat</option>
                    <option value="USERNAME_ASC" ${filterSort == 'USERNAME_ASC' ? 'selected' : ''}>Username A-Z</option>
                </select>
            </div>
        </div>
        <button type="submit">Ap dung</button>
        <a class="btn secondary" href="${pageContext.request.contextPath}/app/admin/user-management">Xoa loc</a>
    </form>
</div>

<div class="card">
    <h2>Danh sach tai khoan</h2>
    <table>
        <tr><th>ID</th><th>Username</th><th>Email</th><th>Role</th><th>Trang thai</th><th>Hanh dong</th></tr>
        <c:forEach var="u" items="${users}">
            <tr>
                <td><c:out value="${u.userId}"/></td>
                <td><c:out value="${u.username}"/></td>
                <td><c:out value="${u.email}"/></td>
                <td><c:out value="${u.role}"/></td>
                <td>
                    <span class="badge badge-${u.status == 'ACTIVE' ? 'active' : 'locked'}">
                        <c:out value="${u.status}"/>
                    </span>
                </td>
                <td>
                    <button type="button" class="secondary"
                            onclick="openEditModal('${u.userId}', '<c:out value="${u.username}" escapeXml="true"/>', '<c:out value="${u.email}" escapeXml="true"/>', '${u.role}')">
                        Sua
                    </button>
                    <c:choose>
                        <c:when test="${u.status == 'ACTIVE'}">
                            <form class="inline" method="post" action="${pageContext.request.contextPath}/app/admin/user-management">
                                <input type="hidden" name="userId" value="${u.userId}">
                                <input type="hidden" name="action" value="lock">
                                <button type="submit" class="danger">Khoa</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <form class="inline" method="post" action="${pageContext.request.contextPath}/app/admin/user-management">
                                <input type="hidden" name="userId" value="${u.userId}">
                                <input type="hidden" name="action" value="unlock">
                                <button type="submit">Mo khoa</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                    <form class="inline" method="post" action="${pageContext.request.contextPath}/app/admin/user-management">
                        <input type="hidden" name="userId" value="${u.userId}">
                        <input type="hidden" name="action" value="reset-password">
                        <button type="submit" class="secondary">Reset mat khau</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty users}">
            <tr><td colspan="6" style="color:#94a3b8;">Khong co tai khoan nao khop dieu kien loc</td></tr>
        </c:if>
    </table>
</div>

<!-- Modal sua user -->
<div id="editModal" style="display:none; position:fixed; inset:0; background:rgba(0,0,0,.6);
     align-items:center; justify-content:center; z-index:50;">
    <div class="card" style="width:380px;">
        <h2 style="margin-top:0;">Sua tai khoan</h2>
        <form method="post" action="${pageContext.request.contextPath}/app/admin/user-management">
            <input type="hidden" name="action" value="update">
            <input type="hidden" id="editUserId" name="userId">
            <label>Username</label>
            <input type="text" id="editUsername" name="username" required>
            <label>Email</label>
            <input type="email" id="editEmail" name="email" required>
            <label>Vai tro</label>
            <select id="editRole" name="role" required>
                <option value="ADMIN">ADMIN</option>
                <option value="IT">IT</option>
                <option value="SHAREHOLDER">SHAREHOLDER</option>
            </select>
            <button type="submit">Luu thay doi</button>
            <button type="button" class="secondary" onclick="closeEditModal()">Huy</button>
        </form>
    </div>
</div>

<script>
function openEditModal(userId, username, email, role) {
    document.getElementById('editUserId').value = userId;
    document.getElementById('editUsername').value = username;
    document.getElementById('editEmail').value = email;
    document.getElementById('editRole').value = role;
    document.getElementById('editModal').style.display = 'flex';
}
function closeEditModal() {
    document.getElementById('editModal').style.display = 'none';
}
</script>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
