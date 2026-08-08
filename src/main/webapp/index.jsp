<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ page import="com.shareholder.model.FinancialReport" %>
<%@ page import="com.shareholder.service.FinancialReportService" %>
<%@ page import="com.shareholder.model.Share" %>
<%@ page import="com.shareholder.model.Shareholder" %>
<%@ page import="com.shareholder.dao.impl.ShareDAOImpl" %>
<%@ page import="com.shareholder.dao.impl.ShareholderDAOImpl" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.math.RoundingMode" %>
<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%
    // Trang chu (public, khong qua servlet rieng) - lay thang du lieu THAT tu FINANCIAL_REPORTS
    // de thay cho so lieu tinh cung truoc day. Loi DB o day khong duoc lam sap trang chu,
    // nen chi log va de danh sach rong (JSP se tu hien trang thai "chua co du lieu").
    List<FinancialReport> homeReportsDesc = new ArrayList<FinancialReport>();
    try {
        homeReportsDesc = new FinancialReportService().listAll(); // da ORDER BY nam/quy DESC
    } catch (Exception ex) {
        getServletContext().log("Trang chu: loi tai bao cao tai chinh", ex);
    }

    FinancialReport homeLatest = homeReportsDesc.isEmpty() ? null : homeReportsDesc.get(0);
    FinancialReport homePrev = homeReportsDesc.size() > 1 ? homeReportsDesc.get(1) : null;

    // Dao nguoc thanh thu tu cu -> moi de ve bieu do, lay toi da 5 ky gan nhat
    List<FinancialReport> homeChronoAsc = new ArrayList<FinancialReport>(homeReportsDesc);
    Collections.reverse(homeChronoAsc);
    List<FinancialReport> homeLastFive = homeChronoAsc.size() > 5
            ? homeChronoAsc.subList(homeChronoAsc.size() - 5, homeChronoAsc.size())
            : homeChronoAsc;

    List<Map<String, Object>> homeBars = new ArrayList<Map<String, Object>>();
    BigDecimal maxRevenue = BigDecimal.ONE;
    for (FinancialReport r : homeLastFive) {
        if (r.getRevenue() != null && r.getRevenue().compareTo(maxRevenue) > 0) maxRevenue = r.getRevenue();
    }
    for (FinancialReport r : homeLastFive) {
        BigDecimal revenue = r.getRevenue() == null ? BigDecimal.ZERO : r.getRevenue();
        int heightPct = revenue.multiply(BigDecimal.valueOf(100))
                .divide(maxRevenue, 0, RoundingMode.HALF_UP).intValue();
        Map<String, Object> bar = new HashMap<String, Object>();
        bar.put("label", "Q" + r.getReportQuarter() + "/" + String.valueOf(r.getReportYear()).substring(2));
        bar.put("height", Math.max(heightPct, 4));
        homeBars.add(bar);
    }

    // Nhan khoang nam cho bieu do - lay THAT tu du lieu dang hien thi, khong co dinh nam.
    String homeYearRange = "--";
    if (!homeLastFive.isEmpty()) {
        int minYear = Integer.MAX_VALUE, maxYear = Integer.MIN_VALUE;
        for (FinancialReport r : homeLastFive) {
            minYear = Math.min(minYear, r.getReportYear());
            maxYear = Math.max(maxYear, r.getReportYear());
        }
        homeYearRange = (minYear == maxYear) ? String.valueOf(minYear) : (minYear + " — " + maxYear);
    }

    // Chenh lech % so voi quy truoc (null neu khong co du lieu quy truoc de so sanh)
    Double homeRevenueDeltaPct = null;
    Double homeProfitDeltaPct = null;
    if (homeLatest != null && homePrev != null) {
        if (homeLatest.getRevenue() != null && homePrev.getRevenue() != null
                && homePrev.getRevenue().signum() != 0) {
            homeRevenueDeltaPct = homeLatest.getRevenue().subtract(homePrev.getRevenue())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(homePrev.getRevenue(), 1, RoundingMode.HALF_UP).doubleValue();
        }
        if (homeLatest.getProfitAfterTax() != null && homePrev.getProfitAfterTax() != null
                && homePrev.getProfitAfterTax().signum() != 0) {
            homeProfitDeltaPct = homeLatest.getProfitAfterTax().subtract(homePrev.getProfitAfterTax())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(homePrev.getProfitAfterTax(), 1, RoundingMode.HALF_UP).doubleValue();
        }
    }

    request.setAttribute("homeLatest", homeLatest);
    request.setAttribute("homeBars", homeBars);
    request.setAttribute("homeYearRange", homeYearRange);
    request.setAttribute("homeRevenueDeltaPct", homeRevenueDeltaPct);
    request.setAttribute("homeProfitDeltaPct", homeProfitDeltaPct);

    // ---- CO CAU CO DONG (top 10 theo % so huu, con lai gop vao "Khac") ----
    // Cong thuc: % = so_co_phan_cua_1_co_dong / TONG so co phan dang luu hanh (SUM toan bang SHARES).
    // KHONG lay tong cua rieng top 10 lam mau so - neu khong % se bi thoi phong sai (vd top 10 chi
    // thuc te chiem 40% cong ty nhung neu chia cho tong top10 se thanh 100%, sai ban chat).
    List<Map<String, Object>> shTopRows = new ArrayList<Map<String, Object>>();
    long shTotalShares = 0;
    long shOtherQuantity = 0;
    double shOtherPct = 0;
    String shPieGradient = "conic-gradient(#e5decf 0deg 360deg)"; // fallback: khoanh tron xam neu chua co du lieu
    try {
        // Chi tinh "co dong hien huu" tren tai khoan dang ACTIVE - tai khoan da bi IT/ADMIN khoa
        // (status=LOCKED) khong duoc tinh vao co cau so huu hien thi cong khai o day, du ho van
        // con giu co phan trong DB (khoa tai khoan la van de truy cap he thong, khong dong nghia
        // voi mat quyen so huu co phan ve mat phap ly - nhung trang cong khai nay chi phan anh
        // "ai dang la co dong hoat dong", nen loc ra cho dung ngu canh hien thi).
        List<Shareholder> shActiveShareholders = new ShareholderDAOImpl().findAllActive();
        Set<Integer> shActiveIds = new HashSet<Integer>();
        Map<Integer, String> shNameById = new HashMap<Integer, String>();
        for (Shareholder sh : shActiveShareholders) {
            shActiveIds.add(sh.getShareholderId());
            shNameById.put(sh.getShareholderId(), sh.getFullName());
        }

        List<Share> shAllSharesRaw = new ShareDAOImpl().findAll();
        List<Share> shAllShares = new ArrayList<Share>();
        for (Share s : shAllSharesRaw) {
            if (shActiveIds.contains(s.getShareholderId())) shAllShares.add(s);
        }

        for (Share s : shAllShares) {
            if (s.getQuantity() > 0) shTotalShares += s.getQuantity();
        }

        List<Share> shSorted = new ArrayList<Share>(shAllShares);
        Collections.sort(shSorted, new Comparator<Share>() {
            public int compare(Share a, Share b) { return Integer.compare(b.getQuantity(), a.getQuantity()); }
        });
        List<Share> shTop10 = shSorted.size() > 10 ? shSorted.subList(0, 10) : shSorted;

        // Bang mau "brass" dam-nhat xoay vong, dong bo tong the mau vang-nau cua trang chu (khong dung
        // mau sac xanh-do-tim ngau nhien pha vo phong cach "paper/ledger" hien tai).
        String[] shPalette = {
            "#B08D57", "#8C6D31", "#D8B26B", "#6B4F27", "#C9A66B",
            "#7A5C3E", "#A67C52", "#5E4B32", "#E0C285", "#4F3B24"
        };

        StringBuilder shGradient = new StringBuilder("conic-gradient(");
        double shDeg = 0;
        long shTopSum = 0;
        int shColorIdx = 0;
        for (Share s : shTop10) {
            if (s.getQuantity() <= 0 || shTotalShares <= 0) continue;
            String shName = shNameById.get(s.getShareholderId());
            if (shName == null) shName = "Co dong #" + s.getShareholderId();
            double shPct = s.getQuantity() * 100.0 / shTotalShares;
            double shSliceDeg = s.getQuantity() * 360.0 / shTotalShares;
            String shColor = shPalette[shColorIdx % shPalette.length];

            Map<String, Object> shRow = new HashMap<String, Object>();
            shRow.put("name", shName);
            shRow.put("quantity", s.getQuantity());
            shRow.put("pct", shPct);
            shRow.put("color", shColor);
            shTopRows.add(shRow);

            shGradient.append(shColor).append(" ")
                    .append(String.format(Locale.US, "%.3f", shDeg)).append("deg ")
                    .append(String.format(Locale.US, "%.3f", shDeg + shSliceDeg)).append("deg, ");
            shDeg += shSliceDeg;
            shTopSum += s.getQuantity();
            shColorIdx++;
        }

        shOtherQuantity = shTotalShares - shTopSum;
        if (shTotalShares > 0 && shOtherQuantity > 0) {
            shOtherPct = shOtherQuantity * 100.0 / shTotalShares;
            shGradient.append("#e5decf ").append(String.format(Locale.US, "%.3f", shDeg)).append("deg 360deg");
        } else if (shGradient.length() > "conic-gradient(".length()) {
            shGradient.setLength(shGradient.length() - 2); // bo dau phay + space thua o cuoi
        }
        shGradient.append(")");
        if (shTotalShares > 0) shPieGradient = shGradient.toString();
    } catch (Exception ex) {
        getServletContext().log("Trang chu: loi tai co cau co dong", ex);
    }
    request.setAttribute("shTopRows", shTopRows);
    request.setAttribute("shTotalShares", shTotalShares);
    request.setAttribute("shOtherQuantity", shOtherQuantity);
    request.setAttribute("shOtherPct", shOtherPct);
    request.setAttribute("shPieGradient", shPieGradient);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<%@ include file="/WEB-INF/views/common/ga4.jsp" %>
<meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>VinScape — Doanh nghiệp kinh tế hàng đầu Việt Nam</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,400;9..144,500;9..144,600;9..144,700&family=Inter:wght@400;500;600;700&family=IBM+Plex+Mono:wght@500;600&display=swap" rel="stylesheet">
<style>
  :root{
    --ink:#0E1A2B;
    --ink-deep:#0A121D;
    --ink-line:rgba(231,236,227,0.12);
    --paper:#EFF2E7;
    --paper-card:#FBFCF8;
    --ledger-line:#C7D3B9;
    --brass:#B98B4E;
    --brass-hover:#A67A3E;
    --brass-soft:rgba(185,139,78,0.14);
    --text-dark:#E7ECE3;
    --text-dark-muted:#93A395;
    --text-paper:#1C2A1E;
    --text-paper-muted:#5C6D58;
    --nav-h:70px;
  }
  *{box-sizing:border-box; margin:0; padding:0;}
  html{scroll-behavior:smooth;}
  body{
    font-family:'Inter',sans-serif;
    background:var(--ink);
    color:var(--text-dark);
    -webkit-font-smoothing:antialiased;
  }
  .display{font-family:'Fraunces',serif;}
  .mono{font-family:'IBM Plex Mono',monospace;}
  a{color:inherit;}
  img,svg{display:block;}

  .reveal{opacity:0; transform:translateY(18px); transition:opacity .7s cubic-bezier(.2,.7,.3,1), transform .7s cubic-bezier(.2,.7,.3,1);}
  .reveal.in{opacity:1; transform:translateY(0);}
  .reveal.d1.in{transition-delay:.08s;}
  .reveal.d2.in{transition-delay:.16s;}
  .reveal.d3.in{transition-delay:.24s;}
  .reveal.d4.in{transition-delay:.32s;}
  @media (prefers-reduced-motion: reduce){ .reveal{opacity:1; transform:none; transition:none;} *,*::before,*::after{animation-duration:.01ms!important;animation-iteration-count:1!important;scroll-behavior:auto!important;} }

  /* ============ HEADER ============ */
  .nav{
    position:sticky; top:0; z-index:50;
    display:flex; align-items:center; justify-content:space-between;
    padding:16px 48px; background:rgba(14,26,43,0.82); backdrop-filter:blur(12px);
    border-bottom:1px solid var(--ink-line);
    transition:background .3s ease, border-color .3s ease;
  }
  .nav-brand{display:flex; align-items:center; gap:10px; font-weight:700; letter-spacing:.3px; font-size:15.5px; text-decoration:none;}
  .nav-brand .mark{
    width:22px; height:22px; border:1.5px solid var(--brass); border-radius:5px;
    display:flex; align-items:center; justify-content:center; color:var(--brass); font-size:11.5px; font-weight:700;
    font-family:'Fraunces',serif;
  }
  .nav-center{display:flex; align-items:center; gap:34px;}
  .nav-center a{
    font-size:13.5px; color:var(--text-dark-muted); text-decoration:none; transition:color .2s;
    position:relative; padding:4px 0;
  }
  .nav-center a::after{
    content:""; position:absolute; left:0; bottom:-2px; width:0; height:1px; background:var(--brass);
    transition:width .25s ease;
  }
  .nav-center a:hover{color:var(--text-dark);}
  .nav-center a:hover::after, .nav-center a.active::after{width:100%;}
  .nav-center a.active{color:var(--brass);}

  .nav-auth{display:flex; align-items:center; gap:18px;}
  .nav-auth .link-signin{font-size:13px; color:var(--text-dark-muted); text-decoration:none; transition:color .2s; font-weight:500;}
  .nav-auth .link-signin:hover{color:var(--text-dark);}
  .btn{
    display:inline-flex; align-items:center; justify-content:center; gap:8px;
    padding:9px 18px; border-radius:5px; font-size:13px; font-weight:600;
    text-decoration:none; cursor:pointer; border:1px solid transparent; transition:all .2s;
  }
  .btn-brass{background:var(--brass); color:#1B140A;}
  .btn-brass:hover{background:var(--brass-hover); transform:translateY(-1px); box-shadow:0 6px 16px rgba(185,139,78,0.28);}
  .btn-ghost-dark{border-color:rgba(231,236,227,0.25); color:var(--text-dark);}
  .btn-ghost-dark:hover{border-color:var(--brass); color:var(--brass);}
  .nav-toggle{display:none;}

  .eyebrow{
    font-family:'IBM Plex Mono',monospace; font-size:11.5px; font-weight:600;
    letter-spacing:2px; text-transform:uppercase; color:var(--brass); margin-bottom:14px;
    display:flex; align-items:center; gap:10px;
  }
  .eyebrow::before{content:""; width:16px; height:1px; background:var(--brass); display:inline-block;}
  .wrap{max-width:1160px; margin:0 auto; padding:0 48px;}
  @media (max-width:860px){
    .nav-center{display:none;}
    .wrap{padding:0 22px;} .nav{padding:14px 20px;}
  }

  /* ============ 1. HERO ============ */
  .hero{
    padding:104px 0 88px; position:relative; overflow:hidden;
    min-height:calc(100vh - var(--nav-h)); display:flex; align-items:center;
    isolation:isolate;
  }
  .hero::before{
    content:""; position:absolute; inset:0; pointer-events:none;
    background:radial-gradient(620px 360px at 88% -10%, rgba(185,139,78,0.16), transparent 60%);
  }
  .hero::after{
    content:""; position:absolute; right:-6%; top:18%; width:420px; height:420px; pointer-events:none;
    border:1px solid var(--ink-line); border-radius:50%;
    animation:orbital-drift 14s ease-in-out infinite;
  }
  .hero-grid{
    position:absolute; inset:0; z-index:-1; pointer-events:none; opacity:.32;
    background-image:linear-gradient(rgba(231,236,227,.055) 1px,transparent 1px),linear-gradient(90deg,rgba(231,236,227,.055) 1px,transparent 1px);
    background-size:56px 56px; mask-image:linear-gradient(90deg,transparent,black 26%,black 72%,transparent);
  }
  .hero-beam{position:absolute; z-index:-1; width:46vw; height:2px; right:-8vw; top:35%; transform:rotate(-27deg); background:linear-gradient(90deg,transparent,var(--brass),transparent); box-shadow:0 0 22px rgba(185,139,78,.65); opacity:.6; animation:beam-scan 8s ease-in-out infinite;}
  .hero-inner{max-width:1160px; position:relative; z-index:1; width:100%;}
  .hero-copy{max-width:720px;}
  .hero h1{
    font-size:clamp(34px,4.6vw,52px); line-height:1.16; font-weight:600;
    color:#F7F5EE; margin-bottom:22px; letter-spacing:-0.5px;
  }
  .hero h1 em{font-style:normal; color:var(--brass);}
  .hero p.lede{ font-size:16px; line-height:1.75; color:var(--text-dark-muted); max-width:600px; margin-bottom:40px; }

  .hero-stats{
    display:grid; grid-template-columns:repeat(4,1fr); gap:0;
    border-top:1px solid var(--ink-line); padding-top:28px; max-width:820px;
  }
  .hero-stats .stat + .stat{border-left:1px solid var(--ink-line);}
  .hero-stats .stat{padding:0 24px;}
  .hero-stats .stat:first-child{padding-left:0;}
  .hero-stats .stat-num{font-family:'Fraunces',serif; font-size:clamp(22px,2.6vw,30px); font-weight:600; color:#F7F5EE;}
  .hero-stats .stat-num .mono-suffix{font-family:'IBM Plex Mono',monospace; font-size:15px; color:var(--brass); font-weight:600;}
  .hero-stats .stat-label{font-size:11.5px; color:var(--text-dark-muted); margin-top:6px; letter-spacing:.2px;}
  @media (max-width:720px){
    .hero-stats{grid-template-columns:repeat(2,1fr); row-gap:22px;}
    .hero-stats .stat:nth-child(3){border-left:none;}
  }

  .hero-panel{
    position:absolute; right:48px; top:50%; transform:translateY(-50%); width:min(340px,30vw);
    padding:18px; border:1px solid rgba(231,236,227,.16); border-radius:14px;
    background:linear-gradient(145deg,rgba(24,39,61,.86),rgba(10,18,29,.76));
    box-shadow:0 24px 80px rgba(0,0,0,.32),inset 0 1px rgba(255,255,255,.06); backdrop-filter:blur(15px);
  }
  .panel-top{display:flex;align-items:center;justify-content:space-between;padding-bottom:15px;border-bottom:1px solid var(--ink-line);}
  .panel-label{font:600 10px 'IBM Plex Mono',monospace;letter-spacing:1.2px;text-transform:uppercase;color:var(--text-dark-muted);}
  .live{display:flex;gap:6px;align-items:center;color:#b9d8b4;font:600 10px 'IBM Plex Mono',monospace;letter-spacing:.5px;}
  .live i{width:7px;height:7px;background:#65bd75;border-radius:50%;box-shadow:0 0 0 0 rgba(101,189,117,.65);animation:pulse-live 2s infinite;}
  .panel-value{font:600 29px 'Fraunces',serif;color:#f7f5ee;margin:20px 0 2px;letter-spacing:-.6px;}
  .panel-caption{font-size:11.5px;color:var(--text-dark-muted);}
  .panel-graph{height:100px;width:100%;margin:18px 0 14px;overflow:visible;}
  .panel-graph path{stroke-dasharray:360;stroke-dashoffset:360;animation:draw-line 2s .35s cubic-bezier(.2,.8,.2,1) forwards;}
  .panel-graph .area{opacity:0;animation:area-in 1s 1.3s forwards;}
  .panel-bottom{display:grid;grid-template-columns:1fr 1fr;border-top:1px solid var(--ink-line);padding-top:14px;gap:16px;}
  .panel-bottom span{display:block;font:500 10px 'IBM Plex Mono',monospace;color:var(--text-dark-muted);margin-bottom:5px;}
  .panel-bottom strong{font-size:13px;color:#f7f5ee;}
  .panel-bottom strong.up{color:#9fd199;}
  @keyframes orbital-drift{50%{transform:translate(-18px,14px) scale(1.06);opacity:.64}}
  @keyframes beam-scan{0%,100%{transform:rotate(-27deg) translateX(-14%);opacity:.2}50%{transform:rotate(-27deg) translateX(12%);opacity:.8}}
  @keyframes pulse-live{70%{box-shadow:0 0 0 8px rgba(101,189,117,0)}}
  @keyframes draw-line{to{stroke-dashoffset:0}}
  @keyframes area-in{to{opacity:1}}
  @media(max-width:1060px){.hero-panel{right:28px;width:290px}.hero-copy{max-width:610px}.hero-stats{max-width:620px}}
  @media(max-width:860px){.hero-panel{display:none}.hero-copy{max-width:720px}}

  /* ============ SECTION HELPERS ============ */
  .section{
    padding:88px 0; position:relative;
    min-height:calc(100vh - var(--nav-h)); display:flex; align-items:center;
  }
  .section > .wrap{ width:100%; }
  .section-dark{background:var(--ink);}
  .section-paper{background:var(--paper); color:var(--text-paper);}
  .section-paper .eyebrow{color:var(--brass-hover);}
  .section-paper .eyebrow::before{background:var(--brass-hover);}
  .section-head{max-width:620px; margin-bottom:48px; display:flex; align-items:flex-end; justify-content:space-between; gap:24px; flex-wrap:wrap;}
  .section-head h2{font-size:clamp(24px,3vw,32px); font-weight:600; letter-spacing:-.4px;}
  .section-dark .section-head h2{color:#F7F5EE;}
  .section-paper .section-head h2{color:var(--text-paper);}
  .section-head p{margin-top:14px; font-size:14.5px; line-height:1.7; max-width:520px;}
  .section-dark .section-head p{color:var(--text-dark-muted);}
  .section-paper .section-head p{color:var(--text-paper-muted);}

  /* ============ 2. VỀ CHÚNG TÔI (paper) ============ */
  .about-grid{display:grid; grid-template-columns:1fr 1fr; gap:40px; align-items:start;}
  @media (max-width:860px){ .about-grid{grid-template-columns:1fr;} }
  .about-grid p{font-size:14.5px; line-height:1.8; color:var(--text-paper-muted); margin-bottom:16px;}
  .about-figures{display:flex; flex-direction:column; gap:22px;}
  .about-figure{border-left:3px solid var(--brass); padding-left:18px; transition:transform .25s ease;}
  .about-figure:hover{transform:translateX(4px);}
  .about-figure h3{font-family:'Fraunces',serif; font-size:17px; font-weight:600; color:var(--text-paper); margin-bottom:6px;}
  .about-figure p{font-size:13px; color:var(--text-paper-muted); margin:0;}

  /* ============ 3. LĨNH VỰC HOẠT ĐỘNG (dark) ============ */
  .field-grid{display:grid; grid-template-columns:repeat(4,1fr); gap:1px; background:var(--ink-line); border:1px solid var(--ink-line); border-radius:10px; overflow:hidden;}
  @media (max-width:960px){ .field-grid{grid-template-columns:repeat(2,1fr);} }
  @media (max-width:560px){ .field-grid{grid-template-columns:1fr;} }
  .field-item{background:var(--ink); padding:30px 26px; transition:background .25s ease;}
  .field-item:hover{background:var(--ink-deep);}
  .field-num{font-family:'IBM Plex Mono',monospace; color:var(--brass); font-size:13px; margin-bottom:16px;}
  .field-item h3{font-family:'Fraunces',serif; font-size:17px; font-weight:600; color:#F7F5EE; margin-bottom:10px;}
  .field-item p{font-size:13px; line-height:1.65; color:var(--text-dark-muted);}

  /* ============ 4. BÁO CÁO TÀI CHÍNH (paper) ============ */
  .fin-layout{display:grid; grid-template-columns:1.1fr 1fr; gap:40px; align-items:stretch;}
  @media (max-width:960px){ .fin-layout{grid-template-columns:1fr;} }

  .fin-chart-card{
    background:var(--paper-card); border:1px solid var(--ledger-line); border-radius:12px;
    padding:30px 30px 22px; display:flex; flex-direction:column;
  }
  .fin-chart-head{display:flex; justify-content:space-between; align-items:baseline; margin-bottom:22px;}
  .fin-chart-head h3{font-family:'Fraunces',serif; font-size:16px; color:var(--text-paper); font-weight:600;}
  .fin-chart-head span{font-size:11.5px; color:var(--text-paper-muted); font-family:'IBM Plex Mono',monospace;}
  .fin-bars{display:flex; align-items:flex-end; gap:16px; height:180px; padding-top:8px;}
  .fin-bar-col{flex:1; display:flex; flex-direction:column; align-items:center; gap:10px; height:100%; justify-content:flex-end;}
  .fin-bar{width:100%; max-width:34px; background:linear-gradient(180deg,var(--brass),var(--brass-hover)); border-radius:4px 4px 0 0; height:0; transition:height 1.1s cubic-bezier(.2,.8,.2,1);}
  .fin-bar-label{font-size:11px; color:var(--text-paper-muted); font-family:'IBM Plex Mono',monospace;}
  .fin-chart-note{margin-top:20px; padding-top:16px; border-top:1px solid var(--ledger-line); font-size:12px; color:var(--text-paper-muted); display:flex; align-items:center; gap:8px;}
  .fin-chart-note .dot{width:7px; height:7px; border-radius:50%; background:var(--brass); flex-shrink:0;}

  .fin-metrics{display:grid; grid-template-columns:1fr 1fr; gap:1px; background:var(--ledger-line); border:1px solid var(--ledger-line); border-radius:12px; overflow:hidden;}
  .fin-metric{background:var(--paper-card); padding:22px 24px;}
  .fin-metric span{display:block; font-size:12px; color:var(--text-paper-muted); margin-bottom:8px;}
  .fin-metric strong{font-family:'Fraunces',serif; font-size:22px; color:var(--text-paper); font-weight:600;}
  .fin-metric .delta{display:inline-flex; align-items:center; gap:4px; font-family:'IBM Plex Mono',monospace; font-size:11.5px; color:#3F7A4E; margin-left:8px; font-weight:600;}
  .fin-metric .delta svg{width:11px; height:11px;}
  .fin-metric .delta.down{color:#B84C4C;}
  .fin-metric strong.val-up{color:#3F7A4E;}
  .fin-metric strong.val-down{color:#B84C4C;}

  .fin-cta{margin-top:20px; padding:16px 20px; background:var(--paper-card); border:1px dashed var(--ledger-line); border-radius:10px; font-size:12.5px; color:var(--text-paper-muted); display:flex; align-items:center; justify-content:space-between; gap:12px; flex-wrap:wrap;}
  .fin-cta a{color:var(--brass-hover); font-weight:600; text-decoration:none; white-space:nowrap;}
  .fin-cta a:hover{text-decoration:underline;}

  .sh-layout{display:grid; grid-template-columns:0.85fr 1.15fr; gap:40px; align-items:center;}
  @media (max-width:960px){ .sh-layout{grid-template-columns:1fr;} }
  .sh-pie-wrap{display:flex; flex-direction:column; align-items:center; gap:18px;}
  .sh-pie{width:240px; height:240px; border-radius:50%; box-shadow:0 0 0 1px var(--ledger-line), 0 18px 40px -20px rgba(60,45,20,.35); position:relative;}
  .sh-pie::after{content:"";position:absolute; inset:26%; background:var(--paper-card); border-radius:50%; box-shadow:inset 0 0 0 1px var(--ledger-line);}
  .sh-pie-center{position:absolute; inset:26%; display:flex; flex-direction:column; align-items:center; justify-content:center; text-align:center; z-index:1;}
  .sh-pie-center strong{font-family:'Fraunces',serif; font-size:15px; color:var(--text-paper); font-weight:600; line-height:1.2;}
  .sh-pie-center span{font-size:10.5px; color:var(--text-paper-muted); margin-top:2px;}
  .sh-table-card{background:var(--paper-card); border:1px solid var(--ledger-line); border-radius:12px; overflow:hidden;}
  .sh-table{width:100%; border-collapse:collapse; font-size:13px;}
  .sh-table th{text-align:left; font-size:10.5px; text-transform:uppercase; letter-spacing:.04em; color:var(--text-paper-muted); padding:12px 16px; border-bottom:1px solid var(--ledger-line); font-family:'IBM Plex Mono',monospace; font-weight:600;}
  .sh-table td{padding:11px 16px; border-bottom:1px solid var(--ledger-line); color:var(--text-paper);}
  .sh-table tr:last-child td{border-bottom:none;}
  .sh-table td.num{font-family:'IBM Plex Mono',monospace; text-align:right; white-space:nowrap;}
  .sh-dot{display:inline-block; width:9px; height:9px; border-radius:50%; margin-right:8px; flex-shrink:0; vertical-align:middle;}
  .sh-name{display:flex; align-items:center;}
  .sh-other td{color:var(--text-paper-muted); font-style:italic;}
  .sh-empty{padding:40px 20px; text-align:center; color:var(--text-paper-muted); font-size:13px;}

  /* ============ 5. TÀI LIỆU (dark) ============ */
  .doc-grid{display:grid; grid-template-columns:repeat(4,1fr); gap:18px;}
  @media (max-width:960px){ .doc-grid{grid-template-columns:repeat(2,1fr);} }
  @media (max-width:560px){ .doc-grid{grid-template-columns:1fr;} }
  .doc-card{
    background:var(--ink-deep); border:1px solid var(--ink-line); border-radius:10px;
    padding:26px 22px; transition:transform .25s ease, border-color .25s ease;
  }
  .doc-card:hover{transform:translateY(-4px); border-color:var(--brass);}
  .doc-icon{
    width:38px; height:38px; border-radius:8px; background:var(--brass-soft);
    display:flex; align-items:center; justify-content:center; margin-bottom:18px; color:var(--brass);
  }
  .doc-card h3{font-size:14.5px; font-weight:600; color:#F7F5EE; margin-bottom:8px;}
  .doc-card p{font-size:12.5px; line-height:1.6; color:var(--text-dark-muted);}
  .doc-note{margin-top:28px; font-size:12.5px; color:var(--text-dark-muted); display:flex; align-items:center; gap:8px;}
  .doc-note a{color:var(--brass); font-weight:600; text-decoration:none;}
  .doc-note a:hover{text-decoration:underline;}

  /* ============ FOOTER ============ */
  footer{background:var(--ink-deep); border-top:1px solid var(--ink-line); padding:64px 0 0;}
  .footer-grid{display:grid; grid-template-columns:1.4fr 1fr 1fr 1fr; gap:32px; padding-bottom:44px;}
  @media (max-width:760px){ .footer-grid{grid-template-columns:1fr 1fr;} }
  @media (max-width:480px){ .footer-grid{grid-template-columns:1fr;} }
  .footer-brand{display:flex; align-items:center; gap:10px; font-weight:700; font-size:15px; margin-bottom:14px;}
  .footer-brand .mark{
    width:22px; height:22px; border:1.5px solid var(--brass); border-radius:5px;
    display:flex; align-items:center; justify-content:center; color:var(--brass); font-size:11.5px; font-weight:700;
    font-family:'Fraunces',serif;
  }
  .footer-desc{font-size:12.5px; color:var(--text-dark-muted); max-width:280px; line-height:1.7;}
  .footer-col h4{font-size:11.5px; text-transform:uppercase; letter-spacing:1px; color:var(--text-dark-muted); margin-bottom:16px; font-weight:600;}
  .footer-col a{display:block; font-size:13.5px; color:var(--text-dark); text-decoration:none; margin-bottom:12px; transition:color .2s; width:fit-content;}
  .footer-col a:hover{color:var(--brass);}
  .footer-bottom{
    border-top:1px solid var(--ink-line); padding:22px 0 32px; font-size:11.5px;
    color:var(--text-dark-muted); display:flex; justify-content:space-between; flex-wrap:wrap; gap:8px;
  }
</style>
</head>
<body>

<nav class="nav">
  <a href="#home" class="nav-brand"><img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo" style="width:24px; height:24px; object-fit:contain; border-radius:4px;">VinScape</a>
  <div class="nav-center">
    <a href="#home" class="nav-link active" data-target="home">Trang chủ</a>
    <a href="#about" class="nav-link" data-target="about">Giới thiệu</a>
    <a href="#financial" class="nav-link" data-target="financial">Báo cáo tài chính</a>
    <a href="#shareholders" class="nav-link" data-target="shareholders">Cơ cấu cổ đông</a>
    <a href="#documents" class="nav-link" data-target="documents">Tài liệu</a>
  </div>
  <div class="nav-auth">
    <a class="link-signin" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
    <a class="btn btn-brass" href="${pageContext.request.contextPath}/register">Đăng ký</a>
  </div>
</nav>

<!-- ============ 1. HOME / HERO ============ -->
<section class="hero" id="home">
  <div class="hero-grid" aria-hidden="true"></div>
  <div class="hero-beam" aria-hidden="true"></div>
  <div class="wrap hero-inner reveal">
    <div class="eyebrow">VinScape · Từ 2004</div>
    <h1 class="display">Doanh nghiệp kinh tế<br><em>hàng đầu Việt Nam.</em></h1>
    <p class="lede">
      VinScape là tập đoàn kinh tế đa ngành hoạt động trong lĩnh vực sản xuất, thương mại và tài chính,
      lấy sự minh bạch với cổ đông làm nền tảng cho mọi quyết định quản trị doanh nghiệp.
    </p>

    <div class="hero-stats">
      <div class="stat">
        <div class="stat-num" data-count="21">21<span class="mono-suffix">+</span></div>
        <div class="stat-label">Năm hoạt động</div>
      </div>
      <div class="stat">
        <div class="stat-num" data-count="4800">4.800<span class="mono-suffix">+</span></div>
        <div class="stat-label">Cổ đông</div>
      </div>
      <div class="stat">
        <div class="stat-num" data-count="12">12<span class="mono-suffix">/63</span></div>
        <div class="stat-label">Tỉnh, thành phố</div>
      </div>
      <div class="stat">
        <div class="stat-num" data-count="3200">3.200<span class="mono-suffix">+</span></div>
        <div class="stat-label">Nhân sự</div>
      </div>
    </div>
    <aside class="hero-panel" aria-label="Tổng quan tài chính VinScape">
      <div class="panel-top"><span class="panel-label">Tổng quan vận hành</span><span class="live"><i></i>LIVE</span></div>
      <div class="panel-value">
        <c:choose>
            <c:when test="${not empty homeLatest and not empty homeLatest.revenue}"><fmt:formatNumber value="${homeLatest.revenue}" type="number" maxFractionDigits="0"/></c:when>
            <c:otherwise>--</c:otherwise>
        </c:choose>
      </div>
      <div class="panel-caption">Doanh thu quý gần nhất<c:if test="${not empty homeLatest}"> · Q<c:out value="${homeLatest.reportQuarter}"/>/<c:out value="${homeLatest.reportYear}"/></c:if></div>
      <svg class="panel-graph" viewBox="0 0 300 100" preserveAspectRatio="none" aria-hidden="true">
        <defs><linearGradient id="panelFill" x1="0" x2="0" y1="0" y2="1"><stop stop-color="#B98B4E" stop-opacity=".34"/><stop offset="1" stop-color="#B98B4E" stop-opacity="0"/></linearGradient></defs>
        <path class="area" d="M0 83 C24 76 32 71 52 75 S78 57 99 65 S127 44 150 51 S181 27 205 39 S237 22 260 29 S281 13 300 17 L300 100 L0 100 Z" fill="url(#panelFill)"/>
        <path d="M0 83 C24 76 32 71 52 75 S78 57 99 65 S127 44 150 51 S181 27 205 39 S237 22 260 29 S281 13 300 17" fill="none" stroke="#D6AE70" stroke-width="2" vector-effect="non-scaling-stroke"/>
      </svg>
      <div class="panel-bottom">
        <div>
            <span>LN sau thuế</span>
            <strong>
                <c:choose>
                    <c:when test="${not empty homeLatest and not empty homeLatest.profitAfterTax}"><fmt:formatNumber value="${homeLatest.profitAfterTax}" type="number" maxFractionDigits="0"/></c:when>
                    <c:otherwise>--</c:otherwise>
                </c:choose>
            </strong>
        </div>
        <div><span>Trạng thái</span><strong class="up">Ổn định</strong></div>
      </div>
    </aside>
  </div>
</section>

<!-- ============ 2. GIỚI THIỆU ============ -->
<section class="section section-paper" id="about">
  <div class="wrap">
    <div class="section-head reveal">
      <div>
        <div class="eyebrow">Giới thiệu</div>
        <h2 class="display">Một hành trình được xây dựng bằng niềm tin</h2>
      </div>
    </div>

    <div class="about-grid">
      <div class="reveal">
        <p>
          VinScape được thành lập với mục tiêu trở thành một tập đoàn kinh tế cổ phần hoạt động
          minh bạch, nơi mọi cổ đông — dù lớn hay nhỏ — đều được tiếp cận đầy đủ thông tin về
          tình hình tài chính, các quyết định của hội đồng quản trị và quyền lợi sở hữu của mình.
        </p>
        <p>
          Chúng tôi tin rằng một doanh nghiệp phát triển bền vững phải đi cùng với một sổ cổ đông
          rõ ràng, nơi từng đợt phát hành, từng lần chuyển nhượng và từng kỳ báo cáo tài chính
          đều được ghi nhận đầy đủ, có thể tra cứu lại bất cứ lúc nào.
        </p>
      </div>

      <div class="about-figures reveal">
        <div class="about-figure">
          <h3>Quản trị minh bạch</h3>
          <p>Mọi nghị quyết đại hội đồng cổ đông đều được đưa ra biểu quyết công khai, đúng tỷ lệ sở hữu.</p>
        </div>
        <div class="about-figure">
          <h3>Cổ đông là trung tâm</h3>
          <p>Hệ thống cổng thông tin điện tử giúp cổ đông theo dõi quyền sở hữu mọi lúc, không cần chờ đại hội thường niên.</p>
        </div>
        <div class="about-figure">
          <h3>Phát triển ổn định</h3>
          <p>Ưu tiên tăng trưởng bền vững, công bố báo cáo tài chính đầy đủ theo từng quý.</p>
        </div>
      </div>
    </div>
  </div>
</section>

<!-- ============ 3. LĨNH VỰC HOẠT ĐỘNG ============ -->
<section class="section section-dark">
  <div class="wrap">
    <div class="section-head reveal">
      <div>
        <div class="eyebrow">Lĩnh vực hoạt động</div>
        <h2 class="display">Bốn trụ cột kinh doanh</h2>
      </div>
    </div>

    <div class="field-grid">
      <div class="field-item reveal">
        <div class="field-num mono">01</div>
        <h3>Sản xuất</h3>
        <p>Đầu tư dây chuyền sản xuất, kiểm soát chất lượng theo tiêu chuẩn ngành.</p>
      </div>
      <div class="field-item reveal d1">
        <div class="field-num mono">02</div>
        <h3>Bất động sản</h3>
        <p>Phát triển hạ tầng và các dự án khu đô thị, thương mại tại nhiều tỉnh thành.</p>
      </div>
      <div class="field-item reveal d2">
        <div class="field-num mono">03</div>
        <h3>Tài chính & Đầu tư</h3>
        <p>Quản lý danh mục đầu tư dài hạn, hợp tác cùng các định chế tài chính trong nước.</p>
      </div>
      <div class="field-item reveal d3">
        <div class="field-num mono">04</div>
        <h3>Thương mại & Dịch vụ</h3>
        <p>Phân phối sản phẩm và cung cấp dịch vụ hậu mãi qua hệ thống đối tác trong, ngoài nước.</p>
      </div>
    </div>
  </div>
</section>

<!-- ============ 4. BÁO CÁO TÀI CHÍNH ============ -->
<section class="section section-paper" id="financial">
  <div class="wrap">
    <div class="section-head reveal">
      <div>
        <div class="eyebrow">Báo cáo tài chính</div>
        <h2 class="display">Kết quả kinh doanh minh bạch</h2>
        <p>Số liệu tổng hợp theo từng quý, được công bố đầy đủ tới cổ đông ngay khi được hội đồng quản trị phê duyệt.</p>
      </div>
    </div>

    <div class="fin-layout">
      <div class="fin-chart-card reveal">
        <div class="fin-chart-head">
          <h3>Doanh thu theo quý</h3>
          <span class="mono"><c:out value="${homeYearRange}"/></span>
        </div>
        <div class="fin-bars" id="finBars">
          <c:forEach var="b" items="${homeBars}">
              <div class="fin-bar-col"><div class="fin-bar" data-h="${b.height}"></div><div class="fin-bar-label"><c:out value="${b.label}"/></div></div>
          </c:forEach>
          <c:if test="${empty homeBars}">
              <div class="fin-bar-col" style="opacity:.4;"><div class="fin-bar" data-h="4"></div><div class="fin-bar-label">--</div></div>
          </c:if>
        </div>
        <div class="fin-chart-note"><span class="dot"></span>
            <c:choose>
                <c:when test="${not empty homeBars}">Doanh thu công bố theo từng quý, cập nhật trực tiếp từ báo cáo tài chính.</c:when>
                <c:otherwise>Chưa có báo cáo tài chính nào được công bố.</c:otherwise>
            </c:choose>
        </div>
      </div>

      <div class="reveal">
        <div class="fin-metrics">
          <div class="fin-metric">
            <span>Doanh thu (quý gần nhất)</span>
            <c:choose>
                <c:when test="${not empty homeLatest and not empty homeLatest.revenue}">
                    <c:choose>
                        <c:when test="${not empty homeRevenueDeltaPct and homeRevenueDeltaPct > 0}">
                            <strong class="val-up"><fmt:formatNumber value="${homeLatest.revenue}" type="number" maxFractionDigits="0"/></strong><span class="delta up"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="12" y1="19" x2="12" y2="5"></line><polyline points="5 12 12 5 19 12"></polyline></svg><fmt:formatNumber value="${homeRevenueDeltaPct}" maxFractionDigits="1"/>%</span>
                        </c:when>
                        <c:when test="${not empty homeRevenueDeltaPct and homeRevenueDeltaPct < 0}">
                            <strong class="val-down"><fmt:formatNumber value="${homeLatest.revenue}" type="number" maxFractionDigits="0"/></strong><span class="delta down"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="12" y1="5" x2="12" y2="19"></line><polyline points="19 12 12 19 5 12"></polyline></svg><fmt:formatNumber value="${homeRevenueDeltaPct}" maxFractionDigits="1"/>%</span>
                        </c:when>
                        <c:otherwise>
                            <strong><fmt:formatNumber value="${homeLatest.revenue}" type="number" maxFractionDigits="0"/></strong>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise><strong>--</strong></c:otherwise>
            </c:choose>
          </div>
          <div class="fin-metric">
            <span>Lợi nhuận sau thuế</span>
            <c:choose>
                <c:when test="${not empty homeLatest and not empty homeLatest.profitAfterTax}">
                    <c:choose>
                        <c:when test="${not empty homeProfitDeltaPct and homeProfitDeltaPct > 0}">
                            <strong class="val-up"><fmt:formatNumber value="${homeLatest.profitAfterTax}" type="number" maxFractionDigits="0"/></strong><span class="delta up"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="12" y1="19" x2="12" y2="5"></line><polyline points="5 12 12 5 19 12"></polyline></svg><fmt:formatNumber value="${homeProfitDeltaPct}" maxFractionDigits="1"/>%</span>
                        </c:when>
                        <c:when test="${not empty homeProfitDeltaPct and homeProfitDeltaPct < 0}">
                            <strong class="val-down"><fmt:formatNumber value="${homeLatest.profitAfterTax}" type="number" maxFractionDigits="0"/></strong><span class="delta down"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="12" y1="5" x2="12" y2="19"></line><polyline points="19 12 12 19 5 12"></polyline></svg><fmt:formatNumber value="${homeProfitDeltaPct}" maxFractionDigits="1"/>%</span>
                        </c:when>
                        <c:otherwise>
                            <strong><fmt:formatNumber value="${homeLatest.profitAfterTax}" type="number" maxFractionDigits="0"/></strong>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise><strong>--</strong></c:otherwise>
            </c:choose>
          </div>
          <div class="fin-metric">
            <span>ROE</span>
            <c:choose>
                <c:when test="${not empty homeLatest and not empty homeLatest.roe}">
                    <strong><fmt:formatNumber value="${homeLatest.roe}" maxFractionDigits="1"/>%</strong>
                </c:when>
                <c:otherwise><strong>--</strong></c:otherwise>
            </c:choose>
          </div>
          <div class="fin-metric">
            <span>EPS</span>
            <c:choose>
                <c:when test="${not empty homeLatest and not empty homeLatest.eps}">
                    <strong><fmt:formatNumber value="${homeLatest.eps}" maxFractionDigits="0"/></strong>
                </c:when>
                <c:otherwise><strong>--</strong></c:otherwise>
            </c:choose>
          </div>
        </div>
        <div class="fin-cta">
          <span>Xem đầy đủ báo cáo tài chính chi tiết theo từng kỳ trong cổng cổ đông.</span>
          <a href="${pageContext.request.contextPath}/login">Đăng nhập để xem →</a>
        </div>
      </div>
    </div>
  </div>
</section>

<!-- ============ 4b. CO CAU CO DONG ============ -->
<section class="section section-paper" id="shareholders">
  <div class="wrap">
    <div class="section-head reveal">
      <div>
        <div class="eyebrow">Cơ cấu sở hữu</div>
        <h2 class="display">Top 10 cổ đông lớn nhất</h2>
        <p>Tỷ lệ sở hữu được tính trên tổng số cổ phần đang lưu hành, cập nhật trực tiếp từ dữ liệu hệ thống.</p>
      </div>
    </div>

    <c:choose>
      <c:when test="${shTotalShares > 0 and not empty shTopRows}">
        <div class="sh-layout">
          <div class="sh-pie-wrap reveal">
            <div class="sh-pie" style="background:${shPieGradient};">
              <div class="sh-pie-center">
                <strong><fmt:formatNumber value="${shTotalShares}" type="number"/></strong>
                <span>tổng cổ phần</span>
              </div>
            </div>
          </div>

          <div class="sh-table-card reveal">
            <table class="sh-table">
              <thead>
                <tr><th>Cổ đông</th><th class="num">Tỷ lệ</th><th class="num">Số cổ phần</th></tr>
              </thead>
              <tbody>
                <c:forEach var="row" items="${shTopRows}">
                  <tr>
                    <td><span class="sh-name"><span class="sh-dot" style="background:${row.color};"></span><c:out value="${row.name}"/></span></td>
                    <td class="num"><fmt:formatNumber value="${row.pct}" maxFractionDigits="2"/>%</td>
                    <td class="num"><fmt:formatNumber value="${row.quantity}" type="number"/></td>
                  </tr>
                </c:forEach>
                <c:if test="${shOtherQuantity > 0}">
                  <tr class="sh-other">
                    <td><span class="sh-name"><span class="sh-dot" style="background:#e5decf;"></span>Cổ đông khác</span></td>
                    <td class="num"><fmt:formatNumber value="${shOtherPct}" maxFractionDigits="2"/>%</td>
                    <td class="num"><fmt:formatNumber value="${shOtherQuantity}" type="number"/></td>
                  </tr>
                </c:if>
              </tbody>
            </table>
          </div>
        </div>
      </c:when>
      <c:otherwise>
        <div class="sh-empty">Chưa có dữ liệu cổ phần để hiển thị cơ cấu sở hữu.</div>
      </c:otherwise>
    </c:choose>
  </div>
</section>

<!-- ============ 5. TÀI LIỆU ============ -->
<section class="section section-dark" id="documents">
  <div class="wrap">
    <div class="section-head reveal">
      <div>
        <div class="eyebrow">Tài liệu</div>
        <h2 class="display">Hồ sơ & tài liệu doanh nghiệp</h2>
        <p>Toàn bộ hồ sơ pháp lý và tài liệu quản trị được lưu trữ có hệ thống, cấp quyền truy cập theo từng nhóm cổ đông.</p>
      </div>
    </div>

    <div class="doc-grid">
      <div class="doc-card reveal">
        <div class="doc-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline></svg>
        </div>
        <h3>Điều lệ công ty</h3>
        <p>Văn bản điều lệ tổ chức và hoạt động, cập nhật theo nghị quyết mới nhất.</p>
      </div>
      <div class="doc-card reveal d1">
        <div class="doc-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"></line><line x1="12" y1="20" x2="12" y2="4"></line><line x1="6" y1="20" x2="6" y2="14"></line></svg>
        </div>
        <h3>Báo cáo thường niên</h3>
        <p>Tổng kết hoạt động kinh doanh và định hướng phát triển hằng năm.</p>
      </div>
      <div class="doc-card reveal d2">
        <div class="doc-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
        </div>
        <h3>Nghị quyết ĐHĐCĐ</h3>
        <p>Các nghị quyết được thông qua tại đại hội đồng cổ đông thường niên và bất thường.</p>
      </div>
      <div class="doc-card reveal d3">
        <div class="doc-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>
        </div>
        <h3>Công bố thông tin</h3>
        <p>Thông báo định kỳ và bất thường theo quy định pháp luật hiện hành.</p>
      </div>
    </div>

    <div class="doc-note reveal">
      Cổ đông đã có tài khoản có thể tải toàn bộ tài liệu trong hệ thống —
      <a href="${pageContext.request.contextPath}/login">đăng nhập cổng cổ đông</a>
    </div>
  </div>
</section>

<!-- ============ FOOTER ============ -->
<footer>
  <div class="wrap">
    <div class="footer-grid">
      <div>
        <div class="footer-brand"><img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo" style="width:24px; height:24px; object-fit:contain; border-radius:4px;">VinScape</div>
        <p class="footer-desc">Tập đoàn kinh tế đa ngành hoạt động trong lĩnh vực sản xuất, bất động sản,
           tài chính và dịch vụ — quản trị minh bạch, đồng hành cùng cổ đông.</p>
      </div>
      <div class="footer-col">
        <h4>Doanh nghiệp</h4>
        <a href="#home">Trang chủ</a>
        <a href="#about">Giới thiệu</a>
      </div>
      <div class="footer-col">
        <h4>Thông tin</h4>
        <a href="#financial">Báo cáo tài chính</a>
        <a href="#shareholders">Cơ cấu cổ đông</a>
        <a href="#documents">Tài liệu</a>
      </div>
      <div class="footer-col">
        <h4>Cổng cổ đông</h4>
        <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
        <a href="${pageContext.request.contextPath}/register">Đăng ký tài khoản</a>
      </div>
    </div>
    <div class="footer-bottom">
      <span>© 2026 VinScape Group</span>
      <span>Đồ án tốt nghiệp — Hệ thống Quản lý Cổ đông</span>
    </div>
  </div>
</footer>

<script>
  (function(){
    var reduceMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    var items = document.querySelectorAll('.reveal');
    if (!('IntersectionObserver' in window)) {
      items.forEach(function(el){ el.classList.add('in'); });
    } else {
      var io = new IntersectionObserver(function(entries){
        entries.forEach(function(entry){
          if (entry.isIntersecting) {
            entry.target.classList.add('in');
            io.unobserve(entry.target);
          }
        });
      }, { threshold: 0.15 });
      items.forEach(function(el){ io.observe(el); });
    }

    // Animate financial bars once visible
    var barsWrap = document.getElementById('finBars');
    if (barsWrap) {
      var barIo = new IntersectionObserver(function(entries){
        entries.forEach(function(entry){
          if (entry.isIntersecting) {
            entry.target.querySelectorAll('.fin-bar').forEach(function(bar){
              bar.style.height = bar.dataset.h + '%';
            });
            barIo.unobserve(entry.target);
          }
        });
      }, { threshold: 0.35 });
      barIo.observe(barsWrap);
    }

    // Count-up starts only after the opening reveal, keeping the first impression calm.
    if (!reduceMotion) {
      window.setTimeout(function(){
        document.querySelectorAll('[data-count]').forEach(function(node){
          var target = Number(node.dataset.count || 0), started = performance.now(), duration = 1150;
          var tick = function(now){
            var progress = Math.min((now - started) / duration, 1);
            var eased = 1 - Math.pow(1 - progress, 3);
            var value = Math.round(target * eased).toLocaleString('vi-VN');
            if (node.firstChild) node.firstChild.nodeValue = value;
            if (progress < 1) requestAnimationFrame(tick);
          };
          requestAnimationFrame(tick);
        });
      }, 180);
    }

    // Active nav link highlighting on scroll
    var navLinks = document.querySelectorAll('.nav-link');
    var sections = Array.prototype.map.call(navLinks, function(a){
      return document.getElementById(a.dataset.target);
    }).filter(Boolean);

    if (sections.length && 'IntersectionObserver' in window) {
      var navIo = new IntersectionObserver(function(entries){
        entries.forEach(function(entry){
          if (entry.isIntersecting) {
            navLinks.forEach(function(a){ a.classList.remove('active'); });
            var active = document.querySelector('.nav-link[data-target="' + entry.target.id + '"]');
            if (active) active.classList.add('active');
          }
        });
      }, { threshold: 0.4, rootMargin: '-80px 0px -50% 0px' });
      sections.forEach(function(s){ navIo.observe(s); });
    }
  })();
</script>

<jsp:include page="/WEB-INF/views/common/ai-chatbox.jsp" />

</body>
</html>
