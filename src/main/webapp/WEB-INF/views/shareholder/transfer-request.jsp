<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Chuyen nhuong co phan</h1>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

<div class="card">
    <h2>Tao yeu cau chuyen nhuong moi</h2>
    <form method="post" action="${pageContext.request.contextPath}/app/shareholder/transfer-request">
        <label>Chuyen den Shareholder ID</label>
        <input type="number" name="toShareholderId" required min="1">
        <label>So luong co phan</label>
        <input type="number" name="quantity" required min="1">
        <button type="submit">Gui yeu cau (cho ADMIN duyet)</button>
    </form>
</div>

<div class="card">
    <h2>Lich su giao dich cua ban</h2>
    <table>
        <tr><th>TX ID</th><th>Tu</th><th>Den</th><th>SL</th><th>Loai</th><th>Trang thai</th></tr>
        <c:forEach var="tx" items="${myTransfers}">
            <tr>
                <td><c:out value="${tx.txId}"/></td>
                <td><c:out value="${tx.fromShareholderId}"/></td>
                <td><c:out value="${tx.toShareholderId}"/></td>
                <td><c:out value="${tx.quantity}"/></td>
                <td><c:out value="${tx.txType}"/></td>
                <td>
                    <span class="badge badge-${tx.status == 'COMPLETED' ? 'completed' : tx.status == 'REJECTED' ? 'rejected' : 'pending'}">
                        <c:out value="${tx.status}"/>
                    </span>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty myTransfers}">
            <tr><td colspan="6" style="color:#94a3b8;">Chua co giao dich nao</td></tr>
        </c:if>
    </table>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
