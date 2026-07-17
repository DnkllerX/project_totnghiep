<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Bao cao tai chinh</h1>

<div class="card">
    <table>
        <tr><th>Nam</th><th>Quy</th><th>Doanh thu</th><th>LN truoc thue</th><th>LN sau thue</th>
            <th>EPS</th><th>P/E</th><th>ROE</th><th>ROA</th></tr>
        <c:forEach var="r" items="${reports}">
            <tr>
                <td><c:out value="${r.reportYear}"/></td>
                <td><c:out value="${r.reportQuarter}"/></td>
                <td><c:out value="${r.revenue}"/></td>
                <td><c:out value="${r.profitBeforeTax}"/></td>
                <td><c:out value="${r.profitAfterTax}"/></td>
                <td><c:out value="${r.eps}"/></td>
                <td><c:out value="${r.pe}"/></td>
                <td><c:out value="${r.roe}"/></td>
                <td><c:out value="${r.roa}"/></td>
            </tr>
        </c:forEach>
        <c:if test="${empty reports}">
            <tr><td colspan="9" style="color:#94a3b8;">Chua co bao cao nao duoc cong bo</td></tr>
        </c:if>
    </table>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
