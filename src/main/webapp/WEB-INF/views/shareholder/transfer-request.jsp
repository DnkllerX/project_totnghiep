<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<style>
    :root {
        --card-bg: #ffffff; --text-main: #0f172a; --text-muted: #64748b;
        --primary: #2f6fed; --primary-hover: #2563eb; --border-color: #e2e8f0;
        --danger: #ef4444; --danger-bg: #fef2f2; --info-bg: #eff6ff;
    }
    .page-title { font-size: 24px; font-weight: 700; color: var(--text-main); margin: 0 0 24px 0; letter-spacing: -0.5px; }
    .error-box {
        background-color: var(--danger-bg); color: var(--danger); border: 1px solid #fca5a5;
        padding: 14px 20px; border-radius: 8px; font-size: 13.5px; font-weight: 500;
        margin-bottom: 24px; display: flex; align-items: center; gap: 8px;
    }
    .card {
        background: var(--card-bg); border-radius: 12px; padding: 24px 32px;
        border: 1px solid var(--border-color); box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        margin-bottom: 32px; overflow: hidden;
    }
    .card h2 {
        font-size: 16px; font-weight: 700; color: var(--text-main);
        margin: 0 0 20px 0; padding-bottom: 16px; border-bottom: 1px solid var(--border-color);
        display: flex; align-items: center; gap: 8px;
    }
    .card h2::before { content: ""; display: block; width: 4px; height: 16px; background-color: var(--primary); border-radius: 4px; }
    .card-desc { color: var(--text-muted); font-size: 13px; margin: -8px 0 20px 0; }

    .form-row { display: flex; gap: 20px; flex-wrap: wrap; align-items: flex-end; }
    .form-group { display: flex; flex-direction: column; gap: 6px; }
    .form-group label { font-size: 12.5px; font-weight: 600; color: var(--text-muted); }
    .input-field {
        border: 1px solid var(--border-color); border-radius: 6px; padding: 10px 12px;
        font-size: 13.5px; font-family: 'Inter', sans-serif; width: 220px;
    }
    .input-field:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--info-bg); }
    .btn-submit {
        background: var(--primary); color: #fff; border: none; padding: 11px 22px;
        border-radius: 6px; font-size: 13.5px; font-weight: 600; cursor: pointer; transition: all .2s;
    }
    .btn-submit:hover { background: var(--primary-hover); transform: translateY(-1px); }

    .table-wrapper { width: 100%; overflow-x: auto; }
    table { width: 100%; border-collapse: collapse; text-align: left; }
    th {
        background-color: #f1f5f9; color: var(--text-muted); font-size: 12px; font-weight: 600;
        text-transform: uppercase; padding: 14px 16px; letter-spacing: 0.5px;
    }
    th:first-child { border-top-left-radius: 8px; border-bottom-left-radius: 8px; }
    th:last-child { border-top-right-radius: 8px; border-bottom-right-radius: 8px; }
    td { padding: 16px; border-bottom: 1px solid var(--border-color); font-size: 13.5px; color: var(--text-main); vertical-align: middle; }
    tr:last-child td { border-bottom: none; }
    tr:hover td { background-color: #f8fafc; }

    .badge-id {
        font-weight: 700; color: var(--primary); min-width: 46px; background: var(--info-bg);
        padding: 4px 8px; border-radius: 6px; text-align: center; display: inline-block; font-size: 12.5px;
    }
    .badge-status { font-size: 12px; font-weight: 600; padding: 4px 10px; border-radius: 999px; display: inline-block; }
    .badge-completed { color: #15803d; background: #dcfce7; }
    .badge-rejected { color: #b91c1c; background: #fee2e2; }
    .badge-pending { color: #b45309; background: #fef3c7; }

    .empty-state { text-align: center; color: var(--text-muted); padding: 32px !important; font-style: italic; }
</style>

<h1 class="page-title">Chuyển nhượng Cổ phần</h1>

<c:if test="${not empty error}">
    <div class="error-box">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
        <c:out value="${error}"/>
    </div>
</c:if>

<div class="card">
    <h2>Gửi yêu cầu chuyển nhượng</h2>
    <p class="card-desc">
        Bạn đang là cổ đông mã số <strong><c:out value="${shareholderId}"/></strong>.
        Yêu cầu sẽ cần ADMIN duyệt trước khi số cổ phần được chuyển.
    </p>
    <form method="post" action="${pageContext.request.contextPath}/app/shareholder/transfer-request"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <div class="form-row">
            <div class="form-group">
                <label for="toShareholderId">Chuyển đến (mã cổ đông)</label>
                <input class="input-field" type="number" id="toShareholderId" name="toShareholderId" min="1" required>
            </div>
            <div class="form-group">
                <label for="quantity">Số lượng cổ phần</label>
                <input class="input-field" type="number" id="quantity" name="quantity" min="1" required>
            </div>
            <button type="submit" class="btn-submit">Gửi yêu cầu</button>
        </div>
    </form>
</div>

<div class="card">
    <h2>Lịch sử giao dịch của tôi</h2>
    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>Từ</th>
                    <th>Đến</th>
                    <th>Số lượng</th>
                    <th>Loại</th>
                    <th>Trạng thái</th>
                    <th>Thời gian</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="tx" items="${myTransfers}">
                    <tr>
                        <td><span class="badge-id">#<c:out value="${tx.fromShareholderId}"/></span></td>
                        <td><span class="badge-id">#<c:out value="${tx.toShareholderId}"/></span></td>
                        <td style="font-weight: 600;"><c:out value="${tx.quantity}"/> CP</td>
                        <td style="color: var(--text-muted);"><c:out value="${tx.txType}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${tx.status == 'COMPLETED'}">
                                    <span class="badge-status badge-completed">Đã duyệt</span>
                                </c:when>
                                <c:when test="${tx.status == 'REJECTED'}">
                                    <span class="badge-status badge-rejected">Từ chối</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge-status badge-pending">Đang chờ</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td style="color: var(--text-muted); font-size: 13px;"><c:out value="${tx.createdAt}"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty myTransfers}">
                    <tr><td colspan="6" class="empty-state">Bạn chưa có giao dịch chuyển nhượng nào</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
