<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="basic" type="com.jobmoa.app.CounselMain.biz.participantBasic.BasicDTO" %>

<%-- 기본정보 입력 폼 시작 --%>
<div class="row  card">
    <div class="card-header">
        <h1 class="card-title">기본정보</h1>
        <div class="card-tools">
            <button type="button" class="btn btn-tool" data-lte-toggle="card-collapse" style="font-size: 1.5rem;">
                <i data-lte-icon="expand" class="bi bi-plus-lg"></i>
                <i data-lte-icon="collapse" class="bi bi-dash-lg"></i>
            </button>
            <input type="hidden" id="basicJobNo" name="basicJobNo" value="${not empty param.basicJobNo ? param.basicJobNo : 0}">
        </div>
    </div>
    <div class="card-body">
        <div class="row default-card-body">
            <div class="col-md-1 pb-2 mb-1 w-auto">
                <label for="basicPartic" class="form-label">참여자<span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="basicPartic" name="basicPartic" value="${not empty basic ? basic.basicPartic : ""}">
            </div>
            <div class="col-md-1 datepicker-div">
                <label for="basicDob" class="form-label" >생년월일<span class="text-danger">*</span></label>
                <div class="input-group">
                    <i class="bi bi-calendar-date input-group-text"></i>
                    <input type="text" class="form-control datepicker_on" id="basicDob" name="basicDob" placeholder="yyyy-mm-dd" aria-label="생년월일" value="${not empty basic ? basic.basicDob : ""}"  autocomplete="off">
                </div>
            </div>
            <div class="col-md-1 w-auto">
                <label for="basicGender" class="form-label">성별</label>
                <select class="form-select" aria-label="Default select example" id="basicGender" name="basicGender">
                    <option selected value="남">남</option>
                    <option value="여">여</option>
                </select>
            </div>
            <div class="col-md-1 w-auto">
                <label for="basicRecruit" class="form-label">모집경로</label>
                <select class="form-select" aria-label="Default select example" id="basicRecruit" name="basicRecruit">
                    <option selected value="센터배정">센터배정</option>
                    <option value="대학">대학</option>
                    <option value="고교">고교</option>
                    <option value="훈련기관">훈련기관</option>
                    <option value="자체모집">자체모집</option>
                    <option value="이관">이관</option>
                </select>
            </div>
            <div class="col-md-1 w-auto">
                <label for="basicPartType" class="form-label">참여유형</label>
                <select class="form-select" aria-label="Default select example" id="basicPartType" name="basicPartType">
                    <option selected value="1">1</option>
                    <option value="2">2</option>
                </select>
            </div>
            <div class="col-md-1 w-auto">
                <label for="basicAddress" class="form-label">주소(시,구)<span class="text-danger">* </span></label>
                <input type="text" class="form-control" id="basicAddress" name="basicAddress" value="${not empty basic ? basic.basicAddress : ""}" readonly>
                <div id="layer" style="display:none;position:fixed;overflow:hidden;z-index:1;-webkit-overflow-scrolling:touch;">
                    <img src="//t1.daumcdn.net/postcode/resource/images/close.png" id="btnCloseLayer" style="cursor:pointer;position:absolute;right:-3px;top:-3px;z-index:1" alt="닫기 버튼">
                </div>
            </div>
            <div class="col-md-1 w-auto">
                <label for="basicSchool" class="form-label">학교명<span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="basicSchool" name="basicSchool" value="${not empty basic ? basic.basicSchool : ""}">
                <div class="overflow-y-scroll recommend" id="basicSchoollist"></div>
            </div>
            <div class="col-md-1 w-auto">
                <label for="basicSpecialty" class="form-label">전공<span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="basicSpecialty" name="basicSpecialty" value="${not empty basic ? basic.basicSpecialty : ""}">
                <div class="overflow-y-scroll recommend" id="basicSpecialtylist"></div>
            </div>
            <div class="w-auto">
                <label for="basicAntecedents" class="form-label">경력(년 단위)</label>
                <input type="number" class="form-control" id="basicAntecedents" name="basicAntecedents" min="0" max="40" placeholder="년 단위" value="${not empty basic ? basic.basicAntecedents : ""}">
            </div>
            <div class="col-md-1 w-auto">
                <label for="basicSpecific" class="form-label">특정계층</label>
                <select class="form-select" aria-label="Default select example" id="basicSpecific" name="basicSpecific">
                    <option value=""></option>
                    <option value="O">O</option>
                    <option value="X">X</option>
                </select>
            </div>
            <%--자격증 --%>
            <div class="row col-md-12 particcertif-div mt-1">
                <div class="particcertif-div-header">
                    <label for="particcertifCertif" class="form-label">
                        자격증
                        <i class="bi bi-patch-plus-fill particcertif-div-plus"></i>
                        <i class="bi bi-patch-minus particcertif-div-minus" style="display: none"></i>
                    </label>
                </div>
                <div class="particcertif-div-content d-flex" id="particcertifCertif">
                </div>
                <div class="overflow-y-scroll recommend" id="basicParticcertiflist"></div>
            </div>
        </div>
    </div>
</div>
<%-- 기본정보 입력 폼 끝 --%>