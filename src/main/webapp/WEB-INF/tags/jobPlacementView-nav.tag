<%--
  jobPlacementView-nav.tag - 알선 관리 사이드 네비게이션 태그
  알선(잡플레이스먼트) 모듈의 메인 메뉴 네비게이션을 렌더링한다. pageController 속성값에 따라
  현재 활성 메뉴(참여자 목록, 상세 정보 등)를 하이라이트 표시한다.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="pageController"%>
<!-- Navigation -->
<nav role="navigation" aria-label="메인 메뉴" class="container">
    <div class="main-nav">
        <ul class="nav-links">
            <%--<li>
                <a href="placementMain" class="nav-link ${pageController eq 'main' ? 'active' : ''}">
                    <i class="bi bi-person-fill-gear"></i> 참여자 작성
                </a>
            </li>--%>
            <c:if test="${pageController eq 'list'}">
                <li>
                    <a href="#" class="nav-link active">
<%--                        <i class="bi bi-building"></i> --%>
                        <i class="bi bi-people-fill"></i> 참여자 확인 - 목록
                    </a>
                </li>
            </c:if>
            <c:if test="${pageController eq 'detail'}">
                <li>
                    <a href="#" class="nav-link active">
                        <i class="bi bi-file-text"></i> 상세 정보
                    </a>
                </li>
            </c:if>
        </ul>
    </div>
</nav>