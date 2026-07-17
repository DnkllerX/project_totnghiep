<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Quan ly co dong</h1>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

<div class="card">
    <h2>Tai khoan cho duyet (status = LOCKED)</h2>
    <table>
        <tr><th>Shareholder ID</th><th>Username</th><th>Ho ten</th><th>Cap so co phan khoi tao</th><th></th></tr>
        <c:forEach var="g" items="${pendingApprovals}">
            <tr>
                <td colspan="5">
                    <form method="post" action="${pageContext.request.contextPath}/app/admin/shareholders"
                          style="display:flex; align-items:center; gap:10px;">
                        <span style="min-width:60px;">#<c:out value="${g.shareholderId}"/></span>
                        <span style="flex:1;"><c:out value="${g.username}"/> - <c:out value="${g.fullName}"/> (<c:out value="${g.email}"/>)</span>
                        <input type="hidden" name="shareholderId" value="${g.shareholderId}">
                        <input type="number" name="initialQuantity" placeholder="So CP khoi tao" min="0"
                               style="width:160px;" value="0">
                        <button type="submit">Duyet & Cap CP</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pendingApprovals}">
            <tr><td colspan="5" style="color:#94a3b8;">Khong co tai khoan nao cho duyet</td></tr>
        </c:if>
    </table>
    <p style="color:#94a3b8; font-size:12px; margin-top:10px;">
        Sau khi duyet, tai khoan chuyen status LOCKED sang ACTIVE va co the dang nhap su dung day du chuc nang.
        Luu y: danh sach nay cung hien thi ca tai khoan bi khoa vi ly do khac (khong chi tai khoan moi dang ky).
    </p>
</div>

<div class="card">
    <h2>Danh sach co dong da duyet</h2>
    <table>
        <tr><th>ID</th><th>Ho ten</th><th>CCCD/CMND</th><th>SDT</th><th>Quoc tich</th></tr>
        <c:forEach var="sh" items="${shareholders}">
            <tr>
                <td><c:out value="${sh.shareholderId}"/></td>
                <td><c:out value="${sh.fullName}"/></td>
                <td><c:out value="${sh.citizenId}"/></td>
                <td><c:out value="${sh.phone}"/></td>
                <td><c:out value="${sh.nationality}"/></td>
            </tr>
        </c:forEach>
    </table>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
