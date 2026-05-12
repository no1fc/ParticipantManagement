-- 상담일정 테이블 생성
-- 실행 대상: MSSQL Server
-- 작성일: 2026-05-12

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

-- 인덱스 생성
CREATE INDEX IX_상담일정_상담사ID ON J_참여자관리_상담일정 (상담사ID, 일정날짜);
CREATE INDEX IX_상담일정_구직번호 ON J_참여자관리_상담일정 (구직번호);
CREATE INDEX IX_상담일정_알림 ON J_참여자관리_상담일정 (일정날짜, 삭제여부, 알림분);
