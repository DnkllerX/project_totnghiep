<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Dieu chinh co phan</h1>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

<div class="card">
    <h2>Nhap so co phan moi</h2>
    <form method="post" action="${pageContext.request.contextPath}/app/admin/share-adjust">
        <label>Shareholder ID</label>
        <input type="number" name="shareholderId" required min="1">
        <label>So co phan moi (khong phai so thay doi)</label>
        <input type="number" name="newQuantity" required min="0">
        <label>Ly do dieu chinh</label>
        <textarea name="reason" rows="2" required></textarea>
        <button type="submit">Cap nhat</button>
    </form>
</div>

<div class="card">
    <h2>So du hien tai</h2>
    <table>
        <tr><th>Shareholder ID</th><th>So co phan</th><th>Cap nhat luc</th></tr>
        <c:forEach var="s" items="${shares}">
            <tr>
                <td><c:out value="${s.shareholderId}"/></td>
                <td><c:out value="${s.quantity}"/></td>
                <td><c:out value="${s.updatedAt}"/></td>
            </tr>
        </c:forEach>
    </table>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
