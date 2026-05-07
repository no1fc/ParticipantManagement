package com.jobmoa.app.CounselMain.view.report;

import com.jobmoa.app.CounselMain.biz.adminpage.AdminDTO;
import com.jobmoa.app.CounselMain.biz.adminpage.AdminService;
import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.view.adminpage.AdminAccessSupport;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
public class AdminParticipantExcel {

    @Autowired
    private AdminService adminService;

    @GetMapping("/admin/api/participants/export")
    public void exportParticipants(HttpServletResponse response, AdminDTO dto, HttpSession session) {
        try {
            LoginBean loginBean = AdminAccessSupport.getLoginBean(session);
            if (loginBean == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 정보가 없습니다.");
                return;
            }
            if (!AdminAccessSupport.hasAdminAccess(session)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 필요합니다.");
                return;
            }
            AdminAccessSupport.enforceBranchScope(session, dto);

            List<AdminDTO> dataList = adminService.getParticipantExcelList(dto);

            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("참여자목록");

            String[] headers = {"구직번호", "참여자명", "생년월일", "성별", "지점", "상담사ID", "상담사명",
                    "진행단계", "모집경로", "참여유형", "경력", "학력", "특정계층", "취업역량",
                    "희망직무", "희망급여", "등록일", "마감"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (AdminDTO item : dataList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getJobNo());
                row.createCell(1).setCellValue(item.getParticipantName() != null ? item.getParticipantName() : "");
                row.createCell(2).setCellValue(item.getBirthDate() != null ? item.getBirthDate() : "");
                row.createCell(3).setCellValue(item.getGender() != null ? item.getGender() : "");
                row.createCell(4).setCellValue(item.getBranch() != null ? item.getBranch() : "");
                row.createCell(5).setCellValue(item.getCounselorAccount() != null ? item.getCounselorAccount() : "");
                row.createCell(6).setCellValue(item.getCounselorName() != null ? item.getCounselorName() : "");
                row.createCell(7).setCellValue(item.getProgressStage() != null ? item.getProgressStage() : "");
                row.createCell(8).setCellValue(item.getRecruitPath() != null ? item.getRecruitPath() : "");
                row.createCell(9).setCellValue(item.getParticipationType() != null ? item.getParticipationType() : "");
                row.createCell(10).setCellValue(item.getCareer() != null ? item.getCareer() : "");
                row.createCell(11).setCellValue(item.getEducation() != null ? item.getEducation() : "");
                row.createCell(12).setCellValue(item.getSpecialClass() != null ? item.getSpecialClass() : "");
                row.createCell(13).setCellValue(item.getEmploymentCapacity() != null ? item.getEmploymentCapacity() : "");
                row.createCell(14).setCellValue(item.getDesiredJob() != null ? item.getDesiredJob() : "");
                row.createCell(15).setCellValue(item.getDesiredSalary() != null ? item.getDesiredSalary() : "");
                row.createCell(16).setCellValue(item.getParticipantRegDate() != null ? item.getParticipantRegDate() : "");
                row.createCell(17).setCellValue(item.isClosed() ? "마감" : "진행중");
            }

            String fileName = URLEncoder.encode("관리자_참여자목록_" + LocalDate.now() + ".xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (Exception e) {
            log.error("관리자 참여자 Excel 다운로드 오류", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Excel 생성 중 오류가 발생했습니다.");
            } catch (Exception ignored) {}
        }
    }

    @GetMapping("/admin/api/excel/custom-export")
    public void customExport(HttpServletResponse response, AdminDTO dto, HttpSession session) {
        try {
            LoginBean loginBean = AdminAccessSupport.getLoginBean(session);
            if (loginBean == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 정보가 없습니다.");
                return;
            }
            if (!AdminAccessSupport.hasAdminAccess(session)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 필요합니다.");
                return;
            }
            AdminAccessSupport.enforceBranchScope(session, dto);

            String sheetsParam = dto.getExcelSheets();
            if (sheetsParam == null || sheetsParam.isEmpty()) {
                sheetsParam = "main";
            }
            String[] selectedSheets = sheetsParam.split(",");

            XSSFWorkbook workbook = new XSSFWorkbook();

            for (String sheetType : selectedSheets) {
                switch (sheetType.trim()) {
                    case "main":
                        createMainSheet(workbook, dto);
                        break;
                    case "wishJob":
                        createWishJobSheet(workbook, dto);
                        break;
                    case "certificate":
                        createCertificateSheet(workbook, dto);
                        break;
                    case "training":
                        createTrainingSheet(workbook, dto);
                        break;
                }
            }

            String fileName = URLEncoder.encode("관리자_커스텀엑셀_" + LocalDate.now() + ".xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (Exception e) {
            log.error("관리자 커스텀 Excel 다운로드 오류", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Excel 생성 중 오류가 발생했습니다.");
            } catch (Exception ignored) {}
        }
    }

    private void createMainSheet(XSSFWorkbook workbook, AdminDTO dto) {
        List<AdminDTO> dataList = adminService.getParticipantExcelFullList(dto);
        Sheet sheet = workbook.createSheet("참여자 기본정보");

        String[][] allColumns = {
            {"jobNo", "구직번호"}, {"participantRegDate", "등록일"}, {"counselorAccount", "전담자 계정"},
            {"counselorName", "상담사명"}, {"participantName", "참여자명"}, {"birthDate", "생년월일"},
            {"gender", "성별"}, {"branch", "지점"}, {"recruitPath", "모집경로"},
            {"participationType", "참여유형"}, {"schoolName", "학교명"}, {"major", "전공"},
            {"address", "주소"}, {"career", "경력"}, {"education", "학력"},
            {"specialClass", "특정계층"}, {"placementRequest", "알선요청"}, {"employmentCapacity", "취업역량"},
            {"lastCounselDate", "최근상담일"}, {"progressStage", "진행단계"}, {"initialCounselDate", "초기상담일"},
            {"jobExpiryDate", "구직만료일"}, {"iapCompletionDate", "IAP수료일"}, {"stage3EntryDate", "3단계진입일"},
            {"periodExpiryDate", "기간만료일"}, {"clinicDate", "클리닉실시일"}, {"intensivePlacement", "집중알선여부"},
            {"desiredJob", "희망직무"}, {"desiredSalary", "희망급여"},
            {"employmentDate", "취창업일"}, {"employmentProcessDate", "취창업처리일"}, {"employmentType", "취업유형"},
            {"employer", "취업처"}, {"salary", "임금"}, {"jobRole", "직무"},
            {"incentiveType", "취업인센티브"}, {"workExperienceType", "일경험분류"},
            {"memo", "메모"}, {"others", "기타"}, {"closed", "마감"},
            {"resignationDate", "퇴사일"}, {"indirectEmploymentService", "간접고용서비스"},
            {"managerChangeDate", "전담자 변경일"}, {"initialManagerAccount", "초기전담자 계정"},
            {"participantModifyDate", "참여자 수정일"}, {"iap3Month", "IAP3개월여부"}, {"iap5Month", "IAP5개월여부"},
            {"iap3MonthDate", "IAP3개월일자"}, {"iap5MonthDate", "IAP5개월일자"}, {"allowanceDate", "수당지급일"}
        };

        String selectedCols = dto.getExcelColumns();
        log.info("Excel 컬럼 선택 파라미터: [{}]", selectedCols);
        java.util.Set<String> selectedSet = null;
        if (selectedCols != null && !selectedCols.trim().isEmpty()) {
            String[] colArray = selectedCols.split(",");
            selectedSet = new java.util.LinkedHashSet<>();
            for (String c : colArray) {
                String trimmed = c.trim();
                if (!trimmed.isEmpty()) {
                    selectedSet.add(trimmed);
                }
            }
            log.info("선택된 컬럼 수: {}, 목록: {}", selectedSet.size(), selectedSet);
        }

        java.util.List<String[]> columns = new java.util.ArrayList<>();
        for (String[] col : allColumns) {
            if (selectedSet == null || selectedSet.contains(col[0])) {
                columns.add(col);
            }
        }

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            headerRow.createCell(i).setCellValue(columns.get(i)[1]);
        }

        int rowNum = 1;
        for (AdminDTO item : dataList) {
            Row row = sheet.createRow(rowNum++);
            int colIdx = 0;
            for (String[] col : columns) {
                row.createCell(colIdx++).setCellValue(getColumnValue(item, col[0]));
            }
        }
    }

    private String getColumnValue(AdminDTO item, String key) {
        switch (key) {
            case "jobNo": return String.valueOf(item.getJobNo());
            case "participantRegDate": return item.getParticipantRegDate() != null ? item.getParticipantRegDate() : "";
            case "counselorAccount": return item.getCounselorAccount() != null ? item.getCounselorAccount() : "";
            case "counselorName": return item.getCounselorName() != null ? item.getCounselorName() : "";
            case "participantName": return item.getParticipantName() != null ? item.getParticipantName() : "";
            case "birthDate": return item.getBirthDate() != null ? item.getBirthDate() : "";
            case "gender": return item.getGender() != null ? item.getGender() : "";
            case "branch": return item.getBranch() != null ? item.getBranch() : "";
            case "recruitPath": return item.getRecruitPath() != null ? item.getRecruitPath() : "";
            case "participationType": return item.getParticipationType() != null ? item.getParticipationType() : "";
            case "schoolName": return item.getSchoolName() != null ? item.getSchoolName() : "";
            case "major": return item.getMajor() != null ? item.getMajor() : "";
            case "address": return item.getAddress() != null ? item.getAddress() : "";
            case "career": return item.getCareer() != null ? item.getCareer() : "";
            case "education": return item.getEducation() != null ? item.getEducation() : "";
            case "specialClass": return item.getSpecialClass() != null ? item.getSpecialClass() : "";
            case "placementRequest": return item.getPlacementRequest() != null ? item.getPlacementRequest() : "";
            case "employmentCapacity": return item.getEmploymentCapacity() != null ? item.getEmploymentCapacity() : "";
            case "lastCounselDate": return item.getLastCounselDate() != null ? item.getLastCounselDate() : "";
            case "progressStage": return item.getProgressStage() != null ? item.getProgressStage() : "";
            case "initialCounselDate": return item.getInitialCounselDate() != null ? item.getInitialCounselDate() : "";
            case "jobExpiryDate": return item.getJobExpiryDate() != null ? item.getJobExpiryDate() : "";
            case "iapCompletionDate": return item.getIapCompletionDate() != null ? item.getIapCompletionDate() : "";
            case "stage3EntryDate": return item.getStage3EntryDate() != null ? item.getStage3EntryDate() : "";
            case "periodExpiryDate": return item.getPeriodExpiryDate() != null ? item.getPeriodExpiryDate() : "";
            case "clinicDate": return item.getClinicDate() != null ? item.getClinicDate() : "";
            case "intensivePlacement": return item.getIntensivePlacement() != null ? item.getIntensivePlacement() : "";
            case "desiredJob": return item.getDesiredJob() != null ? item.getDesiredJob() : "";
            case "desiredSalary": return item.getDesiredSalary() != null ? item.getDesiredSalary() : "";
            case "employmentDate": return item.getEmploymentDate() != null ? item.getEmploymentDate() : "";
            case "employmentProcessDate": return item.getEmploymentProcessDate() != null ? item.getEmploymentProcessDate() : "";
            case "employmentType": return item.getEmploymentType() != null ? item.getEmploymentType() : "";
            case "employer": return item.getEmployer() != null ? item.getEmployer() : "";
            case "salary": return item.getSalary() != null ? item.getSalary() : "";
            case "jobRole": return item.getJobRole() != null ? item.getJobRole() : "";
            case "incentiveType": return item.getIncentiveType() != null ? item.getIncentiveType() : "";
            case "workExperienceType": return item.getWorkExperienceType() != null ? item.getWorkExperienceType() : "";
            case "memo": return item.getMemo() != null ? item.getMemo() : "";
            case "others": return item.getOthers() != null ? item.getOthers() : "";
            case "closed": return item.isClosed() ? "마감" : "진행중";
            case "resignationDate": return item.getResignationDate() != null ? item.getResignationDate() : "";
            case "indirectEmploymentService": return item.getIndirectEmploymentService() != null ? item.getIndirectEmploymentService() : "";
            case "managerChangeDate": return item.getManagerChangeDate() != null ? item.getManagerChangeDate() : "";
            case "initialManagerAccount": return item.getInitialManagerAccount() != null ? item.getInitialManagerAccount() : "";
            case "participantModifyDate": return item.getParticipantModifyDate() != null ? item.getParticipantModifyDate() : "";
            case "iap3Month": return item.isIap3Month() ? "Y" : "N";
            case "iap5Month": return item.isIap5Month() ? "Y" : "N";
            case "iap3MonthDate": return item.getIap3MonthDate() != null ? item.getIap3MonthDate() : "";
            case "iap5MonthDate": return item.getIap5MonthDate() != null ? item.getIap5MonthDate() : "";
            case "allowanceDate": return item.getAllowanceDate() != null ? item.getAllowanceDate() : "";
            default: return "";
        }
    }

    private void createWishJobSheet(XSSFWorkbook workbook, AdminDTO dto) {
        List<AdminDTO> dataList = adminService.getExcelWishJobList(dto);
        Sheet sheet = workbook.createSheet("희망직무");

        String[] headers = {"구직번호", "참여자명", "상담사명", "카테고리(대)", "카테고리(중)", "희망직무"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (AdminDTO item : dataList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getJobNo());
            row.createCell(1).setCellValue(item.getParticipantName() != null ? item.getParticipantName() : "");
            row.createCell(2).setCellValue(item.getCounselorName() != null ? item.getCounselorName() : "");
            row.createCell(3).setCellValue(item.getExcelCategoryLarge() != null ? item.getExcelCategoryLarge() : "");
            row.createCell(4).setCellValue(item.getExcelCategoryMid() != null ? item.getExcelCategoryMid() : "");
            row.createCell(5).setCellValue(item.getExcelWishJob() != null ? item.getExcelWishJob() : "");
        }
    }

    private void createCertificateSheet(XSSFWorkbook workbook, AdminDTO dto) {
        List<AdminDTO> dataList = adminService.getExcelCertificateList(dto);
        Sheet sheet = workbook.createSheet("자격증");

        String[] headers = {"구직번호", "참여자명", "상담사명", "자격증명"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (AdminDTO item : dataList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getJobNo());
            row.createCell(1).setCellValue(item.getParticipantName() != null ? item.getParticipantName() : "");
            row.createCell(2).setCellValue(item.getCounselorName() != null ? item.getCounselorName() : "");
            row.createCell(3).setCellValue(item.getExcelCertificateName() != null ? item.getExcelCertificateName() : "");
        }
    }

    private void createTrainingSheet(XSSFWorkbook workbook, AdminDTO dto) {
        List<AdminDTO> dataList = adminService.getExcelTrainingList(dto);
        Sheet sheet = workbook.createSheet("직업훈련");

        String[] headers = {"구직번호", "참여자명", "상담사명", "직업훈련명"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (AdminDTO item : dataList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getJobNo());
            row.createCell(1).setCellValue(item.getParticipantName() != null ? item.getParticipantName() : "");
            row.createCell(2).setCellValue(item.getCounselorName() != null ? item.getCounselorName() : "");
            row.createCell(3).setCellValue(item.getExcelTrainingName() != null ? item.getExcelTrainingName() : "");
        }
    }
}
