<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>He thong Quan ly Co dong</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="navbar">
    <div>
        <span class="brand">SnapshotDB</span>
        <c:if test="${sessionScope.role == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/app/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/app/admin/shareholders">Co dong</a>
            <a href="${pageContext.request.contextPath}/app/admin/share-adjust">Dieu chinh CP</a>
            <a href="${pageContext.request.contextPath}/app/admin/transfer-approval">Duyet chuyen nhuong</a>
            <a href="${pageContext.request.contextPath}/app/admin/share-issue">Phat hanh/Co tuc</a>
            <a href="${pageContext.request.contextPath}/app/admin/resolution">Nghi quyet</a>
            <a href="${pageContext.request.contextPath}/app/admin/documents">Tai lieu</a>
            <a href="${pageContext.request.contextPath}/app/admin/financial-reports/manage">Bao cao TC</a>
        </c:if>
        <c:if test="${sessionScope.role == 'IT'}">
            <a href="${pageContext.request.contextPath}/app/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/app/admin/user-management">Quan ly tai khoan</a>
        </c:if>
        <c:if test="${sessionScope.role == 'SHAREHOLDER'}">
            <a href="${pageContext.request.contextPath}/app/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/app/shareholder/sign">Ky nhan co phan</a>
            <a href="${pageContext.request.contextPath}/app/shareholder/vote">Bieu quyet</a>
            <a href="${pageContext.request.contextPath}/app/shareholder/transfer-request">Chuyen nhuong</a>
            <a href="${pageContext.request.contextPath}/app/shareholder/financial-reports">Bao cao TC</a>
        </c:if>
    </div>
    <div>
        <c:if test="${not empty sessionScope.username}">
            <span style="margin-right:14px; color:#94a3b8; font-size:13px;">
                <c:out value="${sessionScope.username}"/> (<c:out value="${sessionScope.role}"/>)
            </span>
            <a href="${pageContext.request.contextPath}/app/logout">Dang xuat</a>
        </c:if>
    </div>
</div>
<div class="container">
