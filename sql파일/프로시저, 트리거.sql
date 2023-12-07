-- 1. 저장 프로시저
-- ‘근무기록’ 테이블을 통해 일정 기간동안의 알바생의 출,퇴근 기록을 반환하는 기능을 수행

CREATE OR REPLACE PROCEDURE GET_WORK_HISTORY(
    p_alba_id VARCHAR2,
    p_start_date DATE,
    p_end_date DATE,
    p_cursor OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_cursor FOR
        SELECT 근무날짜, 출근시간, 퇴근시간, 총근무시간
        FROM WORKRECORD
        WHERE 알바생번호 = p_alba_id
            AND 근무날짜 BETWEEN p_start_date AND p_end_date;
END;
/
commit;


-- 샘플 알바생과 근무기록 데이터 생성
INSERT INTO PARTTIMER VALUES ('00000055', '알바생1', '010-1234-5678', '주소1', '1111-2222', NULL);
INSERT INTO WORKRECORD VALUES (TO_DATE('2023-01-15', 'YYYY-MM-DD'), TO_TIMESTAMP('08:00:00', 'HH24:MI:SS'), TO_TIMESTAMP('17:00:00', 'HH24:MI:SS'), 9, '00000055');
INSERT INTO WORKRECORD VALUES (TO_DATE('2023-01-16', 'YYYY-MM-DD'), TO_TIMESTAMP('09:00:00', 'HH24:MI:SS'), TO_TIMESTAMP('18:00:00', 'HH24:MI:SS'), 9, '00000055');


-- 테스트용 쿼리

SET SERVEROUTPUT ON;

DECLARE
    p_alba_id VARCHAR2(8) := '00000055';
    p_start_date DATE := TO_DATE('2023-01-01', 'YYYY-MM-DD');
    p_end_date DATE := TO_DATE('2023-01-31', 'YYYY-MM-DD');
    p_cursor SYS_REFCURSOR;

    v_work_date DATE;
    v_start_time TIMESTAMP;
    v_end_time TIMESTAMP;
    v_total_hours NUMBER;
BEGIN
    -- 프로시저 호출
    GET_WORK_HISTORY(p_alba_id, p_start_date, p_end_date, p_cursor);
    
    -- 결과 출력
    LOOP
        FETCH p_cursor INTO v_work_date, v_start_time, v_end_time, v_total_hours;
        EXIT WHEN p_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('근무날짜: ' || TO_CHAR(v_work_date, 'YYYY-MM-DD') ||
                             ' 출근시간: ' || TO_CHAR(v_start_time, 'HH24:MI:SS') ||
                             ' 퇴근시간: ' || TO_CHAR(v_end_time, 'HH24:MI:SS') ||
                             ' 총근무시간: ' || v_total_hours || ' 시간');
    END LOOP;
    CLOSE p_cursor;
END;
/

----------------------------------------------------------------------------------------

-- 특정 지점 및 호실의 알바생들을 모두 다른 지점의 호실로 이동시키는 기능을 수행


CREATE OR REPLACE PROCEDURE MOVE_PARTTIMERS(
    from_branch_code VARCHAR2,
    from_room_number NUMBER,
    to_branch_code VARCHAR2,
    to_room_number NUMBER
) AS
BEGIN
    -- 이동 대상 알바생 선택
    UPDATE ASSIGNMENT
    SET 지점코드 = to_branch_code,
        호실번호 = to_room_number
    WHERE (지점코드, 호실번호, 알바생번호) IN (
        SELECT DISTINCT from_branch_code, from_room_number, 알바생번호
        FROM ASSIGNMENT
        WHERE 지점코드 = from_branch_code
            AND 호실번호 = from_room_number
);

    -- 이동 후에 이동한 지점 및 호실의 알바생 수 업데이트
    UPDATE ROOM
    SET 알바생수 = (
        SELECT COUNT(DISTINCT ASSIGNMENT.알바생번호)
        FROM ASSIGNMENT
        WHERE ASSIGNMENT.지점코드 = ROOM.지점코드
        AND ASSIGNMENT.호실번호 = ROOM.호실번호
    )
    WHERE 지점코드 = to_branch_code
        AND 호실번호 = to_room_number;
END;
/

BEGIN
    MOVE_PARTTIMERS('101', 1001, '101', 1002);
END;
/
-- BEGIN
--     MOVE_PARTTIMERS('현재_지점_코드', 현재_호실_번호, '이동_할_지점_코드', 이동_할_호실_번호);
-- END;



-----------------------------------------------------------------------------------

-- 2. 트리거
-- 급여테이블이 갱신(삽입, 삭제, 변경)될 때마다 그 갱신내용을 반영하는 기능 수행
-- (예, 새로운 급여 정보가 입력되면 해당 column을 삽입하고, 급여가 지금되면 지급 여부를 갱신)

CREATE OR REPLACE TRIGGER SALARY_UPDATE_TRIGGER
    AFTER INSERT OR UPDATE OR DELETE
    ON SALARY
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        -- 새로운 행이 삽입될 때 수행할 작업
        UPDATE SALARY SET 지급여부 = 'O' WHERE 급여일자 = :NEW.급여일자;
    END IF;
END;
/


----------------------------------------------------------------------------------------

-- 근무기록 테이블에서 한 알바생이 근무한 기록을 삭제하면 해당하는 날짜의 배정 테이블에서도 배정받은 기록을 삭제하는 기능 수행

CREATE OR REPLACE TRIGGER DELETE_ASSIGNMENT_AFTER_WORKRECORD_DELETE
AFTER DELETE ON WORKRECORD
FOR EACH ROW
BEGIN
    -- 해당 알바생의 해당 날짜에 대한 배정 기록 삭제
    DELETE FROM ASSIGNMENT
    WHERE 알바생번호 = :OLD.알바생번호
        AND 배정일 = TRUNC(:OLD.근무날짜);
END;
/


DELETE FROM WORKRECORD
WHERE 알바생번호 = '00000006'
    AND 근무날짜 = TO_DATE('23/02/17');


-- 확인: 해당 알바생의 근무 기록 조회
SELECT *
FROM WORKRECORD
WHERE 알바생번호 = '00000006';

-- 확인: 해당 알바생의 배정 기록 조회
SELECT *
FROM ASSIGNMENT
WHERE 알바생번호 = '00000006';
