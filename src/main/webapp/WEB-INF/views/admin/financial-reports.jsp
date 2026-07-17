<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Bao cao tai chinh</h1>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

<div class="card">
    <h2>Them bao cao quy moi</h2>
    <form method="post" action="${pageContext.request.contextPath}/app/admin/financial-reports/manage">
        <div class="grid-2">
            <div>
                <label>Nam</label>
                <input type="number" name="reportYear" required min="2000">
                <label>Quy (1-4)</label>
                <input type="number" name="reportQuarter" required min="1" max="4">
                <label>Doanh thu</label>
                <input type="text" name="revenue">
                <label>Loi nhuan truoc thue</label>
                <input type="text" name="profitBeforeTax">
                <label>Loi nhuan sau thue</label>
                <input type="text" name="profitAfterTax">
            </div>
            <div>
                <label>No ngan han</label>
                <input type="text" name="shortTermDebt">
                <label>No dai han</label>
                <input type="text" name="longTermDebt">
                <label>EPS</label>
                <input type="text" name="eps">
                <label>P/E</label>
                <input type="text" name="pe">
                <label>ROE (%)</label>
                <input type="text" name="roe">
                <label>ROA (%)</label>
                <input type="text" name="roa">
            </div>
        </div>
        <button type="submit">Them bao cao</button>
    </form>
</div>

<div class="card">
    <h2>Danh sach bao cao</h2>
    <table>
        <tr><th>Nam</th><th>Quy</th><th>Doanh thu</th><th>LNST</th><th>EPS</th><th>ROE</th></tr>
        <c:forEach var="r" items="${reports}">
            <tr>
                <td><c:out value="${r.reportYear}"/></td>
                <td><c:out value="${r.reportQuarter}"/></td>
                <td><c:out value="${r.revenue}"/></td>
                <td><c:out value="${r.profitAfterTax}"/></td>
                <td><c:out value="${r.eps}"/></td>
                <td><c:out value="${r.roe}"/></td>
            </tr>
        </c:forEach>
    </table>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
