<%@ page contentType="text/html;charset=UTF-8" %>
<%--
    AI Chatbox - tro ly ao (Gemini) noi cho toan bo trang GUEST va USER.
    Include o cuoi <body> bang: <jsp:include page="/WEB-INF/views/common/ai-chatbox.jsp" />

    Frontend goi POST ${contextPath}/chatbot (xem ChatbotServlet + ChatbotService).
    API key Gemini KHONG ton tai o day - chi song server-side trong gemini.properties.
--%>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

<style>
    #vsc-root, #vsc-root * { box-sizing: border-box; }
    #vsc-root {
        --vsc-bg-950: #05070d;
        --vsc-bg-900: #0a0f1c;
        --vsc-bg-800: #10182a;
        --vsc-bg-700: #161f36;
        --vsc-surface: #131c30;
        --vsc-border: rgba(167, 139, 250, 0.16);
        --vsc-border-soft: rgba(148, 163, 184, 0.12);
        --vsc-purple: #a78bfa;
        --vsc-purple-dim: #7c5cf0;
        --vsc-gold: #eab308;
        --vsc-text: #e7e9ee;
        --vsc-text-dim: #8b93a7;
        --vsc-green: #22c55e;
        font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
        position: fixed;
        top: 0; right: 0;
        z-index: 999999;
        pointer-events: none;
    }
    #vsc-root .vsc-clickable { pointer-events: auto; }

    /* ===== Nut mo (tab doc, bam vao canh phai man hinh) ===== */
    #vsc-launcher {
        position: fixed;
        right: 0;
        bottom: 26px;
        display: flex;
        align-items: center;
        gap: 7px;
        background: linear-gradient(135deg, var(--vsc-bg-900), var(--vsc-bg-700));
        color: #fff;
        border: 1px solid var(--vsc-border);
        border-right: none;
        border-radius: 11px 0 0 11px;
        padding: 9px 11px 9px 13px;
        cursor: pointer;
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(167,139,250,0.05) inset;
        transition: padding .2s ease, box-shadow .2s ease, background .2s ease, transform .2s ease;
        animation: vsc-launcher-rise .5s cubic-bezier(.2,.8,.2,1);
    }
    #vsc-launcher:hover { background: linear-gradient(135deg, var(--vsc-bg-800), #1c2645); padding-right: 14px; box-shadow: 0 8px 24px rgba(0,0,0,0.55), 0 0 18px rgba(167,139,250,0.18); transform: translateX(-2px); }
    #vsc-launcher .vsc-launcher-dot {
        width: 6px; height: 6px; border-radius: 50%; background: var(--vsc-green); flex-shrink: 0;
        box-shadow: 0 0 0 2px rgba(34, 197, 94, 0.2);
        animation: vsc-pulse-dot 2.4s ease-in-out infinite;
    }
    #vsc-launcher .vsc-launcher-icon {
        width: 15px; height: 15px; flex-shrink: 0; color: var(--vsc-purple);
    }
    #vsc-launcher span.vsc-launcher-label {
        font-size: 11px; font-weight: 600; letter-spacing: .2px; white-space: nowrap;
        background: linear-gradient(90deg, #fff, #cbd5e1);
        -webkit-background-clip: text; background-clip: text; color: transparent;
    }
    #vsc-launcher .vsc-badge {
        position: absolute; top: -5px; left: -5px;
        background: #ef4444; color: #fff; font-size: 9px; font-weight: 700;
        border-radius: 999px; min-width: 15px; height: 15px; padding: 0 3px;
        display: flex; align-items: center; justify-content: center;
        border: 2px solid var(--vsc-bg-900);
    }
    #vsc-root.vsc-open #vsc-launcher { display: none; }

    @keyframes vsc-launcher-rise { from { opacity: 0; transform: translateX(24px); } to { opacity: 1; transform: translateX(0); } }
    @keyframes vsc-pulse-dot { 0%, 100% { box-shadow: 0 0 0 3px rgba(34,197,94,0.2); } 50% { box-shadow: 0 0 0 6px rgba(34,197,94,0.08); } }

    /* ===== Panel chat ===== */
    #vsc-panel {
        position: fixed;
        top: 0; right: -320px;
        width: 300px;
        max-width: 88vw;
        height: 100vh;
        background: linear-gradient(180deg, var(--vsc-bg-900) 0%, #0c1220 100%);
        box-shadow: -16px 0 48px rgba(0, 0, 0, 0.5);
        display: flex;
        flex-direction: column;
        transition: right .32s cubic-bezier(.16,1,.3,1);
        border-left: 1px solid var(--vsc-border);
    }
    #vsc-root.vsc-open #vsc-panel { right: 0; }

    #vsc-header {
        flex-shrink: 0;
        position: relative;
        background: linear-gradient(135deg, var(--vsc-bg-900) 0%, var(--vsc-bg-700) 65%, #201a3a 100%);
        color: #fff;
        padding: 12px 13px;
        display: flex; align-items: center; gap: 9px;
        border-bottom: 1px solid var(--vsc-border-soft);
    }
    #vsc-header::before {
        content: ""; position: absolute; top: 0; left: 0; right: 0; height: 2px;
        background: linear-gradient(90deg, var(--vsc-gold), var(--vsc-purple) 55%, var(--vsc-purple-dim));
        background-size: 200% 100%;
        animation: vsc-sheen 6s linear infinite;
    }
    @keyframes vsc-sheen { 0% { background-position: 0% 0; } 100% { background-position: 200% 0; } }

    #vsc-header .vsc-avatar {
        width: 30px; height: 30px; border-radius: 9px;
        background: linear-gradient(135deg, var(--vsc-purple-dim), #6366f1);
        display: flex; align-items: center; justify-content: center; flex-shrink: 0;
        box-shadow: 0 0 0 1px rgba(255,255,255,0.08) inset, 0 3px 10px rgba(124,92,240,0.4);
        animation: vsc-avatar-glow 3.5s ease-in-out infinite;
    }
    @keyframes vsc-avatar-glow {
        0%, 100% { box-shadow: 0 0 0 1px rgba(255,255,255,0.08) inset, 0 3px 10px rgba(124,92,240,0.35); }
        50% { box-shadow: 0 0 0 1px rgba(255,255,255,0.08) inset, 0 3px 16px rgba(124,92,240,0.6); }
    }
    #vsc-header .vsc-avatar svg { width: 15px; height: 15px; color: #fff; }
    #vsc-header .vsc-title-block { flex: 1; min-width: 0; }
    #vsc-header .vsc-title { font-size: 12.5px; font-weight: 700; letter-spacing: .2px; }
    #vsc-header .vsc-subtitle { font-size: 10px; color: var(--vsc-text-dim); margin-top: 2px; display: flex; align-items: center; gap: 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    #vsc-header .vsc-subtitle .vsc-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--vsc-green); flex-shrink: 0; animation: vsc-pulse-dot 2.4s ease-in-out infinite; }
    #vsc-header-actions { display: flex; align-items: center; gap: 2px; flex-shrink: 0; }
    .vsc-icon-btn {
        width: 25px; height: 25px; border-radius: 7px; border: none; background: transparent;
        color: #94a3b8; cursor: pointer; display: flex; align-items: center; justify-content: center;
        transition: background .15s, color .15s;
    }
    .vsc-icon-btn:hover { background: rgba(167,139,250,0.12); color: #fff; }
    .vsc-icon-btn svg { width: 14px; height: 14px; }

    #vsc-messages {
        flex: 1; overflow-y: auto; padding: 13px 11px; display: flex; flex-direction: column; gap: 9px;
        background:
            radial-gradient(ellipse 500px 300px at 100% 0%, rgba(167,139,250,0.06), transparent 60%),
            var(--vsc-bg-950);
    }
    #vsc-messages::-webkit-scrollbar { width: 5px; }
    #vsc-messages::-webkit-scrollbar-track { background: transparent; }
    #vsc-messages::-webkit-scrollbar-thumb { background: var(--vsc-bg-700); border-radius: 4px; }

    .vsc-msg { display: flex; gap: 6px; max-width: 92%; animation: vsc-msg-in .32s cubic-bezier(.2,.8,.2,1); }
    @keyframes vsc-msg-in { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
    .vsc-msg.vsc-user { align-self: flex-end; flex-direction: row-reverse; }
    .vsc-msg.vsc-ai { align-self: flex-start; }
    .vsc-bubble {
        padding: 7px 10px; border-radius: 11px; font-size: 12px; line-height: 1.5;
        white-space: pre-wrap; word-break: break-word;
    }
    .vsc-msg.vsc-ai .vsc-bubble { background: var(--vsc-surface); color: var(--vsc-text); border: 1px solid var(--vsc-border-soft); border-bottom-left-radius: 3px; }
    .vsc-msg.vsc-user .vsc-bubble { background: linear-gradient(135deg, var(--vsc-purple-dim), #6366f1); color: #fff; border-bottom-right-radius: 3px; box-shadow: 0 3px 10px rgba(124,92,240,0.28); }
    .vsc-msg.vsc-error .vsc-bubble { background: rgba(220,38,38,0.12); color: #fca5a5; border: 1px solid rgba(220,38,38,0.35); }
    .vsc-mini-avatar {
        width: 20px; height: 20px; border-radius: 6px; flex-shrink: 0;
        display: flex; align-items: center; justify-content: center;
        background: linear-gradient(135deg, var(--vsc-purple-dim), #6366f1); color: #fff;
        margin-top: 2px;
    }
    .vsc-mini-avatar svg { width: 11px; height: 11px; }

    .vsc-welcome {
        background: var(--vsc-surface); border: 1px solid var(--vsc-border-soft); border-left: 2px solid var(--vsc-gold);
        border-radius: 11px;
        padding: 12px; font-size: 11.5px; color: #b6bccb; line-height: 1.55;
        animation: vsc-msg-in .4s cubic-bezier(.2,.8,.2,1);
    }
    .vsc-welcome strong { color: #fff; }

    #vsc-quick { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 9px; }
    .vsc-chip {
        border: 1px solid var(--vsc-border); background: rgba(167,139,250,0.06); color: #cbd5e1;
        font-size: 10.5px; font-weight: 500; padding: 5px 9px; border-radius: 999px;
        cursor: pointer; transition: all .15s;
    }
    .vsc-chip:hover { background: rgba(167,139,250,0.16); border-color: var(--vsc-purple); color: #fff; transform: translateY(-1px); }

    .vsc-typing { display: flex; gap: 3px; padding: 3px 2px; }
    .vsc-typing span {
        width: 5px; height: 5px; border-radius: 50%; background: var(--vsc-purple);
        animation: vsc-bounce 1.2s infinite ease-in-out;
    }
    .vsc-typing span:nth-child(2) { animation-delay: .15s; }
    .vsc-typing span:nth-child(3) { animation-delay: .3s; }
    @keyframes vsc-bounce { 0%, 60%, 100% { transform: translateY(0); opacity: .5; } 30% { transform: translateY(-4px); opacity: 1; } }

    #vsc-inputbar {
        flex-shrink: 0; border-top: 1px solid var(--vsc-border-soft); background: var(--vsc-bg-900);
        padding: 9px 10px; display: flex; flex-direction: column; gap: 6px;
    }
    #vsc-form { display: flex; align-items: flex-end; gap: 6px; }
    #vsc-input {
        flex: 1; resize: none; border: 1px solid var(--vsc-border-soft); border-radius: 9px;
        padding: 7px 9px; font-size: 12px; font-family: inherit; max-height: 72px;
        outline: none; transition: border-color .15s, box-shadow .15s; line-height: 1.4;
        background: var(--vsc-bg-950); color: var(--vsc-text);
    }
    #vsc-input::placeholder { color: #5b6478; }
    #vsc-input:focus { border-color: var(--vsc-purple); box-shadow: 0 0 0 3px rgba(167,139,250,0.15); }
    #vsc-send {
        flex-shrink: 0; width: 32px; height: 32px; border-radius: 9px; border: none;
        background: linear-gradient(135deg, var(--vsc-purple-dim), #6366f1); color: #fff; cursor: pointer;
        display: flex; align-items: center; justify-content: center;
        transition: filter .15s, opacity .15s, transform .1s;
        box-shadow: 0 3px 10px rgba(124,92,240,0.35);
    }
    #vsc-send:hover { filter: brightness(1.12); }
    #vsc-send:active { transform: scale(.94); }
    #vsc-send:disabled { opacity: .45; cursor: not-allowed; box-shadow: none; }
    #vsc-send svg { width: 14px; height: 14px; }
    .vsc-disclaimer { font-size: 9px; color: #565f74; text-align: center; }

    @media (max-width: 480px) {
        #vsc-panel { width: 100vw; max-width: 100vw; right: -100vw; }
        #vsc-launcher span.vsc-launcher-label { display: none; }
    }
    @media (prefers-reduced-motion: reduce) {
        #vsc-launcher, .vsc-msg, .vsc-welcome, #vsc-header::before,
        #vsc-launcher .vsc-launcher-dot, #vsc-header .vsc-subtitle .vsc-dot, #vsc-header .vsc-avatar {
            animation: none !important;
        }
    }
</style>

<div id="vsc-root">
    <button id="vsc-launcher" class="vsc-clickable" type="button" aria-label="Mở trợ lý AI VinScape">
        <span class="vsc-launcher-dot"></span>
        <svg class="vsc-launcher-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
        <span class="vsc-launcher-label">Trợ lý AI</span>
    </button>

    <aside id="vsc-panel" class="vsc-clickable" role="dialog" aria-label="Trợ lý AI VinScape">
        <header id="vsc-header">
            <div class="vsc-avatar">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1a7 7 0 0 1-7 7h-4a7 7 0 0 1-7-7H2a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2z"></path><circle cx="9" cy="13" r="1"></circle><circle cx="15" cy="13" r="1"></circle></svg>
            </div>
            <div class="vsc-title-block">
                <div class="vsc-title">VinScape AI</div>
                <div class="vsc-subtitle"><span class="vsc-dot"></span> Trợ lý ảo doanh nghiệp · Trực tuyến</div>
            </div>
            <div id="vsc-header-actions">
                <button type="button" class="vsc-icon-btn" id="vsc-clear" title="Xóa hội thoại" aria-label="Xóa hội thoại">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"></path><path d="M10 11v6"></path><path d="M14 11v6"></path><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"></path></svg>
                </button>
                <button type="button" class="vsc-icon-btn" id="vsc-close" title="Đóng" aria-label="Đóng">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </div>
        </header>

        <div id="vsc-messages">
            <div class="vsc-welcome" id="vsc-welcome">
                Xin chào 👋 Tôi là <strong>VinScape AI</strong> — trợ lý ảo của Hệ thống Quản lý Cổ đông.
                Tôi có thể giúp bạn tìm hiểu cách ký nhận cổ phần, biểu quyết, chuyển nhượng, xem báo cáo tài chính
                và các thao tác khác trên hệ thống.
                <div id="vsc-quick">
                    <button type="button" class="vsc-chip vsc-clickable" data-q="Làm sao để ký nhận cổ phần đã được phát hành?">Ký nhận cổ phần thế nào?</button>
                    <button type="button" class="vsc-chip vsc-clickable" data-q="Tôi muốn gửi yêu cầu chuyển nhượng cổ phần, cần làm gì?">Chuyển nhượng cổ phần?</button>
                    <button type="button" class="vsc-chip vsc-clickable" data-q="Tôi quên mật khẩu đăng nhập, phải làm sao?">Quên mật khẩu?</button>
                </div>
            </div>
        </div>

        <div id="vsc-inputbar">
            <form id="vsc-form">
                <textarea id="vsc-input" rows="1" maxlength="2000" placeholder="Nhập câu hỏi của bạn..." aria-label="Nhập câu hỏi"></textarea>
                <button type="submit" id="vsc-send" aria-label="Gửi">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"></line><polygon points="22 2 15 22 11 13 2 9 22 2"></polygon></svg>
                </button>
            </form>
            <div class="vsc-disclaimer">Nội dung do AI tạo ra, chỉ mang tính tham khảo.</div>
        </div>
    </aside>
</div>

<script>
(function () {
    var CTX = "${pageContext.request.contextPath}";
    var root = document.getElementById('vsc-root');
    var launcher = document.getElementById('vsc-launcher');
    var closeBtn = document.getElementById('vsc-close');
    var clearBtn = document.getElementById('vsc-clear');
    var messagesEl = document.getElementById('vsc-messages');
    var welcomeEl = document.getElementById('vsc-welcome');
    var form = document.getElementById('vsc-form');
    var input = document.getElementById('vsc-input');
    var sendBtn = document.getElementById('vsc-send');
    var STORAGE_KEY = 'vsc_chat_history_v1';

    var history = [];
    try {
        var saved = sessionStorage.getItem(STORAGE_KEY);
        if (saved) history = JSON.parse(saved);
    } catch (e) { history = []; }

    function persist() {
        try { sessionStorage.setItem(STORAGE_KEY, JSON.stringify(history)); } catch (e) {}
    }

    function escapeHtml(str) {
        var div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    function renderMessage(role, text, isError) {
        var wrap = document.createElement('div');
        wrap.className = 'vsc-msg ' + (role === 'user' ? 'vsc-user' : 'vsc-ai') + (isError ? ' vsc-error' : '');

        if (role !== 'user') {
            var avatar = document.createElement('div');
            avatar.className = 'vsc-mini-avatar';
            avatar.innerHTML = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1a7 7 0 0 1-7 7h-4a7 7 0 0 1-7-7H2a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2z"></path></svg>';
            wrap.appendChild(avatar);
        }

        var bubble = document.createElement('div');
        bubble.className = 'vsc-bubble';
        bubble.innerHTML = escapeHtml(text).replace(/\n/g, '<br>');
        wrap.appendChild(bubble);

        messagesEl.appendChild(wrap);
        messagesEl.scrollTop = messagesEl.scrollHeight;
        return wrap;
    }

    function renderTyping() {
        var wrap = document.createElement('div');
        wrap.className = 'vsc-msg vsc-ai';
        wrap.id = 'vsc-typing-indicator';
        wrap.innerHTML = '<div class="vsc-mini-avatar"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1a7 7 0 0 1-7 7h-4a7 7 0 0 1-7-7H2a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2z"></path></svg></div><div class="vsc-bubble"><div class="vsc-typing"><span></span><span></span><span></span></div></div>';
        messagesEl.appendChild(wrap);
        messagesEl.scrollTop = messagesEl.scrollHeight;
    }

    function removeTyping() {
        var el = document.getElementById('vsc-typing-indicator');
        if (el) el.remove();
    }

    function restoreHistory() {
        if (history.length > 0 && welcomeEl) welcomeEl.style.display = 'none';
        history.forEach(function (turn) {
            renderMessage(turn.role === 'model' ? 'model' : 'user', turn.text, false);
        });
    }
    restoreHistory();

    function openPanel() {
        root.classList.add('vsc-open');
        setTimeout(function () { input.focus(); }, 250);
    }
    function closePanel() { root.classList.remove('vsc-open'); }

    launcher.addEventListener('click', openPanel);
    closeBtn.addEventListener('click', closePanel);

    clearBtn.addEventListener('click', function () {
        history = [];
        persist();
        messagesEl.innerHTML = '';
        messagesEl.appendChild(welcomeEl);
        welcomeEl.style.display = 'block';
    });

    document.querySelectorAll('.vsc-chip').forEach(function (chip) {
        chip.addEventListener('click', function () {
            input.value = chip.getAttribute('data-q');
            form.requestSubmit();
        });
    });

    input.addEventListener('input', function () {
        input.style.height = 'auto';
        input.style.height = Math.min(input.scrollHeight, 96) + 'px';
    });
    input.addEventListener('keydown', function (e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            form.requestSubmit();
        }
    });

    var sending = false;
    form.addEventListener('submit', function (e) {
        e.preventDefault();
        var text = input.value.trim();
        if (!text || sending) return;

        if (welcomeEl) welcomeEl.style.display = 'none';
        renderMessage('user', text, false);
        history.push({ role: 'user', text: text });
        persist();

        input.value = '';
        input.style.height = 'auto';
        sending = true;
        sendBtn.disabled = true;
        renderTyping();

        fetch(CTX + '/chatbot', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                message: text,
                history: history.slice(0, -1),
                page: document.title
            })
        })
        .then(function (res) {
            return res.json().then(function (data) { return { ok: res.ok, data: data }; });
        })
        .then(function (result) {
            removeTyping();
            if (result.ok && result.data && result.data.reply) {
                renderMessage('model', result.data.reply, false);
                history.push({ role: 'model', text: result.data.reply });
                persist();
            } else {
                var errMsg = (result.data && result.data.error) ? result.data.error : 'Đã có lỗi xảy ra. Vui lòng thử lại.';
                renderMessage('model', errMsg, true);
            }
        })
        .catch(function () {
            removeTyping();
            renderMessage('model', 'Không thể kết nối tới máy chủ. Vui lòng kiểm tra mạng và thử lại.', true);
        })
        .finally(function () {
            sending = false;
            sendBtn.disabled = false;
        });
    });
})();
</script>
