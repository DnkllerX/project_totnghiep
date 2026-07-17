<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Nghi quyet</h1>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

<div class="card">
    <h2>Tao nghi quyet moi</h2>
    <form method="post" action="${pageContext.request.contextPath}/app/admin/resolution">
        <label>Tieu de</label>
        <input type="text" name="title" required>
        <label>Mo ta</label>
        <textarea name="description" rows="3"></textarea>
        <label>Thoi gian bat dau bieu quyet</label>
        <input type="datetime-local" name="startTime" required>
        <label>Thoi gian ket thuc bieu quyet</label>
        <input type="datetime-local" name="endTime" required>
        <button type="submit">Tao nghi quyet (tu dong chup snapshot quyen bieu quyet)</button>
    </form>
</div>

<div class="card">
    <h2>Danh sach nghi quyet</h2>
    <table>
        <tr><th>ID</th><th>Tieu de</th><th>Trang thai</th><th>Bat dau</th><th>Ket thuc</th></tr>
        <c:forEach var="r" items="${resolutions}">
            <tr>
                <td><c:out value="${r.resolutionId}"/></td>
                <td><c:out value="${r.title}"/></td>
                <td>
                    <span class="badge badge-${r.status == 'OPEN' ? 'active' : 'closed'}">
                        <c:out value="${r.status}"/>
                    </span>
                </td>
                <td><c:out value="${r.startTime}"/></td>
                <td><c:out value="${r.endTime}"/></td>
            </tr>
        </c:forEach>
    </table>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
