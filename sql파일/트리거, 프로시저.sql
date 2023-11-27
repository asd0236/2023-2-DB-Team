-- 1. ���� ���ν���
-- ���˹ٻ������̺��� �����Ͽ� ȣ�Ǻ��� �˹ٻ� ���� ����� �� �� ���� ��ȣ�ǡ����̺� ���˹ٻ� ���� �Ӽ����� �Է��ϴ� ��� ����
-- ��ASSIGNMENT�����̺��� ������ 'ROOM'���̺� �ִ� ȣ�ǹ�ȣ �� �˹ٻ� ���� ����� �� �� ���� ��ROOM�����̺� ���˹ٻ����� �Ӽ����� �Է��ϴ� ����� ����, ȣ�ǹ�ȣ���� �˹ٻ����� �ּ� 1���� �ִ� 2����� ��ġ.


ALTER TABLE ROOM
ADD �˹ٻ��� NUMBER(4);



CREATE OR REPLACE PROCEDURE UPDATE_ROOM_PARTTIMER_COUNT AS
BEGIN
  FOR room_rec IN (SELECT DISTINCT r.�����ڵ�, r.ȣ�ǹ�ȣ, r.�˹ٻ���
                   FROM ROOM r)
  LOOP
    DECLARE
      v_current_alba_count NUMBER;
      v_remaining_alba_count NUMBER;
    BEGIN
      SELECT COUNT(DISTINCT a.�˹ٻ���ȣ) INTO v_current_alba_count
      FROM ASSIGNMENT a
      WHERE a.�����ڵ� = room_rec.�����ڵ�
        AND a.ȣ�ǹ�ȣ = room_rec.ȣ�ǹ�ȣ;

      v_remaining_alba_count := GREATEST(2 - v_current_alba_count, 0);

      FOR i IN 1..v_remaining_alba_count
      LOOP
        INSERT INTO ASSIGNMENT (������, �˹ٻ���ȣ, �����ڵ�, ȣ�ǹ�ȣ)
        SELECT SYSDATE, p.�˹ٻ���ȣ, room_rec.�����ڵ�, room_rec.ȣ�ǹ�ȣ
        FROM PARTTIMER p
        WHERE p.�˹ٻ���ȣ NOT IN (SELECT a.�˹ٻ���ȣ
                                  FROM ASSIGNMENT a
                                  WHERE a.�����ڵ� = room_rec.�����ڵ�
                                    AND a.ȣ�ǹ�ȣ = room_rec.ȣ�ǹ�ȣ)
        AND ROWNUM = 1; 
      END LOOP;
    END;

    UPDATE ROOM
    SET �˹ٻ��� = (SELECT COUNT(DISTINCT a.�˹ٻ���ȣ)
                    FROM ASSIGNMENT a
                    WHERE a.�����ڵ� = room_rec.�����ڵ�
                      AND a.ȣ�ǹ�ȣ = room_rec.ȣ�ǹ�ȣ)
    WHERE �����ڵ� = room_rec.�����ڵ�
      AND ȣ�ǹ�ȣ = room_rec.ȣ�ǹ�ȣ;
  END LOOP;
END;
/

EXEC UPDATE_ROOM_PARTTIMER_COUNT;
/

--SELECT * FROM ROOM;

--SELECT �����ڵ�, COUNT(*) AS �˹ٻ���
--FROM ASSIGNMENT
--GROUP BY �����ڵ�;



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
-- �޿����̺��� ����(����, ����, ����)�� ������ �� ���ų����� �ݿ��ϴ� ��� ����(��, ���ο� �޿� ������ �ԷµǸ� �ش� column�� �����ϰ�, �޿��� ���ݵǸ� ���� ���θ� ����)

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

-- �˹ٻ����̺��� ����(����, ����, ����)�� ������ �� ���ų����� ȣ�� ���̺� �ݿ��ϴ� ��� ����(��, �˹ٻ� ������ ����, ����, ���ŵǸ� ������ ȣ���� �˹ٻ����� ����)

CREATE OR REPLACE TRIGGER ROOM_UPDATE_TRIGGER
    AFTER INSERT OR UPDATE OR DELETE
    ON ROOM
BEGIN
    UPDATE_ROOM_PARTTIMER_COUNT();
END;
/