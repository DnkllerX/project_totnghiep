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
    .badge-pending { color: #b45309; background: #fef3c7; }
    .badge-accepted, .badge-completed { color: #15803d; background: #dcfce7; }
    .badge-expired { color: #b91c1c; background: #fee2e2; }

    .btn-action {
        display: inline-flex; align-items: center; justify-content: center;
        padding: 8px 14px; border-radius: 6px; font-size: 12.5px; font-weight: 600;
        cursor: pointer; transition: all 0.2s; border: 1px solid transparent;
        background: var(--primary); color: #fff;
    }
    .btn-action:hover { background: var(--primary-hover); }
    .empty-state { text-align: center; color: var(--text-muted); padding: 32px !important; font-style: italic; }

    /* Modal ky */
    .modal-backdrop {
        display:none; position:fixed; inset:0; background:rgba(15,23,42,.55);
        align-items:center; justify-content:center; z-index:50;
    }
    .modal-card {
        width: 420px; background: var(--card-bg); border-radius: 12px; padding: 24px 28px;
        box-shadow: 0 20px 40px rgba(0,0,0,0.25);
    }
    .modal-card h2 { border: none; padding: 0; margin-bottom: 8px; }
    .modal-card h2::before { display: none; }
    .modal-note { color: var(--text-muted); font-size: 12.5px; margin-bottom: 14px; }
    #signatureCanvas { border: 1px solid var(--border-color); border-radius: 8px; cursor: crosshair; background: #fff; }
    .modal-actions { margin-top: 14px; display: flex; gap: 10px; }
    .btn-secondary {
        background: transparent; color: var(--text-main); border: 1px solid var(--border-color);
        padding: 9px 16px; border-radius: 6px; font-size: 13px; font-weight: 600; cursor: pointer;
    }
    .btn-secondary:hover { background: #f1f5f9; }
    .btn-confirm {
        background: var(--primary); color: #fff; border: none; padding: 9px 16px;
        border-radius: 6px; font-size: 13px; font-weight: 600; cursor: pointer;
    }
    .btn-confirm:hover { background: var(--primary-hover); }
</style>

<h1 class="page-title">Ký nhận Cổ phần / Cổ tức</h1>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

<div class="card">
    <h2>Danh sách chờ ký</h2>
    <div class="table-wrapper">
        <table>
            <thead>
                <tr><th>ID</th><th>Đợt phát hành</th><th>SL dự kiến</th><th>Trạng thái</th><th>Hành động</th></tr>
            </thead>
            <tbody>
                <c:forEach var="d" items="${pendingSignatures}">
                    <tr>
                        <td style="color: var(--text-muted);">#<c:out value="${d.id}"/></td>
                        <td style="font-weight: 500;"><c:out value="${d.issueId}"/></td>
                        <td style="font-weight: 600;"><c:out value="${d.eligibleQuantity}"/> CP</td>
                        <td>
                            <span class="badge-status badge-${d.status == 'PENDING' ? 'pending' : d.status == 'ACCEPTED' ? 'accepted' : d.status == 'COMPLETED' ? 'completed' : 'expired'}">
                                <c:out value="${d.status}"/>
                            </span>
                        </td>
                        <td>
                            <c:if test="${d.status == 'PENDING'}">
                                <button type="button" class="btn-action" onclick="openSignModal(${d.id})">Ký ngay</button>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty pendingSignatures}">
                    <tr><td colspan="5" class="empty-state">Không có bản ghi nào</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<!-- Modal ký chữ ký tay -->
<div id="signModal" class="modal-backdrop">
    <div class="modal-card">
        <h2>Ký chữ ký tay của bạn</h2>
        <p class="modal-note">Đây là chữ ký TAY (vẽ bằng chuột/ngón tay), không phải chữ ký điện tử.</p>
        <canvas id="signatureCanvas" width="360" height="160"></canvas>
        <div class="modal-actions">
            <button type="button" class="btn-secondary" onclick="clearSignature()">Xóa</button>
            <button type="button" class="btn-confirm" onclick="submitSignature()">Xác nhận ký</button>
            <button type="button" class="btn-secondary" onclick="closeSignModal()">Hủy</button>
        </div>
        <form id="signForm" method="post" action="${pageContext.request.contextPath}/app/shareholder/sign" style="display:none;"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <input type="hidden" id="issueDetailId" name="issueDetailId">
            <input type="hidden" id="signatureData" name="signatureData">
        </form>
    </div>
</div>

<script>
let canvas, ctx, drawing = false;

function openSignModal(issueDetailId) {
    document.getElementById('issueDetailId').value = issueDetailId;
    document.getElementById('signModal').style.display = 'flex';
    canvas = document.getElementById('signatureCanvas');
    ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.lineWidth = 2;
    ctx.lineCap = 'round';
    ctx.strokeStyle = '#0f172a';

    canvas.onpointerdown = (e) => { drawing = true; draw(e); };
    canvas.onpointermove = (e) => { if (drawing) draw(e); };
    canvas.onpointerup = () => { drawing = false; ctx.beginPath(); };
    canvas.onpointerleave = () => { drawing = false; ctx.beginPath(); };
}

function draw(e) {
    const rect = canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    ctx.lineTo(x, y);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(x, y);
}

function clearSignature() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
}

function closeSignModal() {
    document.getElementById('signModal').style.display = 'none';
}

function submitSignature() {
    // canvas.toDataURL sinh ra chuoi base64 PNG - day chinh la "signatureData" gui len server,
    // server se decode va luu thanh file (xem SignatureUtil.saveHandwrittenSignature)
    const dataUrl = canvas.toDataURL('image/png');
    document.getElementById('signatureData').value = dataUrl;
    document.getElementById('signForm').submit();
}
</script>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
