<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Duyet chuyen nhuong co phan</h1>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

<div class="card">
    <h2>Yeu cau dang cho duyet</h2>
    <table>
        <tr><th>TX ID</th><th>Tu co dong</th><th>Den co dong</th><th>So luong</th><th>Hanh dong</th></tr>
        <c:forEach var="tx" items="${pendingTransfers}">
            <tr>
                <td><c:out value="${tx.txId}"/></td>
                <td><c:out value="${tx.fromShareholderId}"/></td>
                <td><c:out value="${tx.toShareholderId}"/></td>
                <td><c:out value="${tx.quantity}"/></td>
                <td>
                    <form class="inline" method="post" action="${pageContext.request.contextPath}/app/admin/transfer-approval">
                        <input type="hidden" name="txId" value="${tx.txId}">
                        <input type="hidden" name="action" value="approve">
                        <button type="submit">Duyet</button>
                    </form>
                    <form class="inline" method="post" action="${pageContext.request.contextPath}/app/admin/transfer-approval">
                        <input type="hidden" name="txId" value="${tx.txId}">
                        <input type="hidden" name="action" value="reject">
                        <button type="submit" class="danger">Tu choi</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pendingTransfers}">
            <tr><td colspan="5" style="color:#94a3b8;">Khong co yeu cau nao dang cho duyet</td></tr>
        </c:if>
    </table>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
