-- 1. ���� ���ν���
-- �������� ���̺�� ���ٹ���ϡ� ���̺��� ���� �� �˹ٻ��� ������ �ٹ��Ͽ����� ���θ� �� �� �ִ� ����� ����(�����ϰ� �ٹ� ��¥�� ������ ��,
-- ��� ����� �Ͽ����� ���������� 0�� ��� �ٹ��Ͽ����ϴ�.���� ����, ��ĥ ���Ծ����� ���������� 0�� �� 0���� �ٹ��Ͽ����ϴ�.���� ����,
-- ��� ��¥�� �ٹ����� �ʾ����� ���ٹ��� ����� �������� �ʽ��ϴ�.���� ����.)

CREATE OR REPLACE PROCEDURE CHECK_WORK_RECORD (
    p_alba_number VARCHAR2
) AS
BEGIN
    DECLARE
        v_total_days NUMBER;
        v_worked_days NUMBER := 0;
    BEGIN
        SELECT COUNT(DISTINCT ������) INTO v_total_days
        FROM ASSIGNMENT
        WHERE �˹ٻ���ȣ = p_alba_number;

        SELECT COUNT(DISTINCT �ٹ���¥) INTO v_worked_days
        FROM WORKRECORD
        WHERE �˹ٻ���ȣ = p_alba_number;

        IF v_total_days = 0 THEN
            DBMS_OUTPUT.PUT_LINE('�ٹ��� ����� �������� �ʽ��ϴ�.');
        ELSIF v_worked_days = v_total_days THEN
            DBMS_OUTPUT.PUT_LINE('�������� ' || v_total_days || '�� ��� �ٹ��Ͽ����ϴ�.');
        ELSE
            DBMS_OUTPUT.PUT_LINE('�������� ' || v_total_days || '�� �� ' || v_worked_days || '���� �ٹ��Ͽ����ϴ�.');
        END IF;
    END;
END;
/


SET SERVEROUTPUT ON;
EXEC CHECK_WORK_RECORD('00000012');


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
