# Starbucks 참여자 페이지 구현 계획

## 목적
`J_참여자관리` (EXTERNALPARTICIPANT VIEW) 에서 `희망직무` 컬럼이  
**슈퍼바이저 / 바리스타 / Supervisor / Barista** 중 하나를 포함하는 참여자만  
`/Starbucks` URL로 조회할 수 있는 페이지 제작.

---

## 구현 범위

| 파일 | 작업 |
|------|------|
| `mappings/jobPlacement-mapping.xml` | 스타벅스 필터 SQL 추가 |
| `jobPlacement/view/starbucks/StarbucksController.java` | 신규 Controller |
| `webapp/WEB-INF/starbucksView/participant-list.jsp` | 신규 JSP |

> DTO / Service / DAO는 기존 `JobPlacementDTO`, `JobPlacementService`, `JobPlacementDAO`를 재사용한다.  
> 새로운 SQL ID만 `condition` 패턴으로 추가한다.

---

## SQL 설계 (`jobPlacement-mapping.xml`)

### selectStarbucksCount
```sql
SELECT COUNT(구직번호) AS totalCount
FROM EXTERNALPARTICIPANT
WHERE (
    희망직무 LIKE '%슈퍼바이저%' OR
    희망직무 LIKE '%바리스타%'   OR
    희망직무 LIKE '%Supervisor%' OR
    희망직무 LIKE '%Barista%'
)
```

### selectStarbucksAll
```sql
SELECT (상위 N건 페이지네이션)
FROM EXTERNALPARTICIPANT
WHERE (
    희망직무 LIKE '%슈퍼바이저%' OR
    희망직무 LIKE '%바리스타%'   OR
    희망직무 LIKE '%Supervisor%' OR
    희망직무 LIKE '%Barista%'
)
ORDER BY 구직번호 DESC
OFFSET #{startPage} ROWS
FETCH NEXT #{pageRows} ROWS ONLY
```

반환 컬럼 (`resultType="jobPlacement"`):
- jobNumber, participant, gender, age, address, desiredJob, desiredSalary, counselor, certificate, uniqueNumber

---

## Controller 설계 (`StarbucksController.java`)

```
URL:  GET /Starbucks
패키지: com.jobmoa.app.jobPlacement.view.starbucks
```

- `@Controller`, `@GetMapping("/Starbucks")`
- `@Autowired JobPlacementService`
- 페이지네이션 로직은 `JobPlacementController.jobPlacementListPage()`와 동일
- 개인정보 숨김(hideInfo) 로직 동일하게 적용
- 반환 뷰: `"starbucksView/participant-list"`

---

## JSP 설계 (`starbucksView/participant-list.jsp`)

- Bootstrap 5 + `jobPlacementDefault_0.0.1.css` 재사용
- 테이블 형태로 참여자 목록 출력
- 페이지네이션 (이전/다음/번호 버튼)
- 컬럼: 구직번호, 참여자, 성별, 나이/나이대, 희망직무, 희망연봉, 자격증, 담당상담사
- 본인 상담사 데이터: 실명/주소 표시, 타인: 마스킹 처리
- 검색 폼 불필요 (희망직무 필터 고정)

---

## 진행 체크리스트

- [x] 1. `jobPlacement-mapping.xml` — SQL 2개 추가
- [x] 2. `StarbucksController.java` — Controller 생성
- [x] 3. `starbucksView/participant-list.jsp` — JSP 생성
- [x] 4. 빌드 확인 (`./mvnw clean compile`) — 성공

---

## 참고 — 기존 패턴

- DAO 디스패치: `jobPlacementDTO.setCondition("selectStarbucksCount")` → `jobPlacementDAO.selectOne()`
- 목록 디스패치: `jobPlacementDTO.setCondition("selectStarbucksAll")` → `jobPlacementDAO.selectAll()`
- DTO 페이지네이션: `startPage` = OFFSET, `endPage` = OFFSET + pageRows (RowNum 방식)  
  → 기존 RowNum 방식 확인 후 동일하게 적용