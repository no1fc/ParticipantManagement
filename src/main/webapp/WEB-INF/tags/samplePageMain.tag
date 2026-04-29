<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="content_header_name" %>

<!--begin::App Main-->
<main class="app-main">
    <!--begin::App Content Header-->
    <div class="app-content-header">
        <!--begin::Container-->
        <div class="container-fluid">
            <!--begin::Row-->
            <div class="row">
                <div class="col-sm-6"><h3 class="mb-0"><%=content_header_name%></h3></div>
            </div>
            <!--end::Row-->
        </div>
        <!--end::Container-->
    </div>
    <!--end::App Content Header-->
    <!--begin::App Content-->
    <div class="app-content">
        <!--begin::Main content-->
        <!-- 필요 본문 내용은 이쪽에 만들어 주시면 됩니다. -->
        <div class="container-fluid">
            <div class="row">
                <div class="col-md-12">
                    <div class="card">
                        <div class="card-body">
                            <table id="samplePageMainTable" class="table table-bordered table-hover">
                                <thead>
                                    <tr>
                                        <th>일련번호</th>
                                        <th>입력일</th>
                                        <th>진행단계</th>
                                        <th>최근상담일</th>
                                        <th>참여자</th>
                                        <th>주민번호</th>
                                        <th>2유형</th>
                                        <th>마감여부</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="i" begin="1" end="1">
                                    <tr>

                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!--end::Main content-->
    </div>
    <!--end::App Content-->
</main>
<!--end::App Main-->
