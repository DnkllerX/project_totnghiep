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
        padding: 14px 20px; border-radius: 8px; font-size: 13.5px; font-weight: 500; margin-bottom: 24px;
    }
    .card {
        background: var(--card-bg); border-radius: 12px; padding: 24px 32px;
        border: 1px solid var(--border-color); box-shadow: 0 1px 3px rgba(0,0,0,0.05); overflow: hidden;
    }
    .card h2 {
        font-size: 16px; font-weight: 700; color: var(--text-main); margin: 0 0 20px 0;
        padding-bottom: 16px; border-bottom: 1px solid var(--border-color);
        display: flex; align-items: center; gap: 8px;
    }
    .card h2::before { content: ""; display: block; width: 4px; height: 16px; background-color: var(--primary); border-radius: 4px; }

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

    .badge-status { font-size: 12px; font-weight: 600; padding: 4px 10px; border-radius: 999px; display: inline-block; }
    .badge-open { color: #1d4ed8; background: #dbeafe; }
    .badge-closed { color: #475569; background: #f1f5f9; }

    .vote-select {
        border: 1px solid var(--border-color); border-radius: 6px; padding: 8px 10px;
        font-size: 13px; font-family: 'Inter', sans-serif;
    }
    .btn-submit {
        background: var(--primary); color: #fff; border: none; padding: 8px 16px;
        border-radius: 6px; font-size: 13px; font-weight: 600; cursor: pointer; transition: all .2s; margin-left: 8px;
    }
    .btn-submit:hover { background: var(--primary-hover); }

    .empty-state { text-align: center; color: var(--text-muted); padding: 32px !important; font-style: italic; }
    .card-note { color: var(--text-muted); font-size: 12px; margin-top: 16px; }
</style>

<h1 class="page-title">Biểu quyết Nghị quyết</h1>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

<div class="card">
    <h2>Danh sách nghị quyết</h2>
    <div class="table-wrapper">
        <table>
            <thead>
                <tr><th>ID</th><th>Tiêu đề</th><th>Trạng thái</th><th>Kết thúc</th><th>Biểu quyết</th></tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${resolutions}">
                    <tr>
                        <td style="color: var(--text-muted);">#<c:out value="${r.resolutionId}"/></td>
                        <td style="font-weight: 500;"><c:out value="${r.title}"/></td>
                        <td>
                            <span class="badge-status ${r.status == 'OPEN' ? 'badge-open' : 'badge-closed'}">
                                <c:out value="${r.status}"/>
                            </span>
                        </td>
                        <td style="color: var(--text-muted); font-size: 13px;"><c:out value="${r.endTime}"/></td>
                        <td>
                            <c:if test="${r.status == 'OPEN'}">
                                <form method="post" action="${pageContext.request.contextPath}/app/shareholder/vote" style="display:inline-flex; align-items:center;"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="resolutionId" value="${r.resolutionId}">
                                    <select name="voteValue" class="vote-select">
                                        <option value="AGREE">Đồng ý</option>
                                        <option value="DISAGREE">Không đồng ý</option>
                                        <option value="ABSTAIN">Trắng phiếu</option>
                                    </select>
                                    <button type="submit" class="btn-submit">Gửi</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty resolutions}">
                    <tr><td colspan="5" class="empty-state">Chưa có nghị quyết nào</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
    <p class="card-note">Lưu ý: mỗi cổ đông chỉ được biểu quyết 1 lần cho mỗi nghị quyết và không thể sửa sau khi đã gửi.</p>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
