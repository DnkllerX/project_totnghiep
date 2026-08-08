<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${document.title}"/> — Xem tài liệu</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --ink: #0f172a; --muted: #64748b; --border: #e2e8f0;
            --primary: #3b82f6; --primary-hover: #2563eb; --surface: #eef2f8;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        html, body {
            height: 100%; font-family: 'Inter', sans-serif; color: var(--ink);
            background: var(--surface); overflow: hidden;
        }
        .viewer-shell { display: flex; flex-direction: column; height: 100vh; background:radial-gradient(circle at 80% -10%,#273f65 0,transparent 34%),#111c2d; }

        /* ===== Topbar thuong hieu ===== */
        .topbar {
            background: rgba(10,20,35,.86); color: #fff; height: 58px; flex-shrink: 0;
            display: flex; align-items: center; justify-content: space-between; padding: 0 20px;
            border-bottom:1px solid rgba(255,255,255,.1); backdrop-filter:blur(16px);
        }
        .brand { display: inline-flex; align-items: center; gap: 8px; font-weight: 700; font-size: 14px; letter-spacing: .3px; }
        .brand a { color: #fff; text-decoration: none; display: inline-flex; align-items: center; gap: 8px; }
        .brand img { width: 20px; height: 20px; object-fit: contain; border-radius: 4px; }
        .close-link { color: #94a3b8; font-size: 13px; text-decoration: none; transition: color .15s; }
        .close-link:hover { color: #fff; }

        /* ===== Thanh cong cu tai lieu (kieu enterprise: file badge + ten + hanh dong) ===== */
        .doc-toolbar {
            background: rgba(255,255,255,.97); border-bottom: 1px solid var(--border); flex-shrink: 0;
            display: flex; align-items: center; justify-content: space-between;
            padding: 14px 24px; gap: 16px; flex-wrap: wrap; box-shadow:0 8px 24px rgba(15,23,42,.08); z-index:2;
        }
        .doc-identity { display: flex; align-items: center; gap: 12px; min-width: 0; }
        .file-badge {
            flex-shrink: 0; width: 36px; height: 36px; border-radius: 8px;
            background: linear-gradient(135deg, #ef4444, #b91c1c); color: #fff;
            display: flex; align-items: center; justify-content: center;
            font-size: 10px; font-weight: 800; letter-spacing: .5px;
        }
        .doc-name-block { min-width: 0; }
        .doc-name-block h1 {
            font-size: 14.5px; font-weight: 600; color: var(--ink);
            white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 480px;
        }
        .doc-name-block .doc-sub { font-size: 12px; color: var(--muted); margin-top: 2px; }
        .classification-badge {
            display: inline-flex; align-items: center; gap: 5px; font-size: 10.5px; font-weight: 700;
            color: #92400e; background: #fef3c7; border: 1px solid #fde68a;
            padding: 3px 9px; border-radius: 999px; letter-spacing: .3px; text-transform: uppercase;
        }

        .doc-actions { display: flex; align-items: center; gap: 8px; flex-shrink: 0; }
        .btn-toolbar {
            display: inline-flex; align-items: center; gap: 6px;
            padding: 8px 14px; border-radius: 6px; font-size: 12.5px; font-weight: 600;
            text-decoration: none; cursor: pointer; border: 1px solid var(--border);
            background: #fff; color: var(--ink); transition: all .15s;
        }
        .btn-toolbar:hover { background: #f1f5f9; border-color: #cbd5e1; }
        .btn-toolbar.primary { background: var(--primary); color: #fff; border-color: var(--primary); }
        .btn-toolbar.primary:hover { background: var(--primary-hover); }
        .btn-toolbar svg { width: 15px; height: 15px; }

        /* ===== Vung xem PDF chiem het khong gian con lai ===== */
        .viewer-content{flex:1;min-height:0;display:grid;grid-template-columns:minmax(0,1fr) 312px;background:#18263a;}
        .viewer-body { overflow: hidden; background:#27364b; position: relative; padding:12px; min-height:0; }
        .pdf-frame { width: 100%; height: 100%; border: none; display: block; border-radius:8px; box-shadow:0 20px 50px rgba(0,0,0,.3); background:#fff; position:relative; z-index:1; }
        .pdf-loading{position:absolute; inset:12px; z-index:0; display:flex; flex-direction:column; align-items:center; justify-content:center; color:#dbe7f7; gap:12px; font-size:13px; transition:opacity .3s;}
        .pdf-loading.hide{opacity:0;pointer-events:none;}
        .loader-ring{width:30px;height:30px;border:3px solid rgba(219,231,247,.2);border-top-color:#8fc3ff;border-radius:50%;animation:spin .8s linear infinite;}
        .related-panel{background:#f8fafc;border-left:1px solid rgba(15,23,42,.14);padding:19px 14px;overflow:auto;}
        .related-kicker{font-size:10px;text-transform:uppercase;letter-spacing:1.1px;color:#64748b;font-weight:700;margin:2px 8px 9px;}
        .related-title{font-size:15px;letter-spacing:-.15px;color:#0f172a;margin:0 8px 14px;}
        .related-list{display:flex;flex-direction:column;gap:6px;}
        .related-item{display:block;text-decoration:none;padding:12px 11px;border:1px solid transparent;border-radius:8px;color:#17263a;transition:background .18s,border-color .18s,transform .18s;}
        .related-item:hover{background:#fff;border-color:#dbe5f0;transform:translateX(-2px);box-shadow:0 4px 14px rgba(15,23,42,.06);}
        .related-item.active{background:#eaf2ff;border-color:#bfdbfe;}
        .related-item strong{display:block;font-size:12.5px;line-height:1.45;font-weight:600;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;}
        .related-item span{display:block;color:#64748b;font-size:10.5px;margin-top:6px;}
        .related-empty{color:#64748b;font-size:12px;padding:12px 8px;line-height:1.6;}

        .no-pdf-notice {
            position: absolute; inset: 0; display: flex; flex-direction: column;
            align-items: center; justify-content: center; gap: 14px;
            background: #fff; color: var(--muted); font-size: 14px; text-align: center; padding: 24px;
        }
        .no-pdf-notice a { color: var(--primary); font-weight: 600; text-decoration: none; }
        .no-pdf-notice a:hover { text-decoration: underline; }

        /* ===== Footer trang thai (enterprise footer bar) ===== */
        .viewer-footer {
            background:rgba(10,20,35,.92); border-top: 1px solid rgba(255,255,255,.1); flex-shrink: 0;
            padding: 7px 24px; display: flex; justify-content: space-between; align-items: center;
            font-size: 11px; color:#a9b9ce;
        }
        .viewer-footer .dot { color: #22c55e; }
        .btn-toolbar.icon-only{padding:8px;width:34px;justify-content:center;}
        .viewer-shell:fullscreen .topbar{display:none;}
        .viewer-shell:fullscreen .doc-toolbar{padding:10px 18px;}
        @keyframes spin{to{transform:rotate(360deg)}}
        @media(max-width:900px){.viewer-content{grid-template-columns:1fr}.related-panel{display:none}}
        @media(max-width:640px){.doc-toolbar{padding:11px 14px}.doc-name-block h1{max-width:210px}.viewer-body{padding:6px}.pdf-loading{inset:6px}.doc-actions{width:100%;justify-content:flex-end}.classification-badge{display:none}.viewer-footer span:last-child{display:none}}
        @media(prefers-reduced-motion:reduce){.loader-ring{animation:none}}
    </style>
</head>
<body>
<div class="viewer-shell">

    <header class="topbar">
        <span class="brand">
            <a href="${pageContext.request.contextPath}/app/dashboard">
                <img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo">
                VINSCAPE
            </a>
        </span>
        <a class="close-link" href="javascript:window.close()">Đóng ×</a>
    </header>

    <div class="doc-toolbar">
        <div class="doc-identity">
            <div class="file-badge">PDF</div>
            <div class="doc-name-block">
                <h1><c:out value="${document.title}"/></h1>
                <div class="doc-sub">
                    Đăng ngày <c:out value="${document.uploadedAtDisplay}"/>
                    <c:if test="${not empty document.description}"> · <c:out value="${document.description}"/></c:if>
                </div>
            </div>
        </div>

        <div class="doc-actions">
            <span class="classification-badge">
                <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="11" width="18" height="10" rx="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                Tài liệu nội bộ
            </span>
            <a class="btn-toolbar" href="javascript:window.print()">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 6 2 18 2 18 9"></polyline><path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"></path><rect x="6" y="14" width="12" height="8"></rect></svg>
                In
            </a>
            <button class="btn-toolbar icon-only" id="fullscreenBtn" type="button" title="Toàn màn hình" aria-label="Toàn màn hình">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M8 3H5a2 2 0 0 0-2 2v3M16 3h3a2 2 0 0 1 2 2v3M8 21H5a2 2 0 0 1-2-2v-3M16 21h3a2 2 0 0 0 2-2v-3"/></svg>
            </button>
            <a class="btn-toolbar primary"
               href="${pageContext.request.contextPath}/app/documents/download?id=${document.documentId}">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
                Tải xuống
            </a>
        </div>
    </div>

    <div class="viewer-content">
    <div class="viewer-body">
        <div class="pdf-loading" id="pdfLoading"><span class="loader-ring"></span><span>Đang mở tài liệu bảo mật…</span></div>
        <c:choose>
            <c:when test="${fn:endsWith(fn:toLowerCase(document.fileUrl), '.pdf')}">
                <iframe class="pdf-frame" title="Xem tài liệu PDF"
                        id="pdfFrame" src="${pageContext.request.contextPath}/app/documents/download?id=${document.documentId}&mode=view">
                </iframe>
            </c:when>
            <c:otherwise>
                <div class="no-pdf-notice">
                    <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline></svg>
                    <div>
                        Định dạng file này chưa hỗ trợ xem trước trực tiếp trong trình duyệt.<br>
                        Vui lòng <a href="${pageContext.request.contextPath}/app/documents/download?id=${document.documentId}">tải xuống</a> để xem.
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    <aside class="related-panel" aria-label="Tài liệu khác">
        <div class="related-kicker">Tài liệu khác</div>
        <h2 class="related-title">Cùng kho công bố</h2>
        <div class="related-list">
            <c:forEach var="related" items="${relatedDocuments}">
                <a class="related-item" href="${pageContext.request.contextPath}/app/documents/view?id=${related.documentId}">
                    <strong><c:out value="${related.title}"/></strong>
                    <span><c:out value="${related.uploadedAtDisplay}"/></span>
                </a>
            </c:forEach>
            <c:if test="${empty relatedDocuments}"><p class="related-empty">Chưa có tài liệu khác để hiển thị.</p></c:if>
        </div>
    </aside>
    </div>

    <footer class="viewer-footer">
        <span><span class="dot">●</span> Đã tải tài liệu thành công</span>
        <span>VinScape — Cổng thông tin cổ đông</span>
    </footer>

</div>
<script>
  (function () {
    var frame = document.getElementById('pdfFrame');
    var loader = document.getElementById('pdfLoading');
    if (frame && loader) frame.addEventListener('load', function () { loader.classList.add('hide'); });
    var button = document.getElementById('fullscreenBtn');
    var shell = document.querySelector('.viewer-shell');
    if (button && shell && shell.requestFullscreen) {
      button.addEventListener('click', function () {
        if (document.fullscreenElement) document.exitFullscreen(); else shell.requestFullscreen();
      });
    }
  }());
</script>
</body>
</html>
