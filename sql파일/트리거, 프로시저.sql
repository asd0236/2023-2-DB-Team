-- 1. 저장 프로시저
-- ‘알바생’테이블을 참고하여 호실별로 알바생 수를 계산한 뒤 그 수를 ‘호실’테이블에 ‘알바생 수’ 속성으로 입력하는 기능 수행
-- ‘ASSIGNMENT’테이블을 참고해 'ROOM'테이블에 있는 호실번호 별 알바생 수를 계산한 뒤 그 수를 ‘ROOM’테이블에 ‘알바생수’ 속성으로 입력하는 기능을 수행, 호실번호마다 알바생수를 최소 1명에서 최대 2명까지 배치.


ALTER TABLE ROOM
ADD 알바생수 NUMBER(4);



CREATE OR REPLACE PROCEDURE UPDATE_ROOM_PARTTIMER_COUNT AS
BEGIN
  FOR room_rec IN (SELECT DISTINCT r.지점코드, r.호실번호, r.알바생수
                   FROM ROOM r)
  LOOP
    DECLARE
      v_current_alba_count NUMBER;
      v_remaining_alba_count NUMBER;
    BEGIN
      SELECT COUNT(DISTINCT a.알바생번호) INTO v_current_alba_count
      FROM ASSIGNMENT a
      WHERE a.지점코드 = room_rec.지점코드
        AND a.호실번호 = room_rec.호실번호;

      v_remaining_alba_count := GREATEST(2 - v_current_alba_count, 0);

      FOR i IN 1..v_remaining_alba_count
      LOOP
        INSERT INTO ASSIGNMENT (배정일, 알바생번호, 지점코드, 호실번호)
        SELECT SYSDATE, p.알바생번호, room_rec.지점코드, room_rec.호실번호
        FROM PARTTIMER p
        WHERE p.알바생번호 NOT IN (SELECT a.알바생번호
                                  FROM ASSIGNMENT a
                                  WHERE a.지점코드 = room_rec.지점코드
                                    AND a.호실번호 = room_rec.호실번호)
        AND ROWNUM = 1; 
      END LOOP;
    END;

    UPDATE ROOM
    SET 알바생수 = (SELECT COUNT(DISTINCT a.알바생번호)
                    FROM ASSIGNMENT a
                    WHERE a.지점코드 = room_rec.지점코드
                      AND a.호실번호 = room_rec.호실번호)
    WHERE 지점코드 = room_rec.지점코드
      AND 호실번호 = room_rec.호실번호;
  END LOOP;
END;
/

EXEC UPDATE_ROOM_PARTTIMER_COUNT;
/

--SELECT * FROM ROOM;

--SELECT 지점코드, COUNT(*) AS 알바생수
--FROM ASSIGNMENT
--GROUP BY 지점코드;



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
-- 급여테이블이 갱신(삽입, 삭제, 변경)될 때마다 그 갱신내용을 반영하는 기능 수행(예, 새로운 급여 정보가 입력되면 해당 column을 삽입하고, 급여가 지금되면 지급 여부를 갱신)

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

-- 알바생테이블이 갱신(삽입, 삭제, 변경)될 때마다 그 갱신내용을 호실 테이블에 반영하는 기능 수행(예, 알바생 정보가 삽입, 삭제, 갱신되면 배정된 호실의 알바생수를 갱신)

CREATE OR REPLACE TRIGGER ROOM_UPDATE_TRIGGER
    AFTER INSERT OR UPDATE OR DELETE
    ON ROOM
BEGIN
    UPDATE_ROOM_PARTTIMER_COUNT();
END;
/