# Excel 다운로드 시트 추가 개발 계획

> 작성일: 2026-05-06  
> 검토 대상: `ParticipantAllExcel.java`, `participantMain.jsp`, `BranchPariticipant.jsp`, `participant_excel_download_0.0.1.js`, `Participant-mapping.xml`

## 1. 목표

참여자 전체 Excel 다운로드 파일에 기존 전체참여자 시트를 유지하면서 다음 3개 시트를 추가한다.

- 희망직무
- 자격증
- 직업훈련

일반 상담사 다운로드는 본인 담당 참여자만 내려받고, 지점관리자 또는 관리자가 지점관리 화면에서 다운로드할 때는 지점 전체 참여자를 내려받아야 한다.

## 2. 기존 계획에서 수정해야 할 내용

### 2.1 신규 DTO/DAO/Service 메서드 3세트 생성은 과하다

기존 `ParticipantDAO`는 `participantCondition` 값으로 Mapper ID를 선택하는 공통 구조다.  
따라서 `ExcelWishJobDTO`, `ExcelCertificateDTO`, `ExcelEducationDTO`를 새로 만들고 DAO/Service 메서드를 각각 추가하는 계획은 구현 범위가 불필요하게 커진다.

정리 방향:

- `ParticipantDTO`에 Excel 보조 필드만 추가한다.
- `Participant-mapping.xml`에 Excel 시트용 SELECT 3개를 추가한다.
- `ParticipantAllExcel.java`에서 `participantCondition`만 바꿔 기존 `participantService.selectAll()`을 재사용한다.
- `sql-map-config.xml` typeAlias 추가는 하지 않는다.

### 2.2 기존 전체참여자 시트의 희망직무 컬럼을 방치하면 값이 비거나 `0`이 들어갈 수 있다

현재 `participantExcel` 쿼리에서 `A.희망직무` 매핑이 주석 처리되어 있지만, `ParticipantAllExcel.setProgress()`는 여전히 `data.getParticipantJobWant()`를 기존 시트에 쓴다.

문제:

- DB 변경으로 희망직무가 `J_참여자관리_희망직무`로 분리되어 기존 컬럼 값이 없다.
- 현재 `setCellValue()`는 `null`을 `0`으로 쓰므로 텍스트 컬럼인 희망직무에 `0`이 들어갈 수 있다.

정리 방향:

- 기존 전체참여자 시트의 희망직무 컬럼은 1순위 희망직무로 채운다.
- 신규 희망직무 시트에는 모든 희망직무 행을 출력한다.
- 텍스트 시트 작성 helper에서는 `null`을 빈 문자열로 처리한다.

### 2.3 권한 분기는 자동 적용된다고만 쓰면 부족하다

권한 조건 자체는 `participantAuthCondition`을 공유하면 적용된다. 다만 Excel 생성 중 `participantCondition`을 여러 번 바꾸므로, 다음 값이 모든 시트 조회 전에 유지되어야 한다.

- 일반 상담사: `participantUserid`, `participantBranch`
- 지점관리자/관리자 지점 화면: `searchPath = "managerSearch"`, `participantBranch`
- 검색/필터 파라미터: 기존 `participantExcel`과 동일하게 적용

정리 방향:

- `ParticipantAllExcel.participantExcel()`에서 기존처럼 권한 관련 DTO 값을 먼저 확정한다.
- `createExcel()` 내부에서는 같은 `ParticipantDTO`를 사용하되 조회 직전에 `participantCondition`만 바꾼다.
- 신규 3개 쿼리는 기존 `participantExcel`과 같은 `participantAuthCondition`, `participantSearchCondition`, `participantSearchFiterCondition`을 사용한다.

## 3. 시트 구성

### 3.1 전체참여자

기존 템플릿 시트를 유지한다.

추가 보완:

- 기존 희망직무 컬럼은 `J_참여자관리_희망직무`의 1순위 희망직무로 채운다.
- 별도 신규 시트에는 2순위 이상 포함 모든 희망직무를 출력한다.

### 3.2 희망직무

| 순서 | 컬럼명 |
|---:|---|
| 1 | 구직번호 |
| 2 | 상담사명 |
| 3 | 참여자명 |
| 4 | 카테고리_대 |
| 5 | 카테고리_중 |
| 6 | 희망직무 |

출력 기준:

- `J_참여자관리_희망직무`에 행이 있는 데이터만 출력한다.
- 정렬은 `구직번호 DESC`, `희망순위 ASC`, `PK ASC` 기준으로 고정한다.

### 3.3 자격증

| 순서 | 컬럼명 |
|---:|---|
| 1 | 구직번호 |
| 2 | 상담사명 |
| 3 | 참여자명 |
| 4 | 자격증명 |

출력 기준:

- `J_참여자관리_자격증`에 행이 있는 데이터만 출력한다.
- 정렬은 `구직번호 DESC`, `자격증번호 ASC` 기준으로 고정한다.

### 3.4 직업훈련

| 순서 | 컬럼명 |
|---:|---|
| 1 | 구직번호 |
| 2 | 상담사명 |
| 3 | 참여자명 |
| 4 | 직업훈련명 |

출력 기준:

- `J_참여자관리_직업훈련`에 행이 있는 데이터만 출력한다.
- 정렬은 `구직번호 DESC`, `직업훈련번호 ASC` 기준으로 고정한다.

## 4. 수정 대상 파일

| 파일 | 변경 내용 |
|---|---|
| `src/main/java/com/jobmoa/app/CounselMain/biz/participant/ParticipantDTO.java` | Excel 시트 전용 보조 필드 추가 |
| `src/main/resources/mappings/Participant-mapping.xml` | 기존 `participantExcel` 희망직무 매핑 보완, 신규 시트 SELECT 3개 추가 |
| `src/main/java/com/jobmoa/app/CounselMain/view/report/ParticipantAllExcel.java` | 기존 시트 작성 후 신규 시트 3개 생성 |

확인 대상:

| 파일 | 확인 내용 |
|---|---|
| `src/main/webapp/WEB-INF/views/participantMain.jsp` | 일반 상담사 화면에서 `branchManagementPageFlag=false`가 전달되는지 확인 |
| `src/main/webapp/WEB-INF/views/BranchPariticipant.jsp` | 지점관리자 화면에서 `branchManagementPageFlag=true`가 전달되는지 확인 |
| `src/main/webapp/js/participant_excel_download_0.0.1.js` | 두 화면 모두 동일한 `/participantExcel.login` 엔드포인트를 호출하는지 확인 |

현재 확인 결과 JSP/JS는 공통 태그와 공통 JS를 사용하므로 별도 화면 수정은 필요하지 않다. 단, 기능 테스트는 두 화면에서 모두 수행해야 한다.

## 5. 상세 구현 계획

### 5.1 `ParticipantDTO` 필드 추가

`ParticipantDTO`에 신규 시트 전용 필드를 추가한다. 기존 추천 키워드용 `jobCategoryLarge`, `jobCategoryMid`와 의미가 섞이지 않도록 Excel 전용 이름을 사용한다.

```java
private String excelCategoryLarge;    // 희망직무 시트 카테고리_대
private String excelCategoryMid;      // 희망직무 시트 카테고리_중
private String excelWishJob;          // 희망직무 시트 희망직무
private String excelCertificateName;  // 자격증 시트 자격증명
private String excelTrainingName;     // 직업훈련 시트 직업훈련명
```

### 5.2 기존 `participantExcel` 쿼리 보완

기존 전체참여자 시트의 희망직무 컬럼을 유지하기 위해 1순위 희망직무를 조회한다.

```xml
OUTER APPLY (
    SELECT TOP 1
        W.희망직무
    FROM J_참여자관리_희망직무 W
    WHERE W.구직번호 = A.구직번호
    ORDER BY W.희망순위 ASC, W.PK ASC
) WISH1
```

`SELECT` 컬럼에는 아래 매핑을 복구한다.

```xml
ISNULL(WISH1.희망직무, '') AS participantJobWant,
```

주의:

- 기존 `-- ISNULL(A.희망직무, '') AS participantJobWant` 주석은 제거한다.
- `OUTER APPLY`는 `FROM J_참여자관리 A`와 `WHERE` 사이에 둔다.

### 5.3 신규 시트용 SELECT 추가

신규 SELECT는 모두 `resultType="participant"`를 사용한다.

희망직무:

```xml
<select id="participantExcelWishJob" resultType="participant">
    SELECT
        A.구직번호 AS participantJobNo,
        ISNULL(B.이름, '') AS participantUserName,
        ISNULL(A.참여자, '') AS participantPartic,
        ISNULL(C.직무_카테고리_대, '') AS excelCategoryLarge,
        ISNULL(C.직무_카테고리_중, '') AS excelCategoryMid,
        ISNULL(C.희망직무, '') AS excelWishJob
    FROM J_참여자관리 A
    LEFT JOIN J_참여자관리_로그인정보 B
        ON A.전담자_계정 = B.아이디
    INNER JOIN J_참여자관리_희망직무 C
        ON A.구직번호 = C.구직번호
    WHERE
        <include refid="participantAuthCondition"/>
        <if test="searchOption != null">
            <include refid="participantSearchCondition"/>
            <include refid="participantSearchFiterCondition"/>
        </if>
    ORDER BY A.구직번호 DESC, C.희망순위 ASC, C.PK ASC
</select>
```

자격증:

```xml
<select id="participantExcelCertificate" resultType="participant">
    SELECT
        A.구직번호 AS participantJobNo,
        ISNULL(B.이름, '') AS participantUserName,
        ISNULL(A.참여자, '') AS participantPartic,
        ISNULL(C.자격증, '') AS excelCertificateName
    FROM J_참여자관리 A
    LEFT JOIN J_참여자관리_로그인정보 B
        ON A.전담자_계정 = B.아이디
    INNER JOIN J_참여자관리_자격증 C
        ON A.구직번호 = C.구직번호
    WHERE
        <include refid="participantAuthCondition"/>
        <if test="searchOption != null">
            <include refid="participantSearchCondition"/>
            <include refid="participantSearchFiterCondition"/>
        </if>
    ORDER BY A.구직번호 DESC, C.자격증번호 ASC
</select>
```

직업훈련:

```xml
<select id="participantExcelTraining" resultType="participant">
    SELECT
        A.구직번호 AS participantJobNo,
        ISNULL(B.이름, '') AS participantUserName,
        ISNULL(A.참여자, '') AS participantPartic,
        ISNULL(C.직업훈련, '') AS excelTrainingName
    FROM J_참여자관리 A
    LEFT JOIN J_참여자관리_로그인정보 B
        ON A.전담자_계정 = B.아이디
    INNER JOIN J_참여자관리_직업훈련 C
        ON A.구직번호 = C.구직번호
    WHERE
        <include refid="participantAuthCondition"/>
        <if test="searchOption != null">
            <include refid="participantSearchCondition"/>
            <include refid="participantSearchFiterCondition"/>
        </if>
    ORDER BY A.구직번호 DESC, C.직업훈련번호 ASC
</select>
```

주의:

- `A.구직번호`는 `ParticipantDTO.participantJobNo`가 `int` 타입이므로 `ISNULL(A.구직번호, '')`처럼 빈 문자열을 섞지 않는다.
- 신규 쿼리의 권한 조건은 반드시 기존 `participantExcel`과 동일한 fragment를 사용한다.
- `participantSearchCondition`은 테이블 별칭 `A`(J_참여자관리), `B`(J_참여자관리_로그인정보)를 전제하므로 신규 쿼리도 동일 별칭을 유지해야 한다.
- `participantSearchFiterCondition`의 연도 필터(`participantInItCons`)가 `A.초기상담일`을 참조하므로 신규 쿼리에서도 `A` 별칭이 `J_참여자관리`여야 한다.

### 5.4 `ParticipantAllExcel.java` 수정

기존 `createExcel()` 흐름은 유지하고, 기존 시트 작성 후 신규 시트를 만든다.

작업 순서:

1. 기존 전체참여자 데이터 조회: `participantCondition = "participantExcel"`
2. 기존 시트 작성: `createRow(sheet, 1, datas)`
3. 희망직무 조회: `participantCondition = "participantExcelWishJob"`
4. 희망직무 시트 생성
5. 자격증 조회: `participantCondition = "participantExcelCertificate"`
6. 자격증 시트 생성
7. 직업훈련 조회: `participantCondition = "participantExcelTraining"`
8. 직업훈련 시트 생성
9. 수식 재계산 후 다운로드 응답 작성

추가할 helper:

```java
private List<ParticipantDTO> selectExcelSheetData(ParticipantDTO participantDTO, String condition) {
    participantDTO.setParticipantCondition(condition);
    try {
        List<ParticipantDTO> datas = participantService.selectAll(participantDTO);
        return datas == null ? List.of() : datas;
    } catch (Exception e) {
        log.error("엑셀 시트 데이터 조회 실패 condition: {}", condition, e);
        return List.of();
    }
}
```

```java
private void setStringCellValue(Row row, int columnIndex, Object value) {
    Cell cell = row.getCell(columnIndex);
    if (cell == null) {
        cell = row.createCell(columnIndex);
    }
    cell.setCellValue(value == null ? "" : String.valueOf(value));
}
```

시트별 row writer는 별도 메서드로 분리한다.

- `createWishJobRows(Sheet sheet, List<ParticipantDTO> datas)`
- `createCertificateRows(Sheet sheet, List<ParticipantDTO> datas)`
- `createTrainingRows(Sheet sheet, List<ParticipantDTO> datas)`

### 5.5 JSP/JS 수정 여부

현재 구조에서는 JSP/JS 수정이 필요하지 않다.

근거:

- `participantMain.jsp`와 `BranchPariticipant.jsp` 모두 `pariticipantSearchForm.tag`를 사용한다.
- Excel 버튼은 공통 tag의 `#excelDownload`다.
- 두 화면 모두 `participant_excel_download_0.0.1.js`를 로드한다.
- JS는 `/participantExcel.login`에 현재 URL 검색 파라미터와 `branchManagementPageFlag`를 붙여 호출한다.
- 서버는 `branchManagementPageFlag=true`이고 관리자 권한이 있을 때 `searchPath="managerSearch"`로 전환한다.

수정이 필요한 경우:

- 지점관리자 화면에서 `branchManagementPageFlag=true`가 누락되는 버그가 발견될 때만 JSP를 수정한다.
- 일반 상담사 화면에서 hidden 값이 빈 값으로 전달되면 `false` 문자열이 전달되도록 JSP 또는 JS에서 보정한다.

## 6. 권한별 기대 동작

| 다운로드 위치 | 요청 값 | 서버 DTO 상태 | 기대 결과 |
|---|---|---|---|
| 일반 상담사 참여자 조회 | `branchManagementPageFlag=false` | `participantUserid=로그인ID`, `participantBranch=로그인지점`, `searchPath=null` | 본인 담당 참여자만 전체참여자/희망직무/자격증/직업훈련 시트에 출력 |
| 지점관리자 지점전체참여자 조회 | `branchManagementPageFlag=true` | `participantBranch=로그인지점`, `searchPath=managerSearch` | 로그인 지점 전체 참여자가 출력되고 상담사명은 각 참여자의 전담자명으로 표시 |
| 일반 상담사가 임의로 `branchManagementPageFlag=true` 전달 | `branchManagementPageFlag=true` | 관리자 권한이 없으므로 `searchPath` 미설정 | 본인 담당 참여자만 출력 |

## 7. 테스트 계획

### 7.1 빌드 확인

Windows:

```powershell
.\mvnw.cmd clean compile
```

macOS/Linux:

```bash
./mvnw clean compile
```

### 7.2 일반 상담사 다운로드 확인

- `participantMain.jsp` 화면에서 Excel 다운로드
- 파일명 앞부분이 로그인 상담사 ID인지 확인
- 시트 개수 4개 확인
- 전체참여자 시트에 본인 담당 참여자만 있는지 확인
- 희망직무/자격증/직업훈련 시트도 본인 담당 참여자 데이터만 있는지 확인
- 기존 전체참여자 시트의 희망직무 컬럼이 `0`이 아니라 1순위 희망직무 또는 빈 문자열인지 확인

### 7.3 지점관리자 다운로드 확인

- `BranchPariticipant.jsp` 화면에서 Excel 다운로드
- 파일명 앞부분이 지점명인지 확인
- 시트 개수 4개 확인
- 전체참여자 시트에 지점 전체 참여자가 있는지 확인
- 희망직무/자격증/직업훈련 시트도 지점 전체 기준으로 출력되는지 확인
- 상담사명 컬럼이 로그인 사용자명이 아니라 각 참여자의 전담자명인지 확인

### 7.4 검색/필터 적용 확인

아래 조건을 일반 상담사 화면과 지점관리자 화면에서 각각 적용한 뒤 다운로드한다.

- 참여자명 검색
- 구직번호 검색
- 진행단계 검색
- 전담자 검색: 지점관리자 화면만
- 마감/진행중 필터
- 연도 필터
- 참여유형 필터
- 옵션 필터: 초기상담 미실시자, 최근상담일 21일, 구직 만료 15일 도래자 등

기대 결과:

- 기존 전체참여자 시트와 신규 3개 시트의 대상 참여자 범위가 동일하다.
- 신규 시트는 대상 참여자 중 해당 자식 데이터가 있는 행만 출력한다.

## 8. 구현 순서

- [x] `ParticipantDTO.java`에 Excel 보조 필드 5개 추가
- [x] `Participant-mapping.xml`의 `participantExcel`에 1순위 희망직무 OUTER APPLY 매핑 추가
- [x] `Participant-mapping.xml`에 `participantExcelWishJob` SELECT 추가
- [x] `Participant-mapping.xml`에 `participantExcelCertificate` SELECT 추가
- [x] `Participant-mapping.xml`에 `participantExcelTraining` SELECT 추가
- [x] `ParticipantAllExcel.java`에 신규 시트 데이터 조회 helper (`selectExcelSheetData`) 추가
- [x] `ParticipantAllExcel.java`에 문자열 셀 작성 helper (`setStringCellValue`) 추가
- [x] `ParticipantAllExcel.java`에 시트 헤더 생성 helper (`createSheetHeader`) 추가
- [x] `ParticipantAllExcel.java`에 희망직무 시트 생성 로직 (`createWishJobRows`) 추가
- [x] `ParticipantAllExcel.java`에 자격증 시트 생성 로직 (`createCertificateRows`) 추가
- [x] `ParticipantAllExcel.java`에 직업훈련 시트 생성 로직 (`createTrainingRows`) 추가
- [x] `./mvnw clean compile` 컴파일 확인 완료
- [x] 일반 상담사 화면에서 Excel 다운로드 검증
- [x] 지점관리자 화면에서 Excel 다운로드 검증

## 9. 구현 결과 (2026-05-06)

### 수정된 파일 3개

| 파일 | 변경 요약 |
|---|---|
| `ParticipantDTO.java` | `excelCategoryLarge`, `excelCategoryMid`, `excelWishJob`, `excelCertificateName`, `excelTrainingName` 필드 5개 추가 |
| `Participant-mapping.xml` | `participantExcel` 쿼리에 `OUTER APPLY`로 1순위 희망직무 매핑 복구, `participantExcelWishJob`/`participantExcelCertificate`/`participantExcelTraining` SELECT 3개 신규 추가 |
| `ParticipantAllExcel.java` | `createExcel()`에서 기존 시트 작성 후 희망직무/자격증/직업훈련 시트 3개 동적 생성. Helper 메서드 6개 추가: `selectExcelSheetData()`, `setStringCellValue()`, `createSheetHeader()`, `createWishJobRows()`, `createCertificateRows()`, `createTrainingRows()` |

### 수정하지 않은 파일

| 파일 | 사유 |
|---|---|
| `ParticipantDAO.java` | 기존 `selectAll()`이 `participantCondition`으로 mapper ID를 동적 선택하므로 추가 메서드 불필요 |
| `ParticipantService.java` / `ParticipantServiceImpl.java` | 동일 사유 |
| `sql-map-config.xml` | 신규 typeAlias 불필요 (`resultType="participant"` 재사용) |
| `participantMain.jsp` / `BranchPariticipant.jsp` | 공통 JS로 동일 엔드포인트 호출, 변경 불필요 |
| `participant_excel_download_0.0.1.js` | 변경 불필요 |

## 10. 최종 정리

기존 계획의 큰 방향인 “희망직무, 자격증, 직업훈련 시트 추가”는 맞다.  
다만 이 프로젝트의 실제 구조에서는 새 DTO/DAO/Service 세트를 늘리는 방식보다 `ParticipantDTO`와 기존 `participantCondition` 기반 조회 패턴을 재사용하는 방식이 변경 범위가 작고 안전하다.

가장 중요한 누락은 기존 전체참여자 시트의 희망직무 컬럼 처리다. DB에서 희망직무가 분리된 상태이므로, 신규 희망직무 시트만 추가하면 기존 시트의 희망직무 컬럼이 비거나 `0`으로 내려갈 수 있다. 따라서 기존 시트에는 1순위 희망직무를 유지하고, 신규 시트에는 전체 희망직무 목록을 출력하는 방향으로 구현한다.
