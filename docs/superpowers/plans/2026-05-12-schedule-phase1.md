# 상담 일정 관리 1단계 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 상담사가 FullCalendar 기반 UI에서 상담 일정을 CRUD하고, 드래그 앤 드롭으로 이동하며, WebSocket 알림을 받고, 대시보드에서 금일 일정을 확인할 수 있는 기능을 구현한다.

**Architecture:** CounselMain 모듈 내 `biz/schedule`, `view/schedule` 패키지를 추가한다. 기존 AOP 트랜잭션과 LoginInterceptor가 자동 적용된다. REST API는 `@RestController`로 JSON 응답하고, 프론트엔드는 FullCalendar 6.1.11 CDN + jQuery `$.ajax()` + SweetAlert2를 사용한다.

**Tech Stack:** Spring MVC, MyBatis 3.5.6, MSSQL (T-SQL), FullCalendar 6.1.11, jQuery, SweetAlert2, AdminLTE 3, WebSocket (STOMP/SockJS), Lombok

**Spec:** `docs/superpowers/specs/2026-05-12-schedule-design.md`

---

## File Structure

### 신규 생성
| 파일                                                                                  | 책임 |
|-------------------------------------------------------------------------------------|------|
| `src/main/java/com/jobmoa/app/CounselMain/biz/schedule/ScheduleDTO.java`            | 일정 데이터 전송 객체 |
| `src/main/java/com/jobmoa/app/CounselMain/biz/schedule/ScheduleDAO.java`            | MyBatis 데이터 접근 |
| `src/main/java/com/jobmoa/app/CounselMain/biz/schedule/ScheduleService.java`        | 서비스 인터페이스 |
| `src/main/java/com/jobmoa/app/CounselMain/biz/schedule/ScheduleServiceImpl.java`    | 비즈니스 로직 구현 |
| `src/main/java/com/jobmoa/app/CounselMain/biz/schedule/ScheduleAlertScheduler.java` | WebSocket 알림 스케줄러 |
| `src/main/java/com/jobmoa/app/CounselMain/view/schedule/ScheduleController.java`    | 페이지 컨트롤러 |
| `src/main/java/com/jobmoa/app/CounselMain/view/schedule/ScheduleApiController.java` | REST API 컨트롤러 |
| `src/main/resources/mappings/Schedule-mapping.xml`                                  | MyBatis SQL 매퍼 |
| `src/main/webapp/WEB-INF/views/schedule/schedulePage.jsp`                           | 상담사 일정 페이지 |
| `src/main/webapp/js/schedule_0.0.2.js`                                              | 캘린더 초기화, CRUD AJAX, 드래그, 모달 |
| `src/main/webapp/css/scheduleCss/schedule_0.0.1.css`                                | 캘린더/모달/위젯 스타일 |

### 수정
| 파일 | 변경 내용 |
|------|-----------|
| `src/main/java/com/jobmoa/app/config/RootConfig.java` | ComponentScan에 `biz.schedule` 추가 |
| `src/main/java/com/jobmoa/app/config/WebMvcConfig.java` | ComponentScan에 `view.schedule` 추가 |
| `src/main/resources/sql-map-config.xml` | typeAlias + mapper 등록 |
| `src/main/webapp/WEB-INF/tags/gnb.tag` | "상담 일정" 메뉴 추가 |
| `src/main/webapp/WEB-INF/views/DashBoardPage.jsp` | 금일 J_참여자관리_상담일정 위젯 추가 |
| `src/main/webapp/js/dashboard_data_visualization_0.0.2.js` (또는 신규 JS) | 위젯 AJAX + 렌더링 |

---

## Task 1: DB 테이블 생성

**Files:**
- Create: DDL 스크립트 (MSSQL에서 직접 실행)

- [ ] **Step 1: J_참여자관리_상담일정 테이블 DDL 실행**

MSSQL에서 아래 DDL을 실행한다:

```sql
CREATE TABLE J_참여자관리_상담일정 (
    일정ID          INT IDENTITY(1,1) PRIMARY KEY,
    구직번호        INT NULL,
    상담사ID        VARCHAR(50) NOT NULL,
    일정날짜        DATE NOT NULL,
    시작시간        TIME NOT NULL,
    종료시간        TIME NULL,
    일정유형        VARCHAR(20) NOT NULL,
    메모            NVARCHAR(500) NULL,
    알림분          INT NULL DEFAULT 30,
    등록일          DATETIME DEFAULT GETDATE(),
    수정일          DATETIME DEFAULT GETDATE(),
    삭제여부        CHAR(1) DEFAULT 'N'
);
```

- [ ] **Step 2: 인덱스 생성**

```sql
CREATE INDEX IX_J_참여자관리_상담일정_상담사ID ON J_참여자관리_상담일정 (상담사ID, 일정날짜);
CREATE INDEX IX_J_참여자관리_상담일정_구직번호 ON J_참여자관리_상담일정 (구직번호);
CREATE INDEX IX_J_참여자관리_상담일정_알림 ON J_참여자관리_상담일정 (일정날짜, 삭제여부, 알림분);
```

- [ ] **Step 3: 테이블 생성 확인**

```sql
SELECT TOP 0 * FROM J_참여자관리_상담일정;
```

Expected: 빈 결과셋, 컬럼 12개 확인

---

## Task 2: Spring 설정 파일 업데이트

**Files:**
- Modify: `src/main/java/com/jobmoa/app/config/RootConfig.java`
- Modify: `src/main/java/com/jobmoa/app/config/WebMvcConfig.java`
- Modify: `src/main/resources/sql-map-config.xml`

- [ ] **Step 1: RootConfig.java에 biz.schedule 추가**

`RootConfig.java`의 `@ComponentScan` basePackages 배열에 추가한다. 기존 마지막 항목 `"com.jobmoa.app.recruitmentFormation.biz"` 뒤에 추가:

```java
"com.jobmoa.app.CounselMain.biz.recommend",
"com.jobmoa.app.CounselMain.biz.schedule",
"com.jobmoa.app.jobPlacement.biz.jobPlacement",
```

- [ ] **Step 2: WebMvcConfig.java에 view.schedule 추가**

`WebMvcConfig.java`의 `@ComponentScan` basePackages 배열에 추가한다. 기존 마지막 항목 `"com.jobmoa.app.recruitmentFormation.view.jobinfo"` 뒤에 추가:

```java
"com.jobmoa.app.CounselMain.view.adminpage",
"com.jobmoa.app.CounselMain.view.schedule",
"com.jobmoa.app.jobPlacement.view.jobPlacement",
```

- [ ] **Step 3: sql-map-config.xml에 typeAlias 추가**

`<typeAliases>` 섹션 마지막에 추가:

```xml
<typeAlias type="com.jobmoa.app.CounselMain.biz.schedule.ScheduleDTO" alias="schedule" />
```

- [ ] **Step 4: sql-map-config.xml에 mapper 등록**

`<mappers>` 섹션 마지막에 추가:

```xml
<mapper resource="mappings/Schedule-mapping.xml"/>
```

- [ ] **Step 5: 컴파일 확인**

Run: `./mvnw clean compile`
Expected: BUILD SUCCESS (아직 클래스가 없으므로 에러가 날 수 있음 — Task 3 이후 다시 확인)

---

## Task 3: ScheduleDTO 생성

**Files:**
- Create: `src/main/java/com/jobmoa/app/CounselMain/biz/schedule/ScheduleDTO.java`

- [ ] **Step 1: ScheduleDTO 클래스 작성**

```java
package com.jobmoa.app.CounselMain.biz.schedule;

import lombok.Data;

@Data
public class ScheduleDTO {
    // 테이블 매핑
    private int scheduleId;
    private Integer participantJobNo;
    private String counselorId;
    private String scheduleDate;
    private String startTime;
    private String endTime;
    private String scheduleType;
    private String memo;
    private Integer alertMinutes;
    private String regDate;
    private String modDate;
    private String deleteYn;

    // JOIN 결과
    private String participantName;
    private String participantStage;

    // 상담사 정보 (관리자 조회용)
    private String counselorName;

    // 검색/필터
    private String searchStartDate;
    private String searchEndDate;
    private String keyword;
    private String condition;

    // 드래그 앤 드롭용
    private String newDate;
    private String newStartTime;
    private String newEndTime;
}
```

---

## Task 4: MyBatis 매퍼 XML 작성

**Files:**
- Create: `src/main/resources/mappings/Schedule-mapping.xml`

- [ ] **Step 1: Schedule-mapping.xml 작성 — CRUD + 검색 쿼리 전체**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="ScheduleDAO">

    <!-- 월별 일정 목록 조회 (상담사별) -->
    <select id="selectScheduleList" parameterType="schedule" resultType="schedule">
        SELECT
            A.일정ID as scheduleId,
            A.구직번호 as participantJobNo,
            A.상담사ID as counselorId,
            CONVERT(VARCHAR(10), A.일정날짜, 23) as scheduleDate,
            CONVERT(VARCHAR(5), A.시작시간, 108) as startTime,
            CONVERT(VARCHAR(5), A.종료시간, 108) as endTime,
            A.일정유형 as scheduleType,
            A.메모 as memo,
            A.알림분 as alertMinutes,
            ISNULL(B.참여자, '(미정)') as participantName,
            ISNULL(B.진행단계, '') as participantStage
        FROM J_참여자관리_상담일정 A
        LEFT JOIN 참여자관리 B ON A.구직번호 = B.구직번호
        WHERE A.삭제여부 = 'N'
          AND A.상담사ID = #{counselorId}
          AND A.일정날짜 <![CDATA[>=]]> #{searchStartDate}
          AND A.일정날짜 <![CDATA[<=]]> #{searchEndDate}
        ORDER BY A.일정날짜, A.시작시간
    </select>

    <!-- 일정 상세 조회 -->
    <select id="selectScheduleDetail" parameterType="schedule" resultType="schedule">
        SELECT
            A.일정ID as scheduleId,
            A.구직번호 as participantJobNo,
            A.상담사ID as counselorId,
            CONVERT(VARCHAR(10), A.일정날짜, 23) as scheduleDate,
            CONVERT(VARCHAR(5), A.시작시간, 108) as startTime,
            CONVERT(VARCHAR(5), A.종료시간, 108) as endTime,
            A.일정유형 as scheduleType,
            A.메모 as memo,
            A.알림분 as alertMinutes,
            CONVERT(VARCHAR(19), A.등록일, 120) as regDate,
            CONVERT(VARCHAR(19), A.수정일, 120) as modDate,
            ISNULL(B.참여자, '(미정)') as participantName,
            ISNULL(B.진행단계, '') as participantStage
        FROM J_참여자관리_상담일정 A
        LEFT JOIN 참여자관리 B ON A.구직번호 = B.구직번호
        WHERE A.일정ID = #{scheduleId}
          AND A.삭제여부 = 'N'
    </select>

    <!-- 일정 등록 -->
    <insert id="insertSchedule" parameterType="schedule">
        INSERT INTO J_참여자관리_상담일정 (구직번호, 상담사ID, 일정날짜, 시작시간, 종료시간, 일정유형, 메모, 알림분)
        VALUES (#{participantJobNo}, #{counselorId}, #{scheduleDate}, #{startTime},
                #{endTime}, #{scheduleType}, #{memo}, #{alertMinutes})
        <selectKey keyProperty="scheduleId" resultType="int" order="AFTER">
            SELECT SCOPE_IDENTITY()
        </selectKey>
    </insert>

    <!-- 일정 수정 -->
    <update id="updateSchedule" parameterType="schedule">
        UPDATE J_참여자관리_상담일정
        SET 구직번호 = #{participantJobNo},
            일정날짜 = #{scheduleDate},
            시작시간 = #{startTime},
            종료시간 = #{endTime},
            일정유형 = #{scheduleType},
            메모 = #{memo},
            알림분 = #{alertMinutes},
            수정일 = GETDATE()
        WHERE 일정ID = #{scheduleId}
          AND 삭제여부 = 'N'
    </update>

    <!-- 논리삭제 -->
    <update id="deleteSchedule" parameterType="schedule">
        UPDATE J_참여자관리_상담일정
        SET 삭제여부 = 'Y',
            수정일 = GETDATE()
        WHERE 일정ID = #{scheduleId}
          AND 삭제여부 = 'N'
    </update>

    <!-- 드래그 앤 드롭 일정 이동 -->
    <update id="updateScheduleDrag" parameterType="schedule">
        UPDATE J_참여자관리_상담일정
        SET 일정날짜 = #{newDate},
            시작시간 = #{newStartTime},
            종료시간 = #{newEndTime},
            수정일 = GETDATE()
        WHERE 일정ID = #{scheduleId}
          AND 삭제여부 = 'N'
    </update>

    <!-- 일정 시간 중복 체크 -->
    <select id="selectDuplicateCheck" parameterType="schedule" resultType="int">
        SELECT COUNT(*)
        FROM J_참여자관리_상담일정
        WHERE 상담사ID = #{counselorId}
          AND 일정날짜 = #{scheduleDate}
          AND 삭제여부 = 'N'
          AND 시작시간 <![CDATA[<]]> #{endTime}
          AND 종료시간 <![CDATA[>]]> #{startTime}
          <if test="scheduleId != 0">
          AND 일정ID != #{scheduleId}
          </if>
    </select>

    <!-- 참여자 자동완성 검색 -->
    <select id="selectParticipantSearch" parameterType="schedule" resultType="schedule">
        SELECT TOP 10
            A.구직번호 as participantJobNo,
            A.참여자 as participantName,
            A.진행단계 as participantStage
        FROM 참여자관리 A
        WHERE A.담당자 = #{counselorId}
          AND (A.참여자 LIKE '%'+#{keyword}+'%'
               OR CAST(A.구직번호 AS VARCHAR) LIKE '%'+#{keyword}+'%')
        ORDER BY A.참여자
    </select>

    <!-- 금일 J_참여자관리_상담일정 (대시보드 위젯) -->
    <select id="selectTodaySchedule" parameterType="schedule" resultType="schedule">
        SELECT
            A.일정ID as scheduleId,
            A.구직번호 as participantJobNo,
            CONVERT(VARCHAR(5), A.시작시간, 108) as startTime,
            CONVERT(VARCHAR(5), A.종료시간, 108) as endTime,
            A.일정유형 as scheduleType,
            A.메모 as memo,
            ISNULL(B.참여자, '(미정)') as participantName
        FROM J_참여자관리_상담일정 A
        LEFT JOIN 참여자관리 B ON A.구직번호 = B.구직번호
        WHERE A.상담사ID = #{counselorId}
          AND A.일정날짜 = CONVERT(DATE, GETDATE())
          AND A.삭제여부 = 'N'
        ORDER BY A.시작시간
    </select>

    <!-- 알림 대상 일정 조회 (스케줄러용) -->
    <select id="selectAlertTargets" resultType="schedule">
        SELECT
            A.일정ID as scheduleId,
            A.상담사ID as counselorId,
            CONVERT(VARCHAR(5), A.시작시간, 108) as startTime,
            A.일정유형 as scheduleType,
            A.알림분 as alertMinutes,
            ISNULL(B.참여자, '(미정)') as participantName
        FROM J_참여자관리_상담일정 A
        LEFT JOIN 참여자관리 B ON A.구직번호 = B.구직번호
        WHERE A.일정날짜 = CONVERT(DATE, GETDATE())
          AND A.삭제여부 = 'N'
          AND A.알림분 IS NOT NULL
          AND DATEADD(MINUTE, -A.알림분, CAST(CONVERT(VARCHAR(10), GETDATE(), 23) + ' ' + CONVERT(VARCHAR(8), A.시작시간, 108) AS DATETIME))
              BETWEEN DATEADD(MINUTE, -1, GETDATE()) AND GETDATE()
    </select>

</mapper>
```

---

## Task 5: ScheduleDAO 생성

**Files:**
- Create: `src/main/java/com/jobmoa/app/CounselMain/biz/schedule/ScheduleDAO.java`

- [ ] **Step 1: ScheduleDAO 클래스 작성**

```java
package com.jobmoa.app.CounselMain.biz.schedule;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ScheduleDAO {

    @Autowired
    @Qualifier("mybatis")
    private SqlSessionTemplate sqlSession;

    private static final String ns = "ScheduleDAO.";

    public List<ScheduleDTO> selectScheduleList(ScheduleDTO dto) {
        return sqlSession.selectList(ns + "selectScheduleList", dto);
    }

    public ScheduleDTO selectScheduleDetail(ScheduleDTO dto) {
        return sqlSession.selectOne(ns + "selectScheduleDetail", dto);
    }

    public int insertSchedule(ScheduleDTO dto) {
        return sqlSession.insert(ns + "insertSchedule", dto);
    }

    public int updateSchedule(ScheduleDTO dto) {
        return sqlSession.update(ns + "updateSchedule", dto);
    }

    public int deleteSchedule(ScheduleDTO dto) {
        return sqlSession.update(ns + "deleteSchedule", dto);
    }

    public int updateScheduleDrag(ScheduleDTO dto) {
        return sqlSession.update(ns + "updateScheduleDrag", dto);
    }

    public int selectDuplicateCheck(ScheduleDTO dto) {
        return sqlSession.selectOne(ns + "selectDuplicateCheck", dto);
    }

    public List<ScheduleDTO> selectParticipantSearch(ScheduleDTO dto) {
        return sqlSession.selectList(ns + "selectParticipantSearch", dto);
    }

    public List<ScheduleDTO> selectTodaySchedule(ScheduleDTO dto) {
        return sqlSession.selectList(ns + "selectTodaySchedule", dto);
    }

    public List<ScheduleDTO> selectAlertTargets() {
        return sqlSession.selectList(ns + "selectAlertTargets");
    }
}
```

---

## Task 6: ScheduleService + ScheduleServiceImpl 생성

**Files:**
- Create: `src/main/java/com/jobmoa/app/CounselMain/biz/schedule/ScheduleService.java`
- Create: `src/main/java/com/jobmoa/app/CounselMain/biz/schedule/ScheduleServiceImpl.java`

- [ ] **Step 1: ScheduleService 인터페이스 작성**

```java
package com.jobmoa.app.CounselMain.biz.schedule;

import java.util.List;

public interface ScheduleService {
    List<ScheduleDTO> selectScheduleList(ScheduleDTO dto);
    ScheduleDTO selectScheduleDetail(ScheduleDTO dto);
    int insertSchedule(ScheduleDTO dto);
    int updateSchedule(ScheduleDTO dto);
    int deleteSchedule(ScheduleDTO dto);
    int updateScheduleDrag(ScheduleDTO dto);
    boolean checkDuplicate(ScheduleDTO dto);
    List<ScheduleDTO> selectParticipantSearch(ScheduleDTO dto);
    List<ScheduleDTO> selectTodaySchedule(ScheduleDTO dto);
    List<ScheduleDTO> selectAlertTargets();
}
```

- [ ] **Step 2: ScheduleServiceImpl 구현체 작성**

```java
package com.jobmoa.app.CounselMain.biz.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("schedule")
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleDAO scheduleDAO;

    @Override
    public List<ScheduleDTO> selectScheduleList(ScheduleDTO dto) {
        return scheduleDAO.selectScheduleList(dto);
    }

    @Override
    public ScheduleDTO selectScheduleDetail(ScheduleDTO dto) {
        return scheduleDAO.selectScheduleDetail(dto);
    }

    @Override
    public int insertSchedule(ScheduleDTO dto) {
        if (checkDuplicate(dto)) {
            return -1;
        }
        return scheduleDAO.insertSchedule(dto);
    }

    @Override
    public int updateSchedule(ScheduleDTO dto) {
        if (checkDuplicate(dto)) {
            return -1;
        }
        return scheduleDAO.updateSchedule(dto);
    }

    @Override
    public int deleteSchedule(ScheduleDTO dto) {
        return scheduleDAO.deleteSchedule(dto);
    }

    @Override
    public int updateScheduleDrag(ScheduleDTO dto) {
        ScheduleDTO checkDto = new ScheduleDTO();
        checkDto.setCounselorId(dto.getCounselorId());
        checkDto.setScheduleDate(dto.getNewDate());
        checkDto.setStartTime(dto.getNewStartTime());
        checkDto.setEndTime(dto.getNewEndTime());
        checkDto.setScheduleId(dto.getScheduleId());

        if (checkDuplicate(checkDto)) {
            return -1;
        }
        return scheduleDAO.updateScheduleDrag(dto);
    }

    @Override
    public boolean checkDuplicate(ScheduleDTO dto) {
        if (dto.getEndTime() == null || dto.getEndTime().isEmpty()) {
            return false;
        }
        int count = scheduleDAO.selectDuplicateCheck(dto);
        return count > 0;
    }

    @Override
    public List<ScheduleDTO> selectParticipantSearch(ScheduleDTO dto) {
        return scheduleDAO.selectParticipantSearch(dto);
    }

    @Override
    public List<ScheduleDTO> selectTodaySchedule(ScheduleDTO dto) {
        return scheduleDAO.selectTodaySchedule(dto);
    }

    @Override
    public List<ScheduleDTO> selectAlertTargets() {
        return scheduleDAO.selectAlertTargets();
    }
}
```

- [ ] **Step 3: 컴파일 확인**

Run: `./mvnw clean compile`
Expected: BUILD SUCCESS

- [ ] **Step 4: 커밋**

```bash
git add src/main/java/com/jobmoa/app/CounselMain/biz/schedule/
git add src/main/resources/mappings/Schedule-mapping.xml
git add src/main/java/com/jobmoa/app/config/RootConfig.java
git add src/main/java/com/jobmoa/app/config/WebMvcConfig.java
git add src/main/resources/sql-map-config.xml
git commit -m "feat: J_참여자관리_상담일정 백엔드 레이어 추가 (DTO, DAO, Service, MyBatis 매퍼)"
```

---

## Task 7: ScheduleApiController 생성

**Files:**
- Create: `src/main/java/com/jobmoa/app/CounselMain/view/schedule/ScheduleApiController.java`

- [ ] **Step 1: ScheduleApiController 작성**

```java
package com.jobmoa.app.CounselMain.view.schedule;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.schedule.ScheduleDTO;
import com.jobmoa.app.CounselMain.biz.schedule.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule")
@Slf4j
public class ScheduleApiController {

    @Autowired
    private ScheduleService scheduleService;

    private LoginBean getLogin(HttpSession session) {
        return (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
    }

    private ResponseEntity<Map<String, Object>> unauthorized() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "로그인이 필요합니다.");
        return ResponseEntity.status(401).body(result);
    }

    // 일정 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> getScheduleList(ScheduleDTO dto, HttpSession session) {
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        dto.setCounselorId(login.getMemberUserID());
        List<ScheduleDTO> list = scheduleService.selectScheduleList(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    // 일정 상세 조회
    @GetMapping("/detail/{scheduleId}")
    public ResponseEntity<?> getScheduleDetail(@PathVariable int scheduleId, HttpSession session) {
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        ScheduleDTO dto = new ScheduleDTO();
        dto.setScheduleId(scheduleId);
        ScheduleDTO detail = scheduleService.selectScheduleDetail(dto);

        Map<String, Object> result = new HashMap<>();
        if (detail == null) {
            result.put("success", false);
            result.put("message", "해당 일정을 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", detail);
        return ResponseEntity.ok(result);
    }

    // 일정 등록
    @PostMapping("/save")
    public ResponseEntity<?> saveSchedule(@RequestBody ScheduleDTO dto, HttpSession session) {
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        dto.setCounselorId(login.getMemberUserID());

        Map<String, Object> result = new HashMap<>();

        if (dto.getScheduleDate() == null || dto.getStartTime() == null || dto.getScheduleType() == null) {
            result.put("success", false);
            result.put("message", "필수 항목을 입력해주세요.");
            return ResponseEntity.status(400).body(result);
        }

        int inserted = scheduleService.insertSchedule(dto);
        if (inserted == -1) {
            result.put("success", false);
            result.put("message", "해당 시간에 이미 일정이 있습니다.");
            return ResponseEntity.status(409).body(result);
        }

        result.put("success", inserted > 0);
        result.put("message", inserted > 0 ? "일정이 등록되었습니다." : "등록에 실패했습니다.");
        result.put("scheduleId", dto.getScheduleId());
        return ResponseEntity.ok(result);
    }

    // 일정 수정
    @PutMapping("/update")
    public ResponseEntity<?> updateSchedule(@RequestBody ScheduleDTO dto, HttpSession session) {
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        Map<String, Object> result = new HashMap<>();

        // 권한 확인: 본인 일정인지 체크
        ScheduleDTO existing = scheduleService.selectScheduleDetail(dto);
        if (existing == null) {
            result.put("success", false);
            result.put("message", "해당 일정을 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(result);
        }
        if (!existing.getCounselorId().equals(login.getMemberUserID())) {
            Boolean isManager = (Boolean) session.getAttribute("IS_MANAGER");
            Boolean isBranchManager = (Boolean) session.getAttribute("IS_BRANCH_MANAGER");
            if ((isManager == null || !isManager) && (isBranchManager == null || !isBranchManager)) {
                result.put("success", false);
                result.put("message", "수정 권한이 없습니다.");
                return ResponseEntity.status(403).body(result);
            }
        }

        dto.setCounselorId(existing.getCounselorId());
        int updated = scheduleService.updateSchedule(dto);
        if (updated == -1) {
            result.put("success", false);
            result.put("message", "해당 시간에 이미 일정이 있습니다.");
            return ResponseEntity.status(409).body(result);
        }

        result.put("success", updated > 0);
        result.put("message", updated > 0 ? "일정이 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // 일정 삭제
    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable int scheduleId, HttpSession session) {
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        Map<String, Object> result = new HashMap<>();

        ScheduleDTO existing = new ScheduleDTO();
        existing.setScheduleId(scheduleId);
        ScheduleDTO detail = scheduleService.selectScheduleDetail(existing);

        if (detail == null) {
            result.put("success", false);
            result.put("message", "해당 일정을 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(result);
        }
        if (!detail.getCounselorId().equals(login.getMemberUserID())) {
            Boolean isManager = (Boolean) session.getAttribute("IS_MANAGER");
            Boolean isBranchManager = (Boolean) session.getAttribute("IS_BRANCH_MANAGER");
            if ((isManager == null || !isManager) && (isBranchManager == null || !isBranchManager)) {
                result.put("success", false);
                result.put("message", "삭제 권한이 없습니다.");
                return ResponseEntity.status(403).body(result);
            }
        }

        ScheduleDTO deleteDto = new ScheduleDTO();
        deleteDto.setScheduleId(scheduleId);
        int deleted = scheduleService.deleteSchedule(deleteDto);

        result.put("success", deleted > 0);
        result.put("message", deleted > 0 ? "일정이 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // 드래그 앤 드롭 일정 이동
    @PutMapping("/drag-update")
    public ResponseEntity<?> dragUpdateSchedule(@RequestBody ScheduleDTO dto, HttpSession session) {
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        Map<String, Object> result = new HashMap<>();

        ScheduleDTO existing = new ScheduleDTO();
        existing.setScheduleId(dto.getScheduleId());
        ScheduleDTO detail = scheduleService.selectScheduleDetail(existing);

        if (detail == null) {
            result.put("success", false);
            result.put("message", "해당 일정을 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(result);
        }
        if (!detail.getCounselorId().equals(login.getMemberUserID())) {
            result.put("success", false);
            result.put("message", "본인 일정만 이동할 수 있습니다.");
            return ResponseEntity.status(403).body(result);
        }

        dto.setCounselorId(detail.getCounselorId());
        int updated = scheduleService.updateScheduleDrag(dto);
        if (updated == -1) {
            result.put("success", false);
            result.put("message", "해당 시간에 이미 일정이 있습니다.");
            return ResponseEntity.status(409).body(result);
        }

        result.put("success", updated > 0);
        result.put("message", updated > 0 ? "일정이 이동되었습니다." : "이동에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // 참여자 검색 (자동완성)
    @GetMapping("/participants")
    public ResponseEntity<?> searchParticipants(@RequestParam String keyword, HttpSession session) {
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        ScheduleDTO dto = new ScheduleDTO();
        dto.setCounselorId(login.getMemberUserID());
        dto.setKeyword(keyword);
        List<ScheduleDTO> list = scheduleService.selectParticipantSearch(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    // 금일 J_참여자관리_상담일정 (대시보드 위젯)
    @GetMapping("/today")
    public ResponseEntity<?> getTodaySchedule(HttpSession session) {
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        ScheduleDTO dto = new ScheduleDTO();
        dto.setCounselorId(login.getMemberUserID());
        List<ScheduleDTO> list = scheduleService.selectTodaySchedule(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        result.put("count", list.size());
        return ResponseEntity.ok(result);
    }
}
```

- [ ] **Step 2: 컴파일 확인**

Run: `./mvnw clean compile`
Expected: BUILD SUCCESS

- [ ] **Step 3: 커밋**

```bash
git add src/main/java/com/jobmoa/app/CounselMain/view/schedule/ScheduleApiController.java
git commit -m "feat: J_참여자관리_상담일정 REST API 컨트롤러 추가 (CRUD + 드래그 + 검색 + 금일조회)"
```

---

## Task 8: ScheduleController 생성

**Files:**
- Create: `src/main/java/com/jobmoa/app/CounselMain/view/schedule/ScheduleController.java`

- [ ] **Step 1: ScheduleController 작성**

```java
package com.jobmoa.app.CounselMain.view.schedule;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
public class ScheduleController {

    @GetMapping("schedule.login")
    public String schedulePage(Model model, HttpSession session) {
        LoginBean login = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
        model.addAttribute("counselorId", login.getMemberUserID());
        model.addAttribute("counselorName", login.getMemberUserName());
        return "views/schedule/schedulePage";
    }
}
```

- [ ] **Step 2: 커밋**

```bash
git add src/main/java/com/jobmoa/app/CounselMain/view/schedule/ScheduleController.java
git commit -m "feat: J_참여자관리_상담일정 페이지 컨트롤러 추가"
```

---

## Task 9: CSS 파일 생성

**Files:**
- Create: `src/main/webapp/css/scheduleCss/schedule_0.0.1.css`

- [ ] **Step 1: 디렉토리 생성 확인 후 CSS 작성**

Run: `mkdir -p src/main/webapp/css/scheduleCss`

```css
/* ===== Schedule Page Styles ===== */

/* Calendar Container */
#calendar {
    background: #fff;
    border-radius: 8px;
    padding: 16px;
    box-shadow: 0 0 1px rgba(0,0,0,.125), 0 1px 3px rgba(0,0,0,.2);
}

.fc .fc-toolbar-title {
    font-size: 1.2rem;
}

.fc .fc-button-primary {
    background-color: #007bff;
    border-color: #007bff;
}

.fc .fc-daygrid-event {
    cursor: pointer;
    border-radius: 4px;
    padding: 2px 4px;
}

.fc .fc-timegrid-event {
    border-radius: 4px;
}

/* Page Actions */
.schedule-page-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
}

.schedule-page-actions h4 {
    margin: 0;
    font-weight: 600;
}

/* Modal */
.schedule-modal .modal-header {
    background: #007bff;
    color: #fff;
}

.schedule-modal .modal-header .close {
    color: #fff;
    opacity: 1;
}

.schedule-modal .form-label {
    font-weight: 500;
    font-size: 0.875rem;
    color: #495057;
}

/* Time Select */
.time-select {
    display: flex;
    gap: 8px;
    align-items: center;
}

.time-select select {
    width: auto;
}

/* Participant Search Dropdown */
.participant-search-wrapper {
    position: relative;
}

.participant-dropdown {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    z-index: 10;
    background: #fff;
    border: 1px solid #dee2e6;
    border-radius: 4px;
    max-height: 200px;
    overflow-y: auto;
    display: none;
    box-shadow: 0 4px 8px rgba(0,0,0,.1);
}

.participant-dropdown.show {
    display: block;
}

.participant-dropdown .item {
    padding: 8px 12px;
    cursor: pointer;
    font-size: 0.875rem;
    display: flex;
    justify-content: space-between;
}

.participant-dropdown .item:hover {
    background: #f0f7ff;
}

.participant-dropdown .item .name {
    font-weight: 500;
}

.participant-dropdown .item .num {
    color: #868e96;
    font-size: 0.8rem;
}

/* Detail Modal */
.schedule-detail .detail-row {
    display: flex;
    padding: 8px 0;
    border-bottom: 1px solid #f0f0f0;
}

.schedule-detail .detail-row:last-child {
    border-bottom: none;
}

.schedule-detail .detail-label {
    width: 100px;
    font-weight: 500;
    color: #495057;
    flex-shrink: 0;
}

.schedule-detail .detail-value {
    flex: 1;
}

.detail-actions {
    display: flex;
    gap: 8px;
    margin-top: 16px;
}

/* Schedule Type Badges */
.badge-schedule {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 0.75rem;
    font-weight: 500;
}

.badge-face {
    background: #d4edda;
    color: #155724;
}

.badge-phone {
    background: #cce5ff;
    color: #004085;
}

.badge-video {
    background: #fff3cd;
    color: #856404;
}

.badge-etc {
    background: #e2e3e5;
    color: #383d41;
}

/* Alert Setting */
.alert-setting {
    display: flex;
    gap: 8px;
    align-items: center;
}

.alert-setting select {
    width: auto;
}

.alert-setting input[type="number"] {
    width: 80px;
}

/* ===== Dashboard Widget Styles ===== */
.today-schedule-widget {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 0 1px rgba(0,0,0,.125), 0 1px 3px rgba(0,0,0,.2);
    overflow: hidden;
}

.today-schedule-header {
    padding: 12px 16px;
    background: #f8f9fa;
    border-bottom: 1px solid #dee2e6;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.today-schedule-header .count-badge {
    background: #007bff;
    color: #fff;
    padding: 2px 10px;
    border-radius: 12px;
    font-size: 0.85rem;
    font-weight: 600;
}

.today-schedule-header h6 {
    margin: 0;
    font-weight: 600;
}

.today-schedule-body {
    padding: 0;
    max-height: 300px;
    overflow-y: auto;
}

.today-schedule-item {
    display: flex;
    align-items: center;
    padding: 10px 16px;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: background 0.15s;
}

.today-schedule-item:hover {
    background: #f8f9fa;
}

.today-schedule-item:last-child {
    border-bottom: none;
}

.today-schedule-item .time {
    font-size: 0.85rem;
    font-weight: 600;
    color: #007bff;
    width: 60px;
    flex-shrink: 0;
}

.today-schedule-item .info {
    flex: 1;
}

.today-schedule-item .info .p-name {
    font-weight: 500;
    font-size: 0.875rem;
}

.today-schedule-item .info .p-type {
    font-size: 0.75rem;
    color: #6c757d;
}

.today-schedule-empty {
    padding: 24px 16px;
    text-align: center;
    color: #adb5bd;
    font-size: 0.875rem;
}
```

- [ ] **Step 2: 커밋**

```bash
git add src/main/webapp/css/scheduleCss/schedule_0.0.1.css
git commit -m "feat: J_참여자관리_상담일정 CSS 스타일 추가"
```

---

## Task 10: JSP 뷰 생성

**Files:**
- Create: `src/main/webapp/WEB-INF/views/schedule/schedulePage.jsp`

- [ ] **Step 1: 디렉토리 생성 확인**

Run: `mkdir -p src/main/webapp/WEB-INF/views/schedule`

- [ ] **Step 2: schedulePage.jsp 작성**

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="gnb" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>상담 일정 - Jobmoa</title>
    <gnb:gnb/>
    <link href="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.css" rel="stylesheet">
    <link href="/css/scheduleCss/schedule_0.0.1.css" rel="stylesheet">
</head>
<body class="hold-transition sidebar-mini layout-fixed">
<div class="wrapper">
    <gnb:gnb/>

    <div class="content-wrapper">
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6"><h1>상담 일정 조회 및 등록</h1></div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><a href="./dashboard.login">홈</a></li>
                            <li class="breadcrumb-item active">상담 일정</li>
                        </ol>
                    </div>
                </div>
            </div>
        </section>

        <section class="content">
            <div class="container-fluid">
                <div class="schedule-page-actions">
                    <h4><i class="fas fa-calendar-alt mr-2"></i>상담 일정</h4>
                    <button class="btn btn-primary" id="btnRegister">
                        <i class="fas fa-plus mr-1"></i>일정 등록
                    </button>
                </div>
                <div id="calendar"></div>
            </div>
        </section>
    </div>

    <!-- 등록/수정 모달 -->
    <div class="modal fade schedule-modal" id="scheduleModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalTitle">
                        <i class="fas fa-calendar-plus mr-2"></i>상담 일정 등록
                    </h5>
                    <button type="button" class="close" data-dismiss="modal">
                        <span>&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="editScheduleId">

                    <div class="form-group">
                        <label class="form-label">일정 날짜 <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" id="scheduleDate">
                    </div>

                    <div class="form-group">
                        <label class="form-label">시간 <span class="text-danger">*</span></label>
                        <div class="time-select">
                            <select class="form-control" id="startHour">
                                <option value="">시</option>
                                <c:forEach var="h" begin="9" end="18">
                                    <option value="${String.format('%02d', h)}">${String.format('%02d', h)}</option>
                                </c:forEach>
                            </select>
                            <span>:</span>
                            <select class="form-control" id="startMinute">
                                <option value="">분</option>
                                <option value="00">00</option><option value="10">10</option>
                                <option value="20">20</option><option value="30">30</option>
                                <option value="40">40</option><option value="50">50</option>
                            </select>
                            <span>~</span>
                            <select class="form-control" id="endHour">
                                <option value="">시</option>
                                <c:forEach var="h" begin="9" end="18">
                                    <option value="${String.format('%02d', h)}">${String.format('%02d', h)}</option>
                                </c:forEach>
                            </select>
                            <span>:</span>
                            <select class="form-control" id="endMinute">
                                <option value="">분</option>
                                <option value="00">00</option><option value="10">10</option>
                                <option value="20">20</option><option value="30">30</option>
                                <option value="40">40</option><option value="50">50</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">참여자 선택</label>
                        <div class="participant-search-wrapper">
                            <input type="text" class="form-control" id="participantSearch"
                                   placeholder="참여자 이름을 입력하세요" autocomplete="off">
                            <input type="hidden" id="participantJobNo">
                            <div class="participant-dropdown" id="participantDropdown"></div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">일정 유형</label>
                        <select class="form-control" id="scheduleType">
                            <option value="대면상담">대면상담</option>
                            <option value="전화상담">전화상담</option>
                            <option value="화상상담">화상상담</option>
                            <option value="기타">기타</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label">알림 설정</label>
                        <div class="alert-setting">
                            <select class="form-control" id="alertSelect">
                                <option value="">알림없음</option>
                                <option value="10">10분 전</option>
                                <option value="30" selected>30분 전</option>
                                <option value="60">1시간 전</option>
                                <option value="custom">직접입력</option>
                            </select>
                            <input type="number" class="form-control" id="alertCustom"
                                   min="1" max="1440" placeholder="분" style="display:none;">
                            <span id="alertCustomLabel" style="display:none;">분 전</span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">메모</label>
                        <textarea class="form-control" id="scheduleMemo" rows="3"
                                  maxlength="500" placeholder="메모를 입력하세요 (최대 500자)"></textarea>
                        <small class="form-text text-muted text-right">
                            <span id="memoCount">0</span>/500
                        </small>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">취소</button>
                    <button type="button" class="btn btn-primary" id="btnSave">
                        <i class="fas fa-check mr-1"></i>저장
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- 상세 모달 -->
    <div class="modal fade" id="detailModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="fas fa-calendar-check mr-2"></i>일정 상세</h5>
                    <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
                </div>
                <div class="modal-body schedule-detail">
                    <input type="hidden" id="detailScheduleId">
                    <div class="detail-row">
                        <div class="detail-label">날짜</div>
                        <div class="detail-value" id="detailDate"></div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">시간</div>
                        <div class="detail-value" id="detailTime"></div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">참여자</div>
                        <div class="detail-value" id="detailParticipant"></div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">유형</div>
                        <div class="detail-value" id="detailType"></div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">알림</div>
                        <div class="detail-value" id="detailAlert"></div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">메모</div>
                        <div class="detail-value" id="detailMemo"></div>
                    </div>
                    <div class="detail-actions">
                        <button class="btn btn-success btn-sm" id="btnCounselNote" disabled title="추후 연동 예정">
                            <i class="fas fa-book mr-1"></i>상담일지 작성
                        </button>
                        <button class="btn btn-warning btn-sm" id="btnEdit">
                            <i class="fas fa-pencil-alt mr-1"></i>수정
                        </button>
                        <button class="btn btn-danger btn-sm" id="btnDelete">
                            <i class="fas fa-trash mr-1"></i>삭제
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <footer:footer/>
</div>

<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.js"></script>
<script src="/js/schedule_0.0.2.js"></script>
</body>
</html>
```

- [ ] **Step 3: 커밋**

```bash
git add src/main/webapp/WEB-INF/views/schedule/schedulePage.jsp
git commit -m "feat: J_참여자관리_상담일정 JSP 뷰 추가"
```

---

## Task 11: JavaScript 작성

**Files:**
- Create: `src/main/webapp/js/schedule_0.0.2.js`

- [ ] **Step 1: schedule_0.0.2.js 작성**

```javascript
$(document).ready(function () {

    var calendar;
    var selectedScheduleId = null;

    // ========================
    // 색상 매핑
    // ========================
    var colorMap = {
        '대면상담': '#007bff',
        '전화상담': '#28a745',
        '화상상담': '#ffc107',
        '기타': '#6c757d'
    };

    var badgeMap = {
        '대면상담': 'badge-face',
        '전화상담': 'badge-phone',
        '화상상담': 'badge-video',
        '기타': 'badge-etc'
    };

    // ========================
    // FullCalendar 초기화
    // ========================
    var calendarEl = document.getElementById('calendar');
    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        navLinks: true,
        editable: true,
        selectable: true,
        dayMaxEvents: 3,
        height: 'auto',
        events: function (info, successCallback, failureCallback) {
            $.ajax({
                url: '/api/schedule/list',
                type: 'GET',
                data: {
                    searchStartDate: info.startStr.substring(0, 10),
                    searchEndDate: info.endStr.substring(0, 10)
                },
                success: function (res) {
                    if (res.success) {
                        var events = res.data.map(function (s) {
                            return {
                                id: s.scheduleId,
                                title: s.participantName + ' - ' + s.scheduleType,
                                start: s.scheduleDate + 'T' + s.startTime,
                                end: s.endTime ? s.scheduleDate + 'T' + s.endTime : null,
                                color: colorMap[s.scheduleType] || '#6c757d',
                                extendedProps: s
                            };
                        });
                        successCallback(events);
                    } else {
                        failureCallback();
                    }
                },
                error: function () {
                    failureCallback();
                }
            });
        },
        dateClick: function (info) {
            openRegisterModal(info.dateStr);
        },
        eventClick: function (info) {
            openDetailModal(info.event.extendedProps);
        },
        eventDrop: function (info) {
            handleEventDrag(info);
        },
        eventResize: function (info) {
            handleEventDrag(info);
        }
    });
    calendar.render();

    // ========================
    // 드래그 앤 드롭 처리
    // ========================
    function handleEventDrag(info) {
        var event = info.event;
        var props = event.extendedProps;
        var newStart = event.start;
        var newEnd = event.end;

        var newDate = newStart.toISOString().substring(0, 10);
        var newStartTime = String(newStart.getHours()).padStart(2, '0') + ':' + String(newStart.getMinutes()).padStart(2, '0');
        var newEndTime = newEnd
            ? String(newEnd.getHours()).padStart(2, '0') + ':' + String(newEnd.getMinutes()).padStart(2, '0')
            : null;

        Swal.fire({
            title: '일정을 이동하시겠습니까?',
            text: newDate + ' ' + newStartTime + (newEndTime ? ' ~ ' + newEndTime : ''),
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '이동',
            cancelButtonText: '취소'
        }).then(function (result) {
            if (result.isConfirmed) {
                $.ajax({
                    url: '/api/schedule/drag-update',
                    type: 'PUT',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        scheduleId: props.scheduleId,
                        newDate: newDate,
                        newStartTime: newStartTime,
                        newEndTime: newEndTime
                    }),
                    success: function (res) {
                        if (res.success) {
                            Swal.fire('완료', res.message, 'success');
                            calendar.refetchEvents();
                        } else {
                            Swal.fire('오류', res.message, 'error');
                            info.revert();
                        }
                    },
                    error: function (xhr) {
                        var msg = xhr.responseJSON ? xhr.responseJSON.message : '처리 중 오류가 발생했습니다.';
                        Swal.fire('오류', msg, 'error');
                        info.revert();
                    }
                });
            } else {
                info.revert();
            }
        });
    }

    // ========================
    // 등록 모달
    // ========================
    $('#btnRegister').on('click', function () {
        var today = new Date().toISOString().substring(0, 10);
        openRegisterModal(today);
    });

    function openRegisterModal(dateStr) {
        $('#modalTitle').html('<i class="fas fa-calendar-plus mr-2"></i>상담 일정 등록');
        $('#editScheduleId').val('');
        $('#scheduleDate').val(dateStr);
        $('#startHour, #startMinute, #endHour, #endMinute').val('');
        $('#participantSearch').val('');
        $('#participantJobNo').val('');
        $('#scheduleType').val('대면상담');
        $('#alertSelect').val('30');
        $('#alertCustom').hide();
        $('#alertCustomLabel').hide();
        $('#scheduleMemo').val('');
        $('#memoCount').text('0');
        $('#scheduleModal').modal('show');
    }

    // ========================
    // 참여자 검색 자동완성
    // ========================
    var searchTimer;
    $('#participantSearch').on('input', function () {
        var keyword = $(this).val().trim();
        clearTimeout(searchTimer);
        if (keyword.length === 0) {
            $('#participantDropdown').removeClass('show');
            return;
        }
        searchTimer = setTimeout(function () {
            $.ajax({
                url: '/api/schedule/participants',
                type: 'GET',
                data: { keyword: keyword },
                success: function (res) {
                    if (res.success && res.data.length > 0) {
                        var html = res.data.map(function (p) {
                            return '<div class="item" data-jobno="' + p.participantJobNo + '" data-name="' + p.participantName + '">'
                                + '<span class="name">' + p.participantName + '</span>'
                                + '<span class="num">' + p.participantJobNo + ' | ' + (p.participantStage || '') + '</span>'
                                + '</div>';
                        }).join('');
                        $('#participantDropdown').html(html).addClass('show');
                    } else {
                        $('#participantDropdown').removeClass('show');
                    }
                }
            });
        }, 300);
    });

    $(document).on('click', '#participantDropdown .item', function () {
        $('#participantSearch').val($(this).data('name'));
        $('#participantJobNo').val($(this).data('jobno'));
        $('#participantDropdown').removeClass('show');
    });

    $(document).on('click', function (e) {
        if (!$(e.target).closest('.participant-search-wrapper').length) {
            $('#participantDropdown').removeClass('show');
        }
    });

    // ========================
    // 알림 설정
    // ========================
    $('#alertSelect').on('change', function () {
        if ($(this).val() === 'custom') {
            $('#alertCustom').show();
            $('#alertCustomLabel').show();
        } else {
            $('#alertCustom').hide();
            $('#alertCustomLabel').hide();
        }
    });

    // 메모 글자수
    $('#scheduleMemo').on('input', function () {
        $('#memoCount').text($(this).val().length);
    });

    // ========================
    // 저장 (등록/수정)
    // ========================
    $('#btnSave').on('click', function () {
        var scheduleDate = $('#scheduleDate').val();
        var startHour = $('#startHour').val();
        var startMinute = $('#startMinute').val();
        var endHour = $('#endHour').val();
        var endMinute = $('#endMinute').val();

        if (!scheduleDate || !startHour || !startMinute) {
            Swal.fire('알림', '날짜와 시작 시간을 선택해주세요.', 'warning');
            return;
        }

        var startTime = startHour + ':' + startMinute;
        var endTime = (endHour && endMinute) ? endHour + ':' + endMinute : null;

        if (endTime && endTime <= startTime) {
            Swal.fire('알림', '종료 시간은 시작 시간 이후여야 합니다.', 'warning');
            return;
        }

        var alertVal = $('#alertSelect').val();
        var alertMinutes = null;
        if (alertVal === 'custom') {
            alertMinutes = parseInt($('#alertCustom').val());
            if (!alertMinutes || alertMinutes < 1) {
                Swal.fire('알림', '알림 시간을 입력해주세요.', 'warning');
                return;
            }
        } else if (alertVal) {
            alertMinutes = parseInt(alertVal);
        }

        var data = {
            scheduleDate: scheduleDate,
            startTime: startTime,
            endTime: endTime,
            participantJobNo: $('#participantJobNo').val() ? parseInt($('#participantJobNo').val()) : null,
            scheduleType: $('#scheduleType').val(),
            memo: $('#scheduleMemo').val(),
            alertMinutes: alertMinutes
        };

        var editId = $('#editScheduleId').val();
        var url, method;

        if (editId) {
            data.scheduleId = parseInt(editId);
            url = '/api/schedule/update';
            method = 'PUT';
        } else {
            url = '/api/schedule/save';
            method = 'POST';
        }

        $.ajax({
            url: url,
            type: method,
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (res) {
                if (res.success) {
                    Swal.fire('완료', res.message, 'success');
                    $('#scheduleModal').modal('hide');
                    calendar.refetchEvents();
                } else {
                    Swal.fire('오류', res.message, 'error');
                }
            },
            error: function (xhr) {
                var msg = xhr.responseJSON ? xhr.responseJSON.message : '처리 중 오류가 발생했습니다.';
                Swal.fire('오류', msg, 'error');
            }
        });
    });

    // ========================
    // 상세 모달
    // ========================
    function openDetailModal(schedule) {
        selectedScheduleId = schedule.scheduleId;
        $('#detailScheduleId').val(schedule.scheduleId);
        $('#detailDate').text(schedule.scheduleDate);
        $('#detailTime').text(schedule.startTime + (schedule.endTime ? ' ~ ' + schedule.endTime : ''));
        $('#detailParticipant').text(schedule.participantName || '(미정)');

        var badge = badgeMap[schedule.scheduleType] || 'badge-etc';
        $('#detailType').html('<span class="badge-schedule ' + badge + '">' + schedule.scheduleType + '</span>');

        if (schedule.alertMinutes) {
            $('#detailAlert').text(schedule.alertMinutes + '분 전');
        } else {
            $('#detailAlert').text('알림없음');
        }

        $('#detailMemo').text(schedule.memo || '-');
        $('#detailModal').modal('show');
    }

    // 수정 버튼
    $('#btnEdit').on('click', function () {
        var id = $('#detailScheduleId').val();
        $('#detailModal').modal('hide');

        $.ajax({
            url: '/api/schedule/detail/' + id,
            type: 'GET',
            success: function (res) {
                if (res.success) {
                    var s = res.data;
                    $('#modalTitle').html('<i class="fas fa-pencil-alt mr-2"></i>상담 일정 수정');
                    $('#editScheduleId').val(s.scheduleId);
                    $('#scheduleDate').val(s.scheduleDate);

                    var st = s.startTime.split(':');
                    $('#startHour').val(st[0]);
                    $('#startMinute').val(st[1]);

                    if (s.endTime) {
                        var et = s.endTime.split(':');
                        $('#endHour').val(et[0]);
                        $('#endMinute').val(et[1]);
                    } else {
                        $('#endHour, #endMinute').val('');
                    }

                    $('#participantSearch').val(s.participantName !== '(미정)' ? s.participantName : '');
                    $('#participantJobNo').val(s.participantJobNo || '');
                    $('#scheduleType').val(s.scheduleType);
                    $('#scheduleMemo').val(s.memo || '');
                    $('#memoCount').text((s.memo || '').length);

                    // 알림 설정 복원
                    if (s.alertMinutes === null) {
                        $('#alertSelect').val('').trigger('change');
                    } else if ([10, 30, 60].indexOf(s.alertMinutes) >= 0) {
                        $('#alertSelect').val(String(s.alertMinutes)).trigger('change');
                    } else {
                        $('#alertSelect').val('custom').trigger('change');
                        $('#alertCustom').val(s.alertMinutes);
                    }

                    $('#scheduleModal').modal('show');
                }
            }
        });
    });

    // 삭제 버튼
    $('#btnDelete').on('click', function () {
        var id = $('#detailScheduleId').val();
        $('#detailModal').modal('hide');

        Swal.fire({
            title: '일정을 삭제하시겠습니까?',
            text: '삭제된 일정은 복구할 수 없습니다.',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            confirmButtonText: '삭제',
            cancelButtonText: '취소'
        }).then(function (result) {
            if (result.isConfirmed) {
                $.ajax({
                    url: '/api/schedule/delete/' + id,
                    type: 'DELETE',
                    success: function (res) {
                        if (res.success) {
                            Swal.fire('완료', res.message, 'success');
                            calendar.refetchEvents();
                        } else {
                            Swal.fire('오류', res.message, 'error');
                        }
                    },
                    error: function (xhr) {
                        var msg = xhr.responseJSON ? xhr.responseJSON.message : '처리 중 오류가 발생했습니다.';
                        Swal.fire('오류', msg, 'error');
                    }
                });
            }
        });
    });

});
```

- [ ] **Step 2: 커밋**

```bash
git add src/main/webapp/js/schedule_0.0.2.js
git commit -m "feat: J_참여자관리_상담일정 JavaScript 추가 (캘린더, CRUD, 드래그, 자동완성)"
```

---

## Task 12: GNB 메뉴 추가

**Files:**
- Modify: `src/main/webapp/WEB-INF/tags/gnb.tag`

- [ ] **Step 1: gnb.tag에 "상담 일정" 메뉴 항목 추가**

"상담관리" 섹션 내부, 기존 메뉴 항목들(참여자 조회 및 관리, 신규 참여자) 뒤에 추가한다.

gnb.tag 파일에서 "상담관리" nav-treeview `<ul>` 안, 마지막 `</li>` 뒤에 추가:

```jsp
<li class="nav-item">
    <a href="./schedule.login" class="nav-link">
        <small><p>상담 일정</p></small>
    </a>
</li>
```

- [ ] **Step 2: 커밋**

```bash
git add src/main/webapp/WEB-INF/tags/gnb.tag
git commit -m "feat: GNB에 상담 일정 메뉴 추가"
```

---

## Task 13: 대시보드 금일 J_참여자관리_상담일정 위젯 추가

**Files:**
- Modify: `src/main/webapp/WEB-INF/views/DashBoardPage.jsp`
- Modify: `src/main/webapp/js/dashboard_data_visualization_0.0.2.js` (또는 위젯 렌더링 코드를 JSP 하단 인라인 스크립트로 작성)

- [ ] **Step 1: DashBoardPage.jsp에 위젯 HTML 추가**

기존 대시보드 JSP의 적절한 위치(상단 통계 카드 아래 또는 사이드 컬럼)에 위젯을 추가한다. 기존 레이아웃 구조를 확인한 후, `<div class="row">` 안에 `col-lg-4` 또는 `col-lg-6`으로 배치:

```jsp
<!-- 금일 J_참여자관리_상담일정 위젯 -->
<div class="col-lg-4">
    <div class="today-schedule-widget">
        <div class="today-schedule-header">
            <h6><i class="fas fa-calendar-day mr-1"></i>오늘 J_참여자관리_상담일정</h6>
            <span class="count-badge" id="todayScheduleCount">0건</span>
        </div>
        <div class="today-schedule-body" id="todayScheduleBody">
            <div class="today-schedule-empty">로딩 중...</div>
        </div>
    </div>
</div>
```

- [ ] **Step 2: 위젯 AJAX 호출 스크립트 추가**

DashBoardPage.jsp 하단 또는 dashboard JS 파일에 추가:

```javascript
// 금일 J_참여자관리_상담일정 위젯
$(document).ready(function () {
    $.ajax({
        url: '/api/schedule/today',
        type: 'GET',
        success: function (res) {
            if (res.success) {
                $('#todayScheduleCount').text(res.count + '건');
                if (res.data.length === 0) {
                    $('#todayScheduleBody').html('<div class="today-schedule-empty">오늘 예정된 J_참여자관리_상담일정이 없습니다.</div>');
                    return;
                }
                var html = res.data.map(function (s) {
                    var badgeMap = {'대면상담':'badge-face','전화상담':'badge-phone','화상상담':'badge-video','기타':'badge-etc'};
                    var badge = badgeMap[s.scheduleType] || 'badge-etc';
                    return '<div class="today-schedule-item" onclick="location.href=\'./schedule.login\'">'
                        + '<div class="time">' + s.startTime + '</div>'
                        + '<div class="info">'
                        + '<div class="p-name">' + s.participantName + '</div>'
                        + '<div class="p-type"><span class="badge-schedule ' + badge + '">' + s.scheduleType + '</span></div>'
                        + '</div></div>';
                }).join('');
                $('#todayScheduleBody').html(html);
            }
        },
        error: function () {
            $('#todayScheduleBody').html('<div class="today-schedule-empty">일정을 불러올 수 없습니다.</div>');
        }
    });
});
```

- [ ] **Step 3: 커밋**

```bash
git add src/main/webapp/WEB-INF/views/DashBoardPage.jsp
git commit -m "feat: 대시보드에 금일 J_참여자관리_상담일정 위젯 추가"
```

---

## Task 14: WebSocket 알림 스케줄러 생성

**Files:**
- Create: `src/main/java/com/jobmoa/app/CounselMain/biz/schedule/ScheduleAlertScheduler.java`

- [ ] **Step 1: ScheduleAlertScheduler 작성**

```java
package com.jobmoa.app.CounselMain.biz.schedule;

import com.jobmoa.app.jobPlacement.view.webSocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ScheduleAlertScheduler {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private WebSocketService webSocketService;

    @Scheduled(cron = "0 * * * * *")
    public void checkScheduleAlerts() {
        try {
            List<ScheduleDTO> targets = scheduleService.selectAlertTargets();
            for (ScheduleDTO target : targets) {
                Map<String, Object> notification = new HashMap<>();
                notification.put("type", "SCHEDULE_ALERT");
                notification.put("title", "상담 일정 알림");
                notification.put("message", target.getAlertMinutes() + "분 후 "
                        + target.getParticipantName() + "님과 "
                        + target.getScheduleType() + " 일정이 있습니다.");
                notification.put("scheduleId", target.getScheduleId());
                notification.put("startTime", target.getStartTime());

                webSocketService.sendObjectToUser(
                        "/topic/notification",
                        notification,
                        target.getCounselorId()
                );
                log.info("일정 알림 전송: 상담사={}, 일정ID={}", target.getCounselorId(), target.getScheduleId());
            }
        } catch (Exception e) {
            log.error("일정 알림 스케줄러 오류: {}", e.getMessage());
        }
    }
}
```

- [ ] **Step 2: 컴파일 확인**

Run: `./mvnw clean compile`
Expected: BUILD SUCCESS

- [ ] **Step 3: 커밋**

```bash
git add src/main/java/com/jobmoa/app/CounselMain/biz/schedule/ScheduleAlertScheduler.java
git commit -m "feat: J_참여자관리_상담일정 WebSocket 알림 스케줄러 추가"
```

---

## Task 15: 통합 빌드 및 수동 테스트

**Files:** 전체 프로젝트

- [ ] **Step 1: 전체 빌드**

Run: `./mvnw clean compile`
Expected: BUILD SUCCESS

- [ ] **Step 2: 애플리케이션 실행**

Run: `./mvnw spring-boot:run`
Expected: 포트 8088에서 정상 기동

- [ ] **Step 3: 브라우저에서 수동 테스트**

로그인 후 아래 항목을 순서대로 확인:

1. GNB에 "상담 일정" 메뉴 표시 확인
2. 상담 일정 페이지 접속 (`/schedule.login`) → FullCalendar 월간 뷰 표시
3. 월/주/일 뷰 전환 동작
4. 날짜 클릭 → 등록 모달 오픈, 날짜 자동 세팅
5. 참여자 검색 자동완성 동작
6. 알림 설정 (30분 전 선택, 직접입력 전환)
7. 일정 등록 → 캘린더에 이벤트 표시
8. 이벤트 클릭 → 상세 모달 표시
9. 수정 → 변경사항 반영
10. 드래그 앤 드롭 → 확인 다이얼로그 → 이동 완료
11. 삭제 → SweetAlert2 확인 → 삭제 완료
12. 중복 시간 등록 시도 → 차단 메시지
13. 대시보드 금일 J_참여자관리_상담일정 위젯 표시 확인

- [ ] **Step 4: 최종 커밋**

```bash
git add -A
git commit -m "feat: J_참여자관리_상담일정 관리 기능 1단계 구현 완료 (v1.5.0)"
```
