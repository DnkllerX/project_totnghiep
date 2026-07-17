<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Tai lieu danh cho co dong</h1>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

<div class="card">
    <h2>Upload tai lieu moi</h2>
    <form method="post" action="${pageContext.request.contextPath}/app/admin/documents" enctype="multipart/form-data">
        <label>Tieu de</label>
        <input type="text" name="title" required>
        <label>Mo ta</label>
        <textarea name="description" rows="2"></textarea>
        <label>File (PDF/Word/Excel, toi da 20MB)</label>
        <input type="file" name="file" accept=".pdf,.doc,.docx,.xls,.xlsx" required>
        <button type="submit">Upload</button>
    </form>
</div>

<div class="card">
    <h2>Danh sach tai lieu</h2>
    <table>
        <tr><th>ID</th><th>Tieu de</th><th>Duong dan</th><th>Ngay upload</th></tr>
        <c:forEach var="d" items="${documents}">
            <tr>
                <td><c:out value="${d.documentId}"/></td>
                <td><c:out value="${d.title}"/></td>
                <td><c:out value="${d.fileUrl}"/></td>
                <td><c:out value="${d.uploadedAt}"/></td>
            </tr>
        </c:forEach>
    </table>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
