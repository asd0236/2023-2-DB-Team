import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;

import oracle.jdbc.OracleTypes;


public class DB_Conn_Query {
   Connection con = null;
   String url = "jdbc:oracle:thin:@localhost:1521:XE";
   String id = "C##tt1";      String password = "1234"; // 본인의 id를 사용해야합니다.
   
   public DB_Conn_Query() {
     try {
    	Class.forName("oracle.jdbc.driver.OracleDriver");
        System.out.println("드라이버 적재 성공");
     } catch (ClassNotFoundException e) { System.out.println("No Driver."); }  
   }
   
   
   private void DB_Connect() {
     try {
         con = DriverManager.getConnection(url, id, password);
         System.out.println("DB 연결 성공");
     } catch (SQLException e) {         System.out.println("Connection Fail");      }
   }
   
   
   // Statement 2
   // GUI에서 알바생 번호와 특정 기간(조회 시작일, 조회 종료일)을 선택 후 조회 버튼을 누르면 일정 기간 동안의 알바생의 출, 퇴근 기록을 반환하며, 선택한 근무일을 삭제할 수 있다.
   private void sqlRun(String alba_id, String start_date, String end_date) throws SQLException{   
       
	   String query = "SELECT * FROM ASSIGNMENT WHERE 알바생번호 = " + alba_id + 
			   " AND 배정일 BETWEEN TO_DATE('" + start_date + "', 'YYYY-MM-DD') AND TO_DATE('" + end_date + "', 'YYYY-MM-DD')";

    try { DB_Connect();
    //Statement : GUI에서 특정 지점 및 호실을 선택 후 알바생 목록 버튼을 클릭하면 알바생들의 정보를 DB에서 가져와 보여주는 기능
    	  Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(query);
          while (rs.next()) {
              System.out.print("\t" + rs.getDate("배정일"));
              System.out.print("\t" + rs.getString("알바생번호"));
              System.out.print("\t" + rs.getString("지점코드"));
              System.out.print("\t" + rs.getInt("호실번호") + '\n');           }
          stmt.close();    rs.close();
    } catch (SQLException e) { e.printStackTrace(); 
    } finally {   con.close(); }
    
   }
   
   // PreparedStatement 1
   // GUI에서 특정 관리자를 선택하면 해당 관리자가 관리하는 지점들의 정보를 DB에서 가져와 보여주는 기능.
   private void sqlRun2_1(int managerId) throws SQLException{   // 단순 검색
       
	    try { DB_Connect();
		    // PreparedStatement : GUI에서 특정 관리자를 선택하면 해당 관리자가 관리하는 지점들의 정보를 DB에서 가져와 보여주는 기능
		    String query = "SELECT 지점코드, 지점명, 주소 FROM BRANCH WHERE 관리자번호 = ?";
		    PreparedStatement pstmt = con.prepareStatement(query);
		    pstmt.setInt(1, managerId);
		    ResultSet rs = pstmt.executeQuery();
		    
		    while (rs.next()) {
		        System.out.print("\t" + rs.getString("지점코드"));
		        System.out.print("\t" + rs.getString("지점명"));
		        System.out.print("\t" + rs.getString("주소") + '\n');       
		       }
		    pstmt.close();    rs.close();
		} catch (SQLException e) { e.printStackTrace(); 
	    } finally {   con.close(); }
	    
	   }
   
   
// PreparedStatement 2
   //GUI에서 알바생에게 지급된 급여의 금액와 급여 일자를 선택해 데이터베이스에 갱신하는 기능. 지급된 급여 정보가 없다면 새로운 행을 삽입하고, 정보가 있다면 지급여부를 ‘O’ 로 변경
   
   private void sqlRun2_2(String date, int salary, String alba_id, String status) throws SQLException{   // 단순 검색
       
	    try { DB_Connect();

	    	// 알바생을 검색하여 지급여부가 있는지 확인
	    	String query = "SELECT 지급여부 FROM SALARY WHERE 급여일자 = TO_DATE(?, 'YYYY-MM-DD') AND 금액 = ? AND 알바생번호 = ?";
		    
	    	PreparedStatement pstmt = con.prepareStatement(query);
		    pstmt.setString(1, date);
		    pstmt.setInt(2, salary);
		    pstmt.setString(3, alba_id);
	    	
		    ResultSet rs = pstmt.executeQuery();
		    
		    // NULL이 아니라면 지급여부를 O로 변경
		    if(rs.next()) {
		    	query = "UPDATE SALARY SET 지급여부 = 'O' WHERE 급여일자 = TO_DATE(?, 'YYYY-MM-DD') AND 금액 = ? AND 알바생번호 = ?";
		    	pstmt = con.prepareStatement(query);
		    	pstmt.setString(1, date);
			    pstmt.setInt(2, salary);
			    pstmt.setString(3, alba_id);
		    	pstmt.executeUpdate();
		    }
		    
		    // NULL이라면 행 삽입
		    else {
		    	query = "INSERT INTO SALARY VALUES (TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?)";
		    	pstmt = con.prepareStatement(query);
		    	pstmt.setString(1, date);
			    pstmt.setInt(2, salary);
			    pstmt.setString(3, alba_id);
			    pstmt.setString(4, status);
			    pstmt.executeUpdate();
		    }
		    
		    
		    pstmt.close(); rs.close();
		} catch (SQLException e) { e.printStackTrace(); 
	    } finally {   con.close(); }
	    
	   }
   
   // Callable Statement 1
   // 일정 기간동안의 알바생의 출,퇴근 기록을 반환해 주는 프로시저를 사용하여 특정 알바생의 일정 기간 동안의 출 퇴근 기록을 조회하는 기능을 수행 
   private void sqlRun3_callable_1(String alba_id, Date start_date, Date end_date) {   // in parameter
   
	   try { DB_Connect();
		   CallableStatement cstmt = con.prepareCall("{call GET_WORK_HISTORY(?, ?, ?, ?)}");
		   
		   // IN 매개변수
		   
		   cstmt.setString(1, alba_id);
		   cstmt.setDate(2, (java.sql.Date) start_date);
		   cstmt.setDate(3, (java.sql.Date) end_date);
		   
		   //OUT 매개변수
		   cstmt.registerOutParameter(4,  OracleTypes.CURSOR);
		   cstmt.execute();
		   //cstmt.executeQuery();

		   // OUT 매개변수에서 결과셋을 가져오기
           ResultSet rs = (ResultSet) cstmt.getObject(4);
		   
        // 결과셋 출력
           while (rs.next()) {
        	    System.out.println("근무날짜: " + rs.getDate("근무날짜")
        	            + ", 출근시간: " + rs.getTimestamp("출근시간").toString()
        	            + ", 퇴근시간: " + rs.getTimestamp("퇴근시간").toString()
        	            + ", 총근무시간: " + rs.getDouble("총근무시간"));
        	}
           rs.close();
		   cstmt.close();    //con.close();  // finally 없이 
		   System.out.println("저장 프로시저가 실행됨");
	   } catch (SQLException e) { e.printStackTrace(); }
   }

   // Callable Statement 2
   // 특정 호실에 배정된 알바생들의 정보를 보여주는 GUI에서 알바생 이동 버튼을 클릭하면 ①의 일부 저장프로시저인 특정 지점 및 호실의 알바생들을 모두 다른 지점의 호실로 이동시키는 기능을 수행
   public void sqlRun3_callable_2(String fromBranchCode, int fromRoomNumber, String toBranchCode, int toRoomNumber) {
       try {
           // 저장 프로시저 호출
           CallableStatement cstmt = con.prepareCall("{call MOVE_PARTTIMERS(?, ?, ?, ?)}");
           
           // IN 매개변수 설정
           cstmt.setString(1, fromBranchCode);
           cstmt.setInt(2, fromRoomNumber);
           cstmt.setString(3, toBranchCode);
           cstmt.setInt(4, toRoomNumber);

           // 프로시저 실행
           cstmt.execute();

           // 자원 정리
           cstmt.close();
           con.close();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }
   

public static void main(String arg[]) throws SQLException {
       DB_Conn_Query dbconquery = new DB_Conn_Query();
       dbconquery.sqlRun("00000001", "2023-01-01", "2023-12-31"); // 알바생번호, 조회 시작일, 조회 종료일
       dbconquery.sqlRun2_1(10031); // 관리자 번호
       dbconquery.sqlRun2_2("2023-07-11", 153920, "00000006", "X"); // 급여 일자, 갱신될 급여, 알바생 번호, 지급 여부
       dbconquery.sqlRun3_callable_1("00000001", java.sql.Date.valueOf("2023-01-01"), java.sql.Date.valueOf("2023-12-31")); // 알바생 번호, 조회 시작일, 조회 종료일
       dbconquery.sqlRun3_callable_2("132", 1002, "133", 1001); // 현재 지점 코드, 현재 호실 번호, 이동시킬 지점 코드, 이동시킬 호실 번호
    }
}
