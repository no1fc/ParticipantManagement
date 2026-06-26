<%--
  hrGnb.tag - 입퇴사자관리(HR) 전용 글로벌 네비게이션
  HR은 잡모아 상담사 앱과 분리된 독립 화면(데모)이다. 잡모아 gnb를 쓰지 않고
  HR 메뉴(부서/계정/사이트접속)만 가진 자체 사이드바/헤더를 렌더링한다.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="active" %>
<%@ attribute name="sub_header" %>

<!--begin::Header-->
<nav class="app-header navbar navbar-expand bg-body">
    <div class="container-fluid">
        <ul class="navbar-nav">
            <li class="nav-item">
                <a class="nav-link" data-lte-toggle="sidebar" href="#" role="button"><i class="bi bi-list"></i></a>
            </li>
            <li class="nav-item d-none d-md-block"><div class="nav-link fw-semibold">입퇴사자관리(HR)</div></li>
            <li class="nav-item d-none d-md-block"><div class="nav-link text-muted">${sub_header}</div></li>
        </ul>
        <ul class="navbar-nav ms-auto">
            <li class="nav-item">
                <a class="nav-link" href="#" data-lte-toggle="fullscreen">
                    <i data-lte-icon="maximize" class="bi bi-arrows-fullscreen"></i>
                    <i data-lte-icon="minimize" class="bi bi-fullscreen-exit" style="display: none"></i>
                </a>
            </li>
            <li class="nav-item dropdown user-menu">
                <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
                    <span class="d-none d-md-inline">로그인 정보</span>
                </a>
                <ul class="dropdown-menu dropdown-menu-lg dropdown-menu-end">
                    <li class="text-bg-primary text-center">
                        <p>입퇴사자관리(HR) <small class="text-center">(${HR_LOGIN_DATA.branch})</small></p>
                    </li>
                    <li class="user-body">
                        <div class="row">
                            <div class="col-6 text-center">담당자<br/>${HR_LOGIN_DATA.userName}</div>
                            <div class="col-6 text-center">권한<br/>${HR_LOGIN_DATA.role}</div>
                        </div>
                    </li>
                    <li class="user-footer">
                        <a href="/hr/logout" class="btn btn-default btn-flat float-end">로그아웃</a>
                    </li>
                </ul>
            </li>
        </ul>
    </div>
</nav>
<!--end::Header-->

<!--begin::Sidebar-->
<aside class="app-sidebar shadow" data-bs-theme="dark">
    <div class="sidebar-brand" style="height: 90px;">
        <a href="/hr/departments" class="brand-link h-100 d-flex align-items-center justify-content-center">
            <span class="brand-text fw-light" style="color:#fff;">
                <i class="bi bi-person-vcard"></i> 입퇴사자관리(HR)
            </span>
        </a>
    </div>
    <%-- 역할별 메뉴 게이팅: 로그인 시 적재된 세션 HR_MENU_ACCESS(메뉴코드→접근레벨) 기준.
         맵이 비었으면(시드 미적용·구세션) 전 메뉴 노출하여 기존 동작 보존(네비 깨짐 방지). --%>
    <c:set var="hrMenuEmpty" value="${empty HR_MENU_ACCESS}" />
    <div class="sidebar-wrapper" style="max-height: 700px; overflow: hidden;">
        <nav class="mt-2">
            <ul class="nav sidebar-menu flex-column" data-lte-toggle="treeview" role="menu" data-accordion="false">
                <c:if test="${hrMenuEmpty or not empty HR_MENU_ACCESS['HR_DEPARTMENT']}">
                <li class="nav-item">
                    <a href="/hr/departments" class="nav-link ${active eq 'departments' ? 'active' : ''}">
                        <i class="nav-icon bi bi-diagram-3"></i>
                        <p>부서/조직 관리</p>
                    </a>
                </li>
                </c:if>
                <c:if test="${hrMenuEmpty or not empty HR_MENU_ACCESS['HR_ACCOUNT']}">
                <li class="nav-item">
                    <a href="/hr/accounts" class="nav-link ${active eq 'accounts' ? 'active' : ''}">
                        <i class="nav-icon bi bi-key"></i>
                        <p>계정 관리</p>
                    </a>
                </li>
                </c:if>
                <c:if test="${hrMenuEmpty or not empty HR_MENU_ACCESS['HR_SITE_ACCESS']}">
                <li class="nav-item">
                    <a href="/hr/site-access" class="nav-link ${active eq 'site-access' ? 'active' : ''}">
                        <i class="nav-icon bi bi-shield-check"></i>
                        <p>사이트 접속·권한 관리</p>
                    </a>
                </li>
                </c:if>
            </ul>
        </nav>
    </div>
</aside>
<!--end::Sidebar-->
