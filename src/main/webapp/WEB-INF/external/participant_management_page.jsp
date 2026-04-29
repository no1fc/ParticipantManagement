<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta charset="UTF-8">
    <title>참여자 정보</title>
    <!-- Jobmoa 로고 탭 이미지 -->
    <link rel="icon" href="/img/JobmoaLogo.png"/>
    <link rel="apple-touch-icon" href="/img/JobmoaLogo.png"/>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px auto;
            min-height: 800px;
            overflow-y: auto;
            width: 95%;
            max-width: 1920px;
            min-width: 1260px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
            table-layout: fixed;
        }

        th {
            background-color: #f4f4f4;
            font-weight: bold;
            position: sticky; /* 헤더 고정 */
            top: 127px;
            z-index: 100;
        }

        th, td {
            border: 1px solid #ddd;
            text-align: center;
            padding: 20px;
            word-wrap: break-word; /* 긴 텍스트 줄바꿈 */
        }

        th:nth-child(1), th:nth-child(2), th:nth-child(3), th:nth-child(4),
        th:nth-child(5), th:nth-child(6), th:nth-child(7), th:nth-child(8),
        th:nth-child(9), th:nth-child(10), th:nth-child(11), th:nth-child(12){
            width: 10%;
        }

        tr:nth-child(even) {
            background-color: #fbfbfb;
        }

        .header {
            background-color: #ffffff;
            text-align: center;
            font-size: 20px;
            position: sticky; /* 헤더 고정 */
            top: 0;
            z-index: 100;
        }

        .header-title{
            font-size: 25px;
            font-weight: bold;
            margin-top: 10px;
            margin-bottom: 10px;
        }

        .header-subTitle {
            font-size: 15px;
            font-weight: bold;
        }

        /* Page Rows Select Start */
        .pageRowSelectDiv{
            display: flex;
            justify-content: end;
            align-items: end;
            margin-bottom: 10px;
            text-align: center;
        }

        .pageRowSelectDiv select{
            width: 100px;
            height: 30px;
            border-radius: 5px;
            border: 1px solid #ddd;
            font-size: 15px;
            font-weight: bold;
        }
        /* Page Rows Select Start */


        /* Pagination Start */
        .paginationDiv {
            height: 50px;
            margin: 30px auto;
        }

        .paginationUl {
            display: flex;
            justify-content: center;
            margin-top: 10px;
            list-style: none;
        }

        .page-item{
            display: inline-block;
            background-color: #f3ecdc;
            cursor: pointer;
            transition: background-color 0.3s;
            text-decoration: none;
            color: #000;
            font-size: 15px;
            font-weight: bold;
            text-align: center;
            width: 30px;
            height: 30px;
        }

        .disabled{
            opacity: 0.5;
            cursor: not-allowed;
            pointer-events: none;
        }

        .page-link{
            display: inline-block;
            color: #000;
            font-size: 15px;
            font-weight: bold;
            text-align: center;
            text-decoration: none;
            align-content: center;
            width: 100%;
            height: 100%;
        }

        .paginationUl li.active {
            background-color: #007bff;
            border-radius: 5px;
        }
        .paginationUl li.active a {
            color: #fff;
        }
        .paginationUl li.disabled {
            opacity: 0.5;
        }
        /* Pagination End */

        /* jobmoa Logo Style Start */
        .sidebar-brand {
            display: flex;
            height: 0px;
        }

    </style>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="/js/paginationJS_0.0.1.js"></script>

    <link
            rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
            integrity="sha256-9kPW/n5nn53j4WMRYAxe9c1rCY96Oogo/MKSVdKzPmI="
            crossorigin="anonymous"
    />

</head>
<body class="container">
<div class="header">
    <div class="sidebar-brand">
        <!--begin::Brand Link-->
        <a href="/external/jobseekers">
            <!--begin::Brand Image-->
            <img
                    src="../../img/JobmoaLogo.png"
                    alt="JOBMOA Logo"
                    class="brand-image opacity-75 shadow  h-100 w-100"
            />
            <!--end::Brand Image-->
            <!--begin::Brand Text-->
            <%--                <span class="brand-text fw-light">AdminLTE 4</span>--%>
            <!--end::Brand Text-->
        </a>
        <!--end::Brand Link-->
    </div>

    <p class="header-title">참여자 관리 정보
    <p class="header-subTitle">외부 기업 제공용 페이지</p>
    </p>
    <div class="pageRowSelectDiv">
        <select class="form-select shadow-sm" name="pageRows" id="pageRows">
            <option ${param.pageRows.equals("10") ? 'selected' : ''} value="10">10</option>
            <option ${param.pageRows.equals("20") ? 'selected' : ''} value="20">20</option>
            <option ${param.pageRows.equals("30") ? 'selected' : ''} value="30">30</option>
            <option ${param.pageRows.equals("40") ? 'selected' : ''} value="40">40</option>
            <option ${param.pageRows.equals("50") ? 'selected' : ''} value="50">50</option>
        </select>
    </div>
</div>
<div class="col-md-12 text-center ms-auto me-auto d-flex justify-content-center">
    <table>
        <thead>
        <tr class="tr">
            <th>구직번호</th>
            <th>이름</th>
            <th>성별</th>
            <th>주소</th>
            <th>학교명</th>
            <th>전공</th>
            <th>희망업무</th>
            <th>자격증</th>
            <th>희망연봉/월급</th>
            <th>상담사</th>
            <th>연락처</th>
            <th>이메일</th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty participantList}">
            <tr>
                <td colspan="12">
                    데이터 확인이 불가능한 상태입니다. 담당자에게 문의 부탁드립니다.
                </td>
            </tr>
        </c:if>
        <c:if test="${not empty participantList}">
            <!-- 데이터 출력 -->
            <c:forEach var="participant" items="${participantList}">
                <tr>
                    <td>${participant.participantJobNo}</td>
                    <td>${participant.participantPartic}</td>
                    <td>${participant.participantGender}</td>
                    <td>${participant.participantAddress}</td>
                    <td>${participant.participantSchool}</td>
                    <td>${participant.participantSpecialty}</td>
                    <td>${participant.participantJobWant}</td>
                    <td>${participant.certificationName}</td>
                    <td> ${participant.participantSalWant > 0 ? participant.participantSalWant+="만원" : ""}</td>
                    <td>${participant.participantUserName}</td>
                    <td>${participant.participantPhoneNumber}</td>
                    <td>${participant.participantEmail}</td>
                </tr>
            </c:forEach>
        </c:if>
        </tbody>
    </table>
</div>

<%-- 페이지네이션 시작 --%>
<div class="paginationDiv">
    <ul class="pagination paginationUl">
    </ul>
</div>
<%-- 페이지네이션 끝 --%>
</body>
<script>
    $(document).ready(function() {
        <%-- pagination 시작 --%>
        // page 변수
        let page = parseInt("${page}", 10) || 1; // page가 비어있거나 아닌 경우 숫자로 변환 후 기본값 1 적용
        page = (page == 0) ? 1 : page; //page가 0이라면 1로 변경
        // startButton 변수
        const startButton = parseInt("${startButton}", 10) || 1; // startButton 기본값 1
        // endButton 변수
        const endButton = parseInt("${endButton}", 10) || 1; // endButton 기본값 10
        // totalButton 변수
        const totalButton = parseInt("${totalButton}", 10) || 0; // totalButton 기본값 0
        //pagination JS 함수 호출
        paginationAddItems(page, startButton, endButton, totalButton);
        <%-- pagination 끝 --%>

        let pageRows = $("#pageRows");
        pageRows.on("change", function() {
            let pageRows = $(this).val();
            location.href = "/external/jobseekers?page=1&pageRows=" + pageRows;
        });

    });
</script>
</html>