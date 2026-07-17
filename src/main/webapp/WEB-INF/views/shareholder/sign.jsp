<%@ include file="/WEB-INF/views/common/header.jsp" %>
<h1>Ky nhan co phan / co tuc</h1>

<c:if test="${not empty error}"><div class="error-box"><c:out value="${error}"/></div></c:if>

<div class="card">
    <h2>Danh sach cho ky</h2>
    <table>
        <tr><th>ID</th><th>Dot phat hanh</th><th>SL du kien</th><th>Trang thai</th><th>Hanh dong</th></tr>
        <c:forEach var="d" items="${pendingSignatures}">
            <tr>
                <td><c:out value="${d.id}"/></td>
                <td><c:out value="${d.issueId}"/></td>
                <td><c:out value="${d.eligibleQuantity}"/></td>
                <td>
                    <span class="badge badge-${d.status == 'PENDING' ? 'pending' : d.status == 'ACCEPTED' ? 'accepted' : d.status == 'COMPLETED' ? 'completed' : 'expired'}">
                        <c:out value="${d.status}"/>
                    </span>
                </td>
                <td>
                    <c:if test="${d.status == 'PENDING'}">
                        <button type="button" class="btn" onclick="openSignModal(${d.id})">Ky ngay</button>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pendingSignatures}">
            <tr><td colspan="5" style="color:#94a3b8;">Khong co ban ghi nao</td></tr>
        </c:if>
    </table>
</div>

<!-- Modal ky chu ky tay -->
<div id="signModal" style="display:none; position:fixed; inset:0; background:rgba(0,0,0,.6);
     align-items:center; justify-content:center; z-index:50;">
    <div class="card" style="width:420px;">
        <h2 style="margin-top:0;">Ky chu ky tay cua ban</h2>
        <p style="color:#94a3b8; font-size:13px;">
            Day la chu ky TAY (ve bang chuot/ngon tay), khong phai chu ky dien tu.
        </p>
        <canvas id="signatureCanvas" width="360" height="160"></canvas>
        <div style="margin-top:10px;">
            <button type="button" class="secondary" onclick="clearSignature()">Xoa</button>
            <button type="button" onclick="submitSignature()">Xac nhan ky</button>
            <button type="button" class="secondary" onclick="closeSignModal()">Huy</button>
        </div>
        <form id="signForm" method="post" action="${pageContext.request.contextPath}/app/shareholder/sign" style="display:none;">
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
