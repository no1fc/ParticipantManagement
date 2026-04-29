<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="basic" type="com.jobmoa.app.CounselMain.biz.participantBasic.BasicDTO" %>

<%-- 기본정보 입력 폼 (form-board 디자인, 8칸 테이블) --%>
<div class="form-board">
    <%-- 섹션 헤더: 좌측 파란 액센트 바 + 제목 --%>
    <div class="section-header">
        <h3 class="section-title">기본정보</h3>
    </div>
    <%-- 구직번호 hidden input (수정 시 식별자) --%>
    <input type="hidden" id="basicJobNo" name="basicJobNo" value="${not empty param.basicJobNo ? param.basicJobNo : 0}">

    <table class="participant-table participant-table--8col">
        <colgroup>
            <col style="width:10%"><col style="width:15%">
            <col style="width:10%"><col style="width:15%">
            <col style="width:10%"><col style="width:15%">
            <col style="width:10%"><col style="width:15%">
        </colgroup>
        <tbody>
            <%-- 행1: 참여자 / 생년월일 / 성별 / 모집경로 --%>
            <tr>
                <th><label for="basicPartic">참여자<span class="text-danger">*</span></label></th>
                <td>
                    <input type="text" class="form-control" id="basicPartic" name="basicPartic" value="${not empty basic ? basic.basicPartic : ""}">
                </td>
                <th><label for="basicDob">생년월일<span class="text-danger">*</span></label></th>
                <td>
                    <div class="input-group">
                        <i class="bi bi-calendar-date input-group-text"></i>
                        <input type="text" class="form-control datepicker_on" id="basicDob" name="basicDob" placeholder="yyyy-mm-dd" aria-label="생년월일" value="${not empty basic ? basic.basicDob : ""}" autocomplete="off">
                    </div>
                </td>
                <th><label for="basicGender">성별</label></th>
                <td>
                    <select class="form-select" aria-label="Default select example" id="basicGender" name="basicGender">
                        <option selected value="남">남</option>
                        <option value="여">여</option>
                    </select>
                </td>
                <th><label for="basicRecruit">모집경로</label></th>
                <td>
                    <select class="form-select" aria-label="Default select example" id="basicRecruit" name="basicRecruit">
                        <option selected value="센터배정">센터배정</option>
                        <option value="자체모집(대학)">자체모집(대학)</option>
                        <option value="자체모집(고교)">자체모집(고교)</option>
                        <option value="자체모집(훈련기관)">자체모집(훈련기관)</option>
                        <option value="자체모집(기타)">자체모집(기타)</option>
                        <option value="이관">이관</option>
                    </select>
                </td>
            </tr>
            <%-- 행2: 참여유형 / 특정계층 / 학력 / 경력 --%>
            <tr>
                <th><label for="basicPartType">참여유형</label></th>
                <td>
                    <select class="form-select" aria-label="Default select example" id="basicPartType" name="basicPartType">
                        <option selected value="1">1</option>
                        <option value="2">2</option>
                    </select>
                </td>
                <th><label for="basicSpecific">특정계층</label></th>
                <td>
                    <select class="form-select" aria-label="Default select example" id="basicSpecific" name="basicSpecific">
                        <option value=""></option>
                        <option value="O">O</option>
                        <option value="X">X</option>
                    </select>
                </td>
                <th><label for="basicEducation">학력<span class="text-danger">*</span></label></th>
                <td>
                    <select class="form-select" id="basicEducation" name="basicEducation">
                        <option value="고졸">고졸</option>
                        <option value="검정고시">검정고시</option>
                        <option value="초대졸">초대졸</option>
                        <option value="대졸">대졸</option>
                        <option value="대학원">대학원</option>
                    </select>
                </td>
                <th><label for="basicAntecedents">경력(년)</label></th>
                <td>
                    <input type="number" class="form-control" id="basicAntecedents" name="basicAntecedents" min="0" max="40" placeholder="년 단위" value="${not empty basic ? basic.basicAntecedents : ""}">
                </td>
            </tr>
            <%-- 행3: 주소 (colspan=7) --%>
            <tr>
                <th><label for="basicAddress">주소(시,구)<span class="text-danger">*</span></label></th>
                <td colspan="7">
                    <input type="text" class="form-control" id="basicAddress" name="basicAddress" value="${not empty basic ? basic.basicAddress : ""}" readonly>
                    <%-- Daum 우편번호 layer는 position:fixed로 동작하므로 셀 내부 위치 무관 --%>
                    <div id="layer" style="display:none;position:fixed;overflow:hidden;z-index:1;-webkit-overflow-scrolling:touch;">
                        <img src="//t1.daumcdn.net/postcode/resource/images/close.png" id="btnCloseLayer" style="cursor:pointer;position:absolute;right:-3px;top:-3px;z-index:1" alt="닫기 버튼">
                    </div>
                </td>
            </tr>
            <%-- 행4: 학교명(자동완성) colspan=3 / 전공(자동완성) colspan=3 --%>
            <tr>
                <th><label for="basicSchool">학교명<span class="text-danger">*</span></label></th>
                <td colspan="3" class="autocomplete-cell">
                    <input type="text" class="form-control" id="basicSchool" name="basicSchool" value="${not empty basic ? basic.basicSchool : ""}">
                    <div class="overflow-y-scroll recommend" id="basicSchoollist"></div>
                </td>
                <th><label for="basicSpecialty">전공<span class="text-danger">*</span></label></th>
                <td colspan="3" class="autocomplete-cell">
                    <input type="text" class="form-control" id="basicSpecialty" name="basicSpecialty" value="${not empty basic ? basic.basicSpecialty : ""}">
                    <div class="overflow-y-scroll recommend" id="basicSpecialtylist"></div>
                </td>
            </tr>
            <%-- 행5: 자격증 (colspan=7, 동적 추가/삭제) --%>
            <tr>
                <th>
                    자격증
                    <span class="cert-count" id="certCount">0</span>
                </th>
                <td colspan="7" class="autocomplete-cell">
                    <%-- particcertifDiv_0.0.2.js가 항목을 렌더링 --%>
                    <div class="particcertif-div-content" id="particcertifCertif"></div>
                    <button type="button" class="btn btn-sm btn-outline-primary mt-1" id="addCertBtn">
                        <i class="bi bi-plus-circle"></i> 자격증 추가
                    </button>
                    <%-- 자동완성 추천 드롭다운 (focus 시 표시) --%>
                    <div class="overflow-y-scroll recommend" id="basicParticcertiflist"></div>
                </td>
            </tr>
            <%-- 행6: 교육내역 (colspan=7, 동적 추가/삭제 — 상담정보에서 기본정보로 이동) --%>
            <tr>
                <th>
                    교육내역
                    <span class="edu-count" id="eduCount">0</span>
                </th>
                <td colspan="7">
                    <%-- educationDiv_0.0.2.js가 항목을 렌더링 --%>
                    <div class="education-div-content" id="education"></div>
                    <button type="button" class="btn btn-sm btn-outline-primary mt-1" id="addEduBtn">
                        <i class="bi bi-plus-circle"></i> 교육내역 추가
                    </button>
                </td>
            </tr>
        </tbody>
    </table>
</div>
<%-- 기본정보 입력 폼 끝 --%>