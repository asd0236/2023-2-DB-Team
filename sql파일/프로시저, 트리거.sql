-- 1. 저장 프로시저
-- ‘배정’ 테이블과 ‘근무기록’ 테이블을 비교해 한 알바생이 성실히 근무하였는지 여부를 알 수 있는 기능을 수행(배정일과 근무 날짜를 비교했을 때,
-- 모두 출근을 하였으면 “배정받은 0일 모두 근무하였습니다.”를 띄우고, 며칠 빼먹었으면 “배정받은 0일 중 0일을 근무하였습니다.”를 띄우고,
-- 모든 날짜에 근무하지 않았으면 “근무한 기록이 존재하지 않습니다.”를 띄운다.)

CREATE OR REPLACE PROCEDURE CHECK_WORK_RECORD (
    p_alba_number VARCHAR2
) AS
BEGIN
    DECLARE
        v_total_days NUMBER;
        v_worked_days NUMBER := 0;
    BEGIN
        SELECT COUNT(DISTINCT 배정일) INTO v_total_days
        FROM ASSIGNMENT
        WHERE 알바생번호 = p_alba_number;

        SELECT COUNT(DISTINCT 근무날짜) INTO v_worked_days
        FROM WORKRECORD
        WHERE 알바생번호 = p_alba_number;

        IF v_total_days = 0 THEN
            DBMS_OUTPUT.PUT_LINE('근무한 기록이 존재하지 않습니다.');
        ELSIF v_worked_days = v_total_days THEN
            DBMS_OUTPUT.PUT_LINE('배정받은 ' || v_total_days || '일 모두 근무하였습니다.');
        ELSE
            DBMS_OUTPUT.PUT_LINE('배정받은 ' || v_total_days || '일 중 ' || v_worked_days || '일을 근무하였습니다.');
        END IF;
    END;
END;
/


SET SERVEROUTPUT ON;
EXEC CHECK_WORK_RECORD('00000012');


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
