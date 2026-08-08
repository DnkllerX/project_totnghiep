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

    .notif-list { display: flex; flex-direction: column; gap: 16px; }
    .notif-card {
        background: var(--card-bg); border-radius: 12px; padding: 20px 24px;
        border: 1px solid var(--border-color); box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }
    .notif-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 10px; }
    .notif-type {
        font-size: 11.5px; font-weight: 700; padding: 4px 10px; border-radius: 999px;
        text-transform: uppercase; letter-spacing: 0.4px; display: inline-block;
    }
    .notif-type.vote { color: #1d4ed8; background: #dbeafe; }
    .notif-type.dividend { color: #b45309; background: #fef3c7; }

    .notif-title { font-size: 15px; font-weight: 700; color: var(--text-main); margin: 0 0 8px 0; }
    .notif-info { font-size: 13.5px; color: var(--text-main); margin: 4px 0; }
    .notif-note {
        margin-top: 10px; padding-top: 10px; border-top: 1px dashed var(--border-color);
        font-size: 12px; color: var(--text-muted); font-style: italic;
    }

    .empty-state {
        text-align: center; color: var(--text-muted); padding: 40px 20px; font-style: italic;
        background: var(--card-bg); border: 1px solid var(--border-color); border-radius: 12px;
    }
</style>

<h1 class="page-title">Thông báo</h1>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

<div class="notif-list">
    <c:forEach var="n" items="${notifications}">
        <div class="notif-card">
            <div class="notif-head">
                <span class="notif-type ${n.type == 'Biểu quyết' ? 'vote' : 'dividend'}"><c:out value="${n.type}"/></span>
            </div>
            <h3 class="notif-title"><c:out value="${n.title}"/></h3>
            <c:forEach var="line" items="${n.infoLines}">
                <p class="notif-info"><c:out value="${line}"/></p>
            </c:forEach>
            <c:if test="${not empty n.note}">
                <div class="notif-note"><c:out value="${n.note}"/></div>
            </c:if>
        </div>
    </c:forEach>
    <c:if test="${empty notifications}">
        <div class="empty-state">Chưa có thông báo nào.</div>
    </c:if>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
