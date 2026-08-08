<%--
    ================================================================
    GOOGLE ANALYTICS 4 (GA4) - Snippet dung chung cho toan bo he thong VinScape
    ================================================================
    CHO DUY NHAT CAN CHINH: thay "G-XXXXXXXXXX" ben duoi bang Measurement ID
    that cua ban (lay tai: Google Analytics > Admin > Luong du lieu > Web >
    chon luong > "Measurement ID" - dang G-XXXXXXXXXX).

    File nay duoc include (<%@ include %>) ngay sau the <head> cua MOI trang
    JSP co giao dien nguoi dung (index, login, register, forgot/reset-password,
    va toan bo trang trong admin/it/shareholder qua header.jsp dung chung).

    Muon tat tam thoi (vd moi truong dev/test) ma khong xoa code: doi GA_ENABLED
    thanh false ben duoi - script se khong duoc nhung vao trang.

    ================================================================
    COOKIE CONSENT (Nghi dinh 13/2023/ND-CP ve bao ve du lieu ca nhan)
    ================================================================
    GA4 KHONG duoc tu dong chay ngay khi vao trang nua - phai co su dong y
    cua nguoi dung truoc (banner hien o duoi man hinh, co ca nut "Dong y"
    va "Tu choi", khong duoc chi co 1 nut ep dong y).
    - Bam "Dong y"  -> luu cookie ga_consent=granted (180 ngay), GA4 moi bat dau load.
    - Bam "Tu choi" -> luu cookie ga_consent=declined (180 ngay), GA4 khong bao gio load.
    - Da co cookie (granted/declined) tu truoc -> khong hien banner lai nua.
--%>
<%
    // ---- CAU HINH GA4 - CHI SUA 2 DONG NAY ----
    String gaMeasurementId = "G-F64BQJL0JJ"; // <-- Thay bang Measurement ID that cua ban
    boolean gaEnabled = true;                 // <-- false = tat GA4 (vd khi chay local/dev)
%>
<%
    if (gaEnabled && gaMeasurementId != null && !gaMeasurementId.contains("XXXXXXXXXX")) {
%>
<style>
    #ga-consent-banner {
        position: fixed; left: 0; right: 0; bottom: 0; z-index: 99999;
        background: #1f2937; color: #f3f4f6; padding: 16px 20px;
        display: flex; flex-wrap: wrap; align-items: center; gap: 16px;
        font-family: Arial, sans-serif; font-size: 13.5px; line-height: 1.5;
        box-shadow: 0 -2px 12px rgba(0,0,0,0.25);
    }
    #ga-consent-banner p { margin: 0; flex: 1 1 320px; }
    #ga-consent-banner .ga-actions { display: flex; gap: 10px; flex-shrink: 0; }
    #ga-consent-banner button {
        padding: 8px 18px; border-radius: 6px; font-size: 13.5px; font-weight: 600;
        cursor: pointer; border: 1px solid transparent;
    }
    #ga-consent-accept { background: #2f6fed; color: #fff; }
    #ga-consent-accept:hover { background: #2563eb; }
    #ga-consent-decline { background: transparent; color: #f3f4f6; border-color: #6b7280; }
    #ga-consent-decline:hover { background: #374151; }
</style>
<script>
(function () {
    var GA_ID = "<%= gaMeasurementId %>";
    var COOKIE_NAME = "ga_consent";

    function getCookie(name) {
        var m = document.cookie.match('(?:^|; )' + name + '=([^;]*)');
        return m ? decodeURIComponent(m[1]) : null;
    }
    function setCookie(name, value) {
        document.cookie = name + "=" + encodeURIComponent(value) +
            "; max-age=" + (180 * 24 * 60 * 60) + "; path=/; SameSite=Lax";
    }
    function loadGA4() {
        var s = document.createElement("script");
        s.async = true;
        s.src = "https://www.googletagmanager.com/gtag/js?id=" + GA_ID;
        document.head.appendChild(s);
        window.dataLayer = window.dataLayer || [];
        function gtag() { dataLayer.push(arguments); }
        window.gtag = gtag;
        gtag('js', new Date());
        // An 1 phan IP nguoi dung de tuan thu quyen rieng tu co ban.
        gtag('config', GA_ID, { anonymize_ip: true });
    }
    function showBanner() {
        var el = document.createElement("div");
        el.id = "ga-consent-banner";
        el.innerHTML =
            '<p>Website su dung Google Analytics (GA4) de thong ke luot truy cap, ' +
            'giup cai thien trai nghiem su dung. Ban co the dong y hoac tu choi ' +
            'theo Nghi dinh 13/2023/ND-CP ve bao ve du lieu ca nhan.</p>' +
            '<div class="ga-actions">' +
            '<button id="ga-consent-decline" type="button">Tu choi</button>' +
            '<button id="ga-consent-accept" type="button">Dong y</button>' +
            '</div>';
        document.body.appendChild(el);
        document.getElementById("ga-consent-accept").addEventListener("click", function () {
            setCookie(COOKIE_NAME, "granted");
            el.remove();
            loadGA4();
        });
        document.getElementById("ga-consent-decline").addEventListener("click", function () {
            setCookie(COOKIE_NAME, "declined");
            el.remove();
        });
    }

    function init() {
        var consent = getCookie(COOKIE_NAME);
        if (consent === "granted") {
            loadGA4();
        } else if (consent === "declined") {
            // Da tu choi truoc do - khong lam gi ca, khong hien banner lai.
        } else {
            showBanner();
        }
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }
})();
</script>
<%
    }
%>
