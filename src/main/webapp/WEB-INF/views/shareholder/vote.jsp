<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Bieu quyet</h1>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

<div class="card">
    <h2>Danh sach nghi quyet</h2>
    <table>
        <tr><th>ID</th><th>Tieu de</th><th>Trang thai</th><th>Ket thuc</th><th>Bieu quyet</th></tr>
        <c:forEach var="r" items="${resolutions}">
            <tr>
                <td><c:out value="${r.resolutionId}"/></td>
                <td><c:out value="${r.title}"/></td>
                <td>
                    <span class="badge badge-${r.status == 'OPEN' ? 'active' : 'closed'}">
                        <c:out value="${r.status}"/>
                    </span>
                </td>
                <td><c:out value="${r.endTime}"/></td>
                <td>
                    <c:if test="${r.status == 'OPEN'}">
                        <form method="post" action="${pageContext.request.contextPath}/app/shareholder/vote" class="inline">
                            <input type="hidden" name="resolutionId" value="${r.resolutionId}">
                            <select name="voteValue" style="width:auto; display:inline-block;">
                                <option value="AGREE">AGREE</option>
                                <option value="DISAGREE">DISAGREE</option>
                                <option value="ABSTAIN">ABSTAIN</option>
                            </select>
                            <button type="submit">Gui</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty resolutions}">
            <tr><td colspan="5" style="color:#94a3b8;">Chua co nghi quyet nao</td></tr>
        </c:if>
    </table>
    <p style="color:#94a3b8; font-size:12px; margin-top:10px;">
        Luu y: moi co dong chi duoc bieu quyet 1 lan cho moi nghi quyet va khong the sua sau khi da gui.
    </p>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
