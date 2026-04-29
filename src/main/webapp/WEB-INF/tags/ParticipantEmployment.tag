<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="employment" type="com.jobmoa.app.CounselMain.biz.participantEmployment.EmploymentDTO" %>

<%-- 취업정보 입력 폼 (form-board 디자인) --%>
<div class="form-board">
    <%-- 섹션 헤더: 좌측 파란 액센트 바 + 제목 --%>
    <div class="section-header">
        <h3 class="section-title">취업정보</h3>
    </div>
    <%-- 구직번호 hidden input (수정 시 식별자) --%>
    <input type="hidden" id="employmentJobNo" name="employmentJobNo" value="${not empty param.employmentJobNo ? param.employmentJobNo : 0}">

    <table class="participant-table">
        <colgroup>
            <col style="width:15%"><col style="width:35%">
            <col style="width:15%"><col style="width:35%">
        </colgroup>
        <tbody>
            <%-- 행1: 취창업일 / 취창업처리일 --%>
            <tr>
                <th><label for="employmentStartDate">취창업일<span class="text-danger">*</span></label></th>
                <td>
                    <div class="input-group">
                        <i class="bi bi-calendar-date input-group-text"></i>
                        <input type="text" class="form-control datepicker_on" id="employmentStartDate" name="employmentStartDate" placeholder="yyyy-mm-dd" aria-label="취창업일" value="${not empty employment ? employment.employmentStartDate:""}" autocomplete="off">
                    </div>
                </td>
                <th><label for="employmentProcDate">취창업처리일</label></th>
                <td>
                    <div class="input-group">
                        <i class="bi bi-calendar-date input-group-text"></i>
                        <input type="text" class="form-control datepicker_on" id="employmentProcDate" name="employmentProcDate" placeholder="yyyy-mm-dd" aria-label="취창업처리일" value="${not empty employment ? employment.employmentProcDate:""}" autocomplete="off">
                    </div>
                </td>
            </tr>
            <%-- 행2: 퇴사일 / 취업유형 --%>
            <tr>
                <th><label for="employmentQuit">퇴사일</label></th>
                <td>
                    <div class="input-group">
                        <i class="bi bi-calendar-date input-group-text"></i>
                        <input type="text" class="form-control datepicker_on" id="employmentQuit" name="employmentQuit" placeholder="yyyy-mm-dd" aria-label="퇴사일" value="${not empty employment ? employment.employmentQuit:""}" autocomplete="off">
                    </div>
                </td>
                <th><label for="employmentEmpType">취업유형<span class="text-danger">*</span></label></th>
                <td>
                    <select class="form-select" aria-label="Default select example" id="employmentEmpType" name="employmentEmpType">

                    </select>
                </td>
            </tr>
            <%-- 행3: 취업처 / 임금 --%>
            <tr>
                <th><label for="employmentLoyer">취업처</label></th>
                <td>
                    <input type="text" class="form-control" id="employmentLoyer" name="employmentLoyer" value="${not empty employment ? employment.employmentLoyer:""}">
                </td>
                <th><label for="employmentSalary">임금(단위 만원)<span class="text-danger">*</span></label></th>
                <td>
                    <input type="number" class="form-control" id="employmentSalary" name="employmentSalary" min="0" max="1000" placeholder="단위 만원" value="${not empty employment ? employment.employmentSalary:""}">
                </td>
            </tr>
            <%-- 행4: 직무 / 일경험분류 --%>
            <tr>
                <th><label for="employmentJobRole">직무</label></th>
                <td>
                    <input type="text" class="form-control" id="employmentJobRole" name="employmentJobRole" value="${not empty employment ? employment.employmentJobRole:""}">
                </td>
                <th><label for="employmentJobcat">일경험분류</label></th>
                <td>
                    <select class="form-select" aria-label="Default select example" id="employmentJobcat" name="employmentJobcat">
                        <option value="">일경험분류_선택</option>
                        <option value="신규진입">신규진입</option>
                        <option value="장기미취업">장기미취업</option>
                        <option value="경력자">경력자</option>
                        <option value="미경력중장년">미경력중장년</option>
                    </select>
                </td>
            </tr>
            <%-- 행5: 취업인센티브 / 기타 --%>
            <tr>
                <th><label for="employmentIncentive">취업인센티브_구분<span class="text-danger">*</span></label></th>
                <td>
                    <select class="form-select" aria-label="Default select example" id="employmentIncentive" name="employmentIncentive">
                        <option value="">취업인센티브_선택</option>
                        <option value="해당[컨설팅 알선 - 120%]">해당[컨설팅 알선 - 120%]</option>
                        <option value="해당[알선취업(외부소개) - 100%]">해당[알선취업(외부소개) - 100%]</option>
                        <option value="해당[대면상담 6회(알선3회) - 100%]">해당[대면상담 6회(알선3회) - 100%]</option>
                        <option value="해당[간접고용서비스 4회 - 50%]">해당[간접고용서비스 4회 - 50%]</option>
                        <option value="미해당[서비스 미제공]">미해당[서비스 미제공]</option>
                        <option value="미해당[1개월 미만 퇴사]">미해당[1개월 미만 퇴사]</option>
                        <option value="미해당[파견업체]">미해당[파견업체]</option>
                        <option value="미해당[IAP수립 후 7일이내 취업]">미해당[IAP수립 후 7일이내 취업]</option>
                        <option value="미해당[주 30시간 미만]">미해당[주 30시간 미만]</option>
                        <option value="미해당[최저임금 미만]">미해당[최저임금 미만]</option>
                        <option value="미해당[기타(해외취업포함)]">미해당[기타(해외취업포함)]</option>
                    </select>
                </td>
                <th><label for="employmentOthers">기타</label></th>
                <td>
                    <input type="text" class="form-control" id="employmentOthers" name="employmentOthers" value="${not empty employment ? employment.employmentOthers:""}">
                </td>
            </tr>
            <%-- 행6: 메모 (colspan=3, textarea) --%>
            <tr>
                <th><label for="employmentMemo">메모</label></th>
                <td colspan="3">
                    <textarea class="form-control" id="employmentMemo" name="employmentMemo" rows="3" cols="10" placeholder="메모를 입력하세요.">${not empty employment ? employment.employmentMemo:""}</textarea>
                </td>
            </tr>
        </tbody>
    </table>
</div>
<%-- 취업정보 입력 폼 끝 --%>