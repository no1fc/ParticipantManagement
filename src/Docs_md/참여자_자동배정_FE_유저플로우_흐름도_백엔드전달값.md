# 참여자 자동배정 FrontEnd 유저 플로우/흐름도 및 백엔드 전달값

## 1. 목적
- FrontEnd 유저 플로우와 화면 흐름을 정의한다.
- 랜덤 배정 완료 후 백엔드로 전달할 값의 형식을 정리한다.

## 2. 전제
- BackEnd는 페이지 접속 시 상담사 정보를 FrontEnd로 전달한다.
- 산정식(난이도 산정, 랜덤 배정 로직 등)은 FrontEnd에서 수행한다.
- 랜덤 배정은 상담사 ID 기준으로 수행한다.

## 3. FrontEnd 입력 데이터
### 3.1 사용자 입력 방식
- 직접 입력
- 파일 등록(Excel, CSV)

### 3.2 참여자 입력 필드
- 참여유형(1/2유형)
- 성별(남/여)
- 연령(청년/중장년)
- 경력유무
- 학력
- 특정계층여부
- 진행단계
- 출장상담사

## 4. BackEnd -> FrontEnd 전달값(페이지 접속 시)
### 4.1 상담사 리스트
- 상담사 아이디
- 상담사 성명
- 현재 인원 요약
  - 배정 인원 개수
  - 성별(남/여) 개수
  - 연령(청년/중장년) 개수
  - 경력자 개수
  - 특정계층 개수

### 4.2 예시(JSON)
```json
{
  "counselors": [
    {
      "counselorId": "C001",
      "counselorName": "홍길동",
      "currentCounts": {
        "totalAssigned": 12,
        "gender": { "male": 5, "female": 7 },
        "ageGroup": { "youth": 4, "middle": 8 },
        "career": { "experienced": 6 },
        "specialGroup": { "count": 3 }
      }
    }
  ]
}
```

## 5. FrontEnd 유저 플로우
1. 페이지 진입 시 상담사 리스트와 현재 인원 요약을 수신한다.
2. 참여자 입력 방식 선택(직접 입력 또는 파일 등록).
3. 참여자 입력 데이터를 검증한다(필수값/형식/중복 확인).
4. 난이도 산정 및 정렬 규칙을 적용한다(FrontEnd 산정식 기준).
5. 상담사별 랜덤 배정을 수행한다(상담사 ID 기준).
6. 배정 결과를 화면에 출력한다(참여자-상담사 매핑).
7. 배정 결과를 확정하면 BackEnd로 전달한다.

## 6. 화면 흐름도(Flowchart)
```mermaid
flowchart TD
  Start([페이지 접속]) --> LoadCounselors[상담사 데이터 수신]
  LoadCounselors --> InputMode{입력 방식 선택}
  InputMode --> Manual[직접 입력]
  InputMode --> File[파일 등록(Excel/CSV)]

  Manual --> Validate[입력 검증]
  File --> Parse[파일 파싱]
  Parse --> Validate

  Validate --> Calc[난이도 산정 및 정렬]
  Calc --> RandomAssign[상담사 ID 기준 랜덤 배정]
  RandomAssign --> Preview[배정 결과 미리보기]
  Preview --> Confirm{확정?}
  Confirm -- No --> Edit[입력 수정/재배정]
  Edit --> Calc
  Confirm -- Yes --> Send[BackEnd로 배정 결과 전송]
  Send --> End([완료])
```

## 7. 랜덤 배정 처리(FrontEnd)
- 입력된 참여자 목록을 산정식에 따라 정렬한다.
- 정렬된 순서대로 상담사 ID 풀에서 랜덤 배정을 수행한다.
- 배정 완료 시 참여자-상담사 매핑을 생성한다.
- 화면 출력은 상담사 ID를 기준으로 그룹핑/정렬 가능하도록 제공한다.

## 8. FrontEnd -> BackEnd 전달값(랜덤 배정 완료)
### 8.1 필수 전달 항목
- 배정 ID(FrontEnd 생성 또는 BackEnd 생성 요청 플래그)
- 배정 수행 시각(ISO-8601)
- 참여자 목록(배정 결과 포함)
- 배정에 사용한 상담사 ID 목록
- 입력 방식(직접 입력/파일 등록)

### 8.2 예시(JSON)
```json
{
  "assignmentBatchId": "BATCH-20260125-0001",
  "assignedAt": "2026-01-25T09:30:00+09:00",
  "inputMethod": "FILE",
  "counselorIds": ["C001", "C002", "C003"],
  "participants": [
    {
      "participantId": "P0001",
      "type": "TYPE_1",
      "gender": "MALE",
      "ageGroup": "YOUTH",
      "hasCareer": true,
      "education": "COLLEGE",
      "isSpecialGroup": false,
      "progressStage": "STAGE_1",
      "travelCounselor": false,
      "assignedCounselorId": "C001"
    }
  ]
}
```

## 9. 검증 및 오류 처리
- 필수값 누락 시 배정 불가 처리 및 에러 표시
- 파일 파싱 실패 시 원인 표시(컬럼 누락/형식 오류)
- 배정 결과 미확정 상태에서는 BackEnd 전송 불가

## 10. 오픈 이슈
- 배정 ID 생성 주체(FrontEnd/BackEnd) 확정 필요
- 파일 템플릿 형식 및 필수 컬럼 확정 필요
