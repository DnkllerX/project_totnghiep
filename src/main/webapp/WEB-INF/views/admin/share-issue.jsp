<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Phat hanh co phan / Co tuc</h1>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

<div class="card">
    <h2>Tao dot moi</h2>
    <form method="post" action="${pageContext.request.contextPath}/app/admin/share-issue">
        <div class="grid-2">
            <div>
                <label>Tieu de</label>
                <input type="text" name="title" required>
                <label>Loai</label>
                <select name="issueType" required>
                    <option value="DIVIDEND">DIVIDEND (co tuc bang co phieu)</option>
                    <option value="ISSUE">ISSUE (phat hanh them, co gia)</option>
                </select>
                <label>Ngay phat hanh</label>
                <input type="date" name="issueDate" required>
                <label>Thoi diem chot danh sach co dong (snapshot)</label>
                <input type="datetime-local" name="snapshotDate" required>
                <label>Tong so co phan phat hanh</label>
                <input type="number" name="shareQuantity" required min="1">
            </div>
            <div>
                <label>Ty le phat hanh (vd 0.1 = 10%)</label>
                <input type="text" name="issueRatio" placeholder="Bat buoc neu DIVIDEND">
                <label>Gia phat hanh (chi ISSUE, de trong neu DIVIDEND)</label>
                <input type="text" name="issuePrice">
                <label>Thoi gian bat dau ky (yyyy-MM-ddTHH:mm)</label>
                <input type="datetime-local" name="startDate" required>
                <label>Thoi gian ket thuc ky (yyyy-MM-ddTHH:mm)</label>
                <input type="datetime-local" name="endDate" required>
            </div>
        </div>
        <p style="color:#94a3b8; font-size:12px;">
            Thu tu bat buoc: Ngay phat hanh &le; Thoi diem chot snapshot &le; Bat dau ky &le; Ket thuc ky.
        </p>
        <label>Mo ta</label>
        <textarea name="description" rows="2"></textarea>
        <button type="submit">Tao dot phat hanh</button>
    </form>
</div>

<div class="card">
    <h2>Danh sach cac dot da tao</h2>
    <table>
        <tr><th>ID</th><th>Tieu de</th><th>Loai</th><th>SL</th><th>Chot snapshot</th><th>Bat dau ky</th><th>Ket thuc ky</th></tr>
        <c:forEach var="i" items="${issues}">
            <tr>
                <td><c:out value="${i.issueId}"/></td>
                <td><c:out value="${i.title}"/></td>
                <td><c:out value="${i.issueType}"/></td>
                <td><c:out value="${i.shareQuantity}"/></td>
                <td><c:out value="${i.snapshotDate}"/></td>
                <td><c:out value="${i.startDate}"/></td>
                <td><c:out value="${i.endDate}"/></td>
            </tr>
        </c:forEach>
    </table>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
