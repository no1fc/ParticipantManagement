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
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * 관리자 참여자 Excel 다운로드 컨트롤러.
 * 관리자 권한으로 참여자 목록을 기본 형식의 Excel 파일로 내보내는 기능을 제공한다.
 */
@Slf4j
@Controller
public class AdminParticipantExcel {

    @Autowired
    private AdminService adminService;

    /**
     * 관리자 참여자 목록을 기본 형식의 Excel 파일로 다운로드한다.
     * 관리자 권한 및 지점 범위 검증 후 참여자 데이터를 Excel로 생성하여 응답에 출력한다.
     *
     * @param response HTTP 응답 (Excel 파일 출력용)
     * @param dto      검색 조건 DTO
     * @param session  HTTP 세션 (권한 확인용)
     */
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

            // SXSSF 스트리밍 워크북(윈도우 100행) — 행 수와 무관하게 상수 메모리 유지
            SXSSFWorkbook workbook = new SXSSFWorkbook(100);
            try {
                Sheet sheet = workbook.createSheet("참여자목록");

                String[] headers = {"구직번호", "참여자명", "생년월일", "성별", "지점", "상담사ID", "상담사명",
                        "진행단계", "모집경로", "참여유형", "경력", "학력", "특정계층", "취업역량",
                        "희망직무", "희망급여", "등록일", "마감"};

                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                }

                // DB를 ResultHandler로 스트리밍하며 한 행씩 기록(전체 List 미적재)
                final int[] rowNum = {1};
                adminService.getParticipantExcelListStream(dto, ctx -> {
                    AdminDTO item = ctx.getResultObject();
                    Row row = sheet.createRow(rowNum[0]++);
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
                });

                String fileName = URLEncoder.encode("관리자_참여자목록_" + LocalDate.now() + ".xlsx", StandardCharsets.UTF_8);
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                workbook.write(response.getOutputStream());
            } finally {
                workbook.dispose(); // SXSSF 임시파일 정리(필수)
                try {
                    workbook.close();
                } catch (IOException ignored) {}
            }

        } catch (Exception e) {
            log.error("관리자 참여자 Excel 다운로드 오류", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Excel 생성 중 오류가 발생했습니다.");
            } catch (Exception ignored) {}
        }
    }
}
