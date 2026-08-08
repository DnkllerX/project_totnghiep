<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
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
        border: 1px solid var(--border-color); box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        margin-bottom: 32px; overflow: hidden;
    }
    .card h2 {
        font-size: 16px; font-weight: 700; color: var(--text-main); margin: 0 0 12px 0;
        padding-bottom: 16px; border-bottom: 1px solid var(--border-color);
        display: flex; align-items:stretch; justify-content: space-between; gap: 8px;
    }
    .card h2 .title-inner { display: flex; align-items:stretch; gap: 8px; }
    .card h2 .title-inner::before {
        content: ""; display: block; width: 4px; height: 16px;
        background-color: var(--primary); border-radius: 4px;
    }
    .card-desc { color: var(--text-muted); font-size: 13px; margin: 0 0 20px 0; }

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
    tr.active-row td { background-color: var(--info-bg); }
    tr:hover td { background-color: #f8fafc; }

    .action-group { display: flex; align-items:stretch; gap: 8px; flex-wrap: wrap; min-height: 44px; }
    .action-group > .btn-outline {
        box-sizing: border-box; width: 138px; height: 44px; padding: 0; margin: 0; border-radius: .3rem;
        flex: 0 0 auto;
    }
    .btn-action {
        display: inline-flex; align-items:stretch; justify-content: center;
        padding: 8px 14px; border-radius: 6px; font-size: 12.5px; font-weight: 600;
        cursor: pointer; transition: all 0.2s; border: 1px solid transparent;
    }
    .btn-download {
        padding: 0;
        border: none;
        outline: none;
        font-size: 12px;
        font-weight: 600;
        border-radius: .3rem;
        background: linear-gradient(160deg, #2f5dc5 0%, #2e56c2 5%, #2f5dc5 11%, #3bbee6 57%, #00d4ff 71%);
        color: #fff;
        box-shadow: 1px 1px rgba(107,221,215,.37);
        filter: drop-shadow(0 0 8px rgba(59,190,230,.42));
        position: relative;
        overflow: hidden;
        cursor: pointer;
        transition: 0.4s ease-in-out;
        text-decoration: none;
        display: inline-flex;
        align-items:stretch;
        justify-content: center;
        box-sizing: border-box;
        width: 92px;
        height: 34px;
        margin: 0;
        flex: 0 0 auto;
    }
    .btn-download .text {
        position: absolute;
        left: 50%;
        top: 50%;
        margin: 0;
        transform: translate(-50%, -50%);
        white-space: nowrap;
        transition: 0.4s ease-in-out;
        color: #fff;
    }
    .btn-download .svg-download {
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -90px) rotate(30deg);
        opacity: 0;
        width: 1.35rem;
        height: 1.35rem;
        transition: 0.4s ease-in-out;
    }
    .btn-download:hover {
        background-color: rgb(50,50,50);
    }
    .btn-download:hover .svg-download {
        transform: translate(-50%, -50%) rotate(0deg);
        opacity: 1;
    }
    .btn-download:hover .text {
        opacity: 0;
    }
    .btn-download:active {
        transform: scale(.97);
    }
    .btn-primary { background: var(--primary); color: #fff; text-decoration: none; }
    .btn-primary:hover { background: var(--primary-hover); transform: translateY(-1px); }
    .btn-outline { background: transparent; color: var(--text-main); border-color: #cbd5e1; }
    .btn-outline:hover { background: #f1f5f9; border-color: #94a3b8; transform: translateY(-1px); }
    .btn-outline.is-active { background: var(--primary); color: #fff; border-color: var(--primary); }

    /* Nut Xem truoc dong bo kich thuoc va hinh dang voi nut Tai xuong */
    .action-group .preview-uiverse,
    .action-group .btn-download {
        width: 92px !important;
        height: 34px !important;
        flex: 0 0 92px !important;
    }
    .action-group .preview-uiverse { white-space: nowrap; }
    .preview-uiverse {
        width: 92px; height: 34px; box-sizing: border-box; padding: 0;
        display: inline-flex; align-items:stretch; justify-content: center;
        border: none; border-radius: .3rem; cursor: pointer; color: #fff;
        background: linear-gradient(160deg, #2f5dc5 0%, #2e56c2 5%, #2f5dc5 11%, #3bbee6 57%, #00d4ff 71%);
        box-shadow: 1px 1px rgba(107,221,215,.37); filter: drop-shadow(0 0 8px rgba(59,190,230,.42));
        transition: .4s ease-in-out;
    }
    .preview-uiverse::after { content: none; }
    .preview-uiverse .button-outer, .preview-uiverse .button-inner { display: contents; }
    .preview-uiverse span { font: 600 12px 'Inter', sans-serif; color: inherit; }
    .preview-uiverse:hover { background: rgb(50,50,50); }
    @media(prefers-reduced-motion:reduce){.preview-uiverse *{transition:none!important;}}

    .empty-state { text-align: center; color: var(--text-muted); padding: 32px !important; font-style: italic; }

    /* Khu vuc xem truoc NGAY TRONG TRANG (khong mo tab/popup moi) */
    #previewPanel { display: none; }
    #previewPanel.is-open { display: block; }
    .preview-frame-box {
        height: 640px; border: 1px solid var(--border-color); border-radius: 8px; overflow: hidden;
    }
    #previewFrame { width: 100%; height: 100%; border: none; display: block; }
    .btn-close-preview {
        background: transparent; border: 1px solid var(--border-color); color: var(--text-muted);
        padding: 6px 12px; border-radius: 6px; font-size: 12.5px; font-weight: 600; cursor: pointer;
    }
    .btn-close-preview:hover { background: #f1f5f9; }
</style>

<h1 class="page-title">Tài liệu Hệ thống</h1>

<c:if test="${not empty error}">
    <div class="error-box"><c:out value="${error}"/></div>
</c:if>

<!-- Khu vuc xem truoc: an mac dinh, hien ngay tren trang khi bam "Xem truoc", KHONG mo tab/trang moi -->
<div class="card" id="previewPanel">
    <h2>
        <span class="title-inner">Xem trước: <span id="previewTitle">—</span></span>
        <button type="button" class="btn-close-preview" onclick="closePreview()">Đóng</button>
    </h2>
    <div class="preview-frame-box">
        <iframe id="previewFrame" src="about:blank"></iframe>
    </div>
</div>

<div class="card">
    <h2><span class="title-inner">Danh sách tài liệu</span></h2>
    <p class="card-desc">
        Báo cáo tài chính, nghị quyết, văn bản liên quan do công ty công bố.
        Trang này chỉ để xem/tải xuống, không thể thêm/sửa/xóa.
    </p>

    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>Tiêu đề</th>
                    <th>Mô tả</th>
                    <th>Ngày đăng</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="d" items="${documents}">
                    <tr id="doc-row-${d.documentId}">
                        <td style="font-weight: 500;"><c:out value="${d.title}"/></td>
                        <td style="color: var(--text-muted);"><c:out value="${d.description}"/></td>
                        <td style="color: var(--text-muted); font-size: 13px;"><c:out value="${d.uploadedAtDisplay}"/></td>
                        <td>
                            <div class="action-group">
                                <c:if test="${fn:endsWith(fn:toLowerCase(d.fileUrl), '.pdf')}">
                                    <button type="button" class="preview-uiverse" id="btn-preview-${d.documentId}"
                                            data-doc-id="${d.documentId}" data-doc-title='<c:out value="${d.title}"/>'
                                            onclick="openPreview(this.dataset.docId, this.dataset.docTitle)">
                                        <span class="button-outer"><span class="button-inner"><span>Xem trước</span></span></span>
                                    </button>
                                </c:if>
                                <a class="btn-download"
                                          href="${pageContext.request.contextPath}/app/documents/download?id=${d.documentId}">
                                <span class="text">Tải xuống</span>
                                <div class="svg-download">
                                    <svg xmlns="http://www.w3.org/2000/svg" fill="white" viewBox="0 0 16 16">
                                        <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z"></path>
                                        <path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z"></path>
                                    </svg>
                                </div>
                            </a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty documents}">
                    <tr><td colspan="4" class="empty-state">Chưa có tài liệu nào được đăng</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<script>
    let currentActiveBtn = null;
    let currentActiveRow = null;

    function openPreview(documentId, title) {
        const panel = document.getElementById('previewPanel');
        const frame = document.getElementById('previewFrame');
        const titleSpan = document.getElementById('previewTitle');

        if (currentActiveBtn) currentActiveBtn.classList.remove('is-active');
        if (currentActiveRow) currentActiveRow.classList.remove('active-row');

        const btn = document.getElementById('btn-preview-' + documentId);
        const row = document.getElementById('doc-row-' + documentId);
        if (btn) { btn.classList.add('is-active'); currentActiveBtn = btn; }
        if (row) { row.classList.add('active-row'); currentActiveRow = row; }

        titleSpan.textContent = title;
        frame.src = '${pageContext.request.contextPath}/app/documents/download?id=' + documentId + '&mode=view';
        panel.classList.add('is-open');
        panel.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }

    function closePreview() {
        const panel = document.getElementById('previewPanel');
        const frame = document.getElementById('previewFrame');
        panel.classList.remove('is-open');
        frame.src = 'about:blank';
        if (currentActiveBtn) currentActiveBtn.classList.remove('is-active');
        if (currentActiveRow) currentActiveRow.classList.remove('active-row');
        currentActiveBtn = null;
        currentActiveRow = null;
    }
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>


<style>
.action-group .btn-download,
.action-group .preview-uiverse{
    width:92px;
    height:34px;
    display:inline-flex;
    align-items:center;
    justify-content:center;
    padding:0;
    margin:0;
    box-sizing:border-box;
    line-height:1;
    vertical-align:middle;
    flex:0 0 92px;
}
.btn-download .text{margin:0;}
</style>
