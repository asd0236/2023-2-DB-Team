-- 1. ���� ���ν���
-- ���ٹ���ϡ� ���̺��� ���� ���� �Ⱓ������ �˹ٻ��� ��,��� ����� ��ȯ�ϴ� ����� ����

CREATE OR REPLACE PROCEDURE GET_WORK_HISTORY(
    p_alba_id VARCHAR2,
    p_start_date DATE,
    p_end_date DATE,
    p_cursor OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_cursor FOR
        SELECT �ٹ���¥, ��ٽð�, ��ٽð�, �ѱٹ��ð�
        FROM WORKRECORD
        WHERE �˹ٻ���ȣ = p_alba_id
            AND �ٹ���¥ BETWEEN p_start_date AND p_end_date;
END;
/
commit;


-- ���� �˹ٻ��� �ٹ���� ������ ����
INSERT INTO PARTTIMER VALUES ('00000055', '�˹ٻ�1', '010-1234-5678', '�ּ�1', '1111-2222', NULL);
INSERT INTO WORKRECORD VALUES (TO_DATE('2023-01-15', 'YYYY-MM-DD'), TO_TIMESTAMP('08:00:00', 'HH24:MI:SS'), TO_TIMESTAMP('17:00:00', 'HH24:MI:SS'), 9, '00000055');
INSERT INTO WORKRECORD VALUES (TO_DATE('2023-01-16', 'YYYY-MM-DD'), TO_TIMESTAMP('09:00:00', 'HH24:MI:SS'), TO_TIMESTAMP('18:00:00', 'HH24:MI:SS'), 9, '00000055');


-- �׽�Ʈ�� ����

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
    -- ���ν��� ȣ��
    GET_WORK_HISTORY(p_alba_id, p_start_date, p_end_date, p_cursor);
    
    -- ��� ���
    LOOP
        FETCH p_cursor INTO v_work_date, v_start_time, v_end_time, v_total_hours;
        EXIT WHEN p_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('�ٹ���¥: ' || TO_CHAR(v_work_date, 'YYYY-MM-DD') ||
                             ' ��ٽð�: ' || TO_CHAR(v_start_time, 'HH24:MI:SS') ||
                             ' ��ٽð�: ' || TO_CHAR(v_end_time, 'HH24:MI:SS') ||
                             ' �ѱٹ��ð�: ' || v_total_hours || ' �ð�');
    END LOOP;
    CLOSE p_cursor;
END;
/

----------------------------------------------------------------------------------------

-- Ư�� ���� �� ȣ���� �˹ٻ����� ��� �ٸ� ������ ȣ�Ƿ� �̵���Ű�� ����� ����


CREATE OR REPLACE PROCEDURE MOVE_PARTTIMERS(
    from_branch_code VARCHAR2,
    from_room_number NUMBER,
    to_branch_code VARCHAR2,
    to_room_number NUMBER
) AS
BEGIN
    -- �̵� ��� �˹ٻ� ����
    UPDATE ASSIGNMENT
    SET �����ڵ� = to_branch_code,
        ȣ�ǹ�ȣ = to_room_number
    WHERE (�����ڵ�, ȣ�ǹ�ȣ, �˹ٻ���ȣ) IN (
        SELECT DISTINCT from_branch_code, from_room_number, �˹ٻ���ȣ
        FROM ASSIGNMENT
        WHERE �����ڵ� = from_branch_code
            AND ȣ�ǹ�ȣ = from_room_number
);

    -- �̵� �Ŀ� �̵��� ���� �� ȣ���� �˹ٻ� �� ������Ʈ
    UPDATE ROOM
    SET �˹ٻ��� = (
        SELECT COUNT(DISTINCT ASSIGNMENT.�˹ٻ���ȣ)
        FROM ASSIGNMENT
        WHERE ASSIGNMENT.�����ڵ� = ROOM.�����ڵ�
        AND ASSIGNMENT.ȣ�ǹ�ȣ = ROOM.ȣ�ǹ�ȣ
    )
    WHERE �����ڵ� = to_branch_code
        AND ȣ�ǹ�ȣ = to_room_number;
END;
/

BEGIN
    MOVE_PARTTIMERS('101', 1001, '101', 1002);
END;
/
-- BEGIN
--     MOVE_PARTTIMERS('����_����_�ڵ�', ����_ȣ��_��ȣ, '�̵�_��_����_�ڵ�', �̵�_��_ȣ��_��ȣ);
-- END;



-----------------------------------------------------------------------------------

-- 2. Ʈ����
-- �޿����̺��� ����(����, ����, ����)�� ������ �� ���ų����� �ݿ��ϴ� ��� ����
-- (��, ���ο� �޿� ������ �ԷµǸ� �ش� column�� �����ϰ�, �޿��� ���ݵǸ� ���� ���θ� ����)

CREATE OR REPLACE TRIGGER SALARY_UPDATE_TRIGGER
    AFTER INSERT OR UPDATE OR DELETE
    ON SALARY
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        -- ���ο� ���� ���Ե� �� ������ �۾�
        UPDATE SALARY SET ���޿��� = 'O' WHERE �޿����� = :NEW.�޿�����;
    END IF;
END;
/


----------------------------------------------------------------------------------------

-- �ٹ���� ���̺��� �� �˹ٻ��� �ٹ��� ����� �����ϸ� �ش��ϴ� ��¥�� ���� ���̺����� �������� ����� �����ϴ� ��� ����

CREATE OR REPLACE TRIGGER DELETE_ASSIGNMENT_AFTER_WORKRECORD_DELETE
AFTER DELETE ON WORKRECORD
FOR EACH ROW
BEGIN
    -- �ش� �˹ٻ��� �ش� ��¥�� ���� ���� ��� ����
    DELETE FROM ASSIGNMENT
    WHERE �˹ٻ���ȣ = :OLD.�˹ٻ���ȣ
        AND ������ = TRUNC(:OLD.�ٹ���¥);
END;
/


DELETE FROM WORKRECORD
WHERE �˹ٻ���ȣ = '00000006'
    AND �ٹ���¥ = TO_DATE('23/02/17');


-- Ȯ��: �ش� �˹ٻ��� �ٹ� ��� ��ȸ
SELECT *
FROM WORKRECORD
WHERE �˹ٻ���ȣ = '00000006';

-- Ȯ��: �ش� �˹ٻ��� ���� ��� ��ȸ
SELECT *
FROM ASSIGNMENT
WHERE �˹ٻ���ȣ = '00000006';
