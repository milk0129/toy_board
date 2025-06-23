-- 기존 시퀀스 먼저 삭제
DROP SEQUENCE SEQ_BOARD_NO;

-- 게시판 테이블 삭제
DROP TABLE BoardV02 CASCADE CONSTRAINTS;
PURGE RECYCLEBIN;

-- 테이블 생성
CREATE TABLE BoardV02 (
    BOD_NO        NUMBER PRIMARY KEY,                   -- 글번호
    BOD_WRITER    VARCHAR2(50) NOT NULL,                -- 글쓴이 (USER_ID, FK)
    BOD_EMAIL     VARCHAR2(100),                        -- 이메일
    BOD_SUBJECT   VARCHAR2(200) NOT NULL,               -- 글 제목
    BOD_PWD       VARCHAR2(100),                        -- 글 비밀번호 
    BOD_LOGTIME   DATE DEFAULT SYSDATE,                 -- 작성 시간
    BOD_CONTENT   VARCHAR2(4000),                       -- 글 내용
    BOD_READCNT   NUMBER DEFAULT 0,                     -- 조회수
    BOD_CONNIP    VARCHAR2(20),                         -- 작성자 IP
    CONSTRAINT FK_BOD_WRITER FOREIGN KEY (BOD_WRITER)
    REFERENCES BOARD_USERS(USER_ID)
);

-- 시퀀스 재생성
CREATE SEQUENCE SEQ_BOARD_NO
START WITH 1
INCREMENT BY 1
NOCACHE;

COMMIT;
