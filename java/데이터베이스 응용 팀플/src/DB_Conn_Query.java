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
   
   
   // Statement
   private void sqlRun(String br, int ro) throws SQLException{   // 단순 검색
       // 특정 지점 및 호실의 알바생 목록 - 매개 변수로 지점(br), 호실(ro)의 정보가 들어왔을 때 
	   String query = "SELECT DISTINCT PARTTIMER.알바생번호, PARTTIMER.이름, PARTTIMER.전화번호, PARTTIMER.주소, PARTTIMER.계좌번호, PARTTIMER.멘토알바생번호 " +
               "FROM ASSIGNMENT, PARTTIMER " +
               "WHERE ASSIGNMENT.지점코드 = " + br + " AND ASSIGNMENT.호실번호 = " + ro + " AND ASSIGNMENT.알바생번호 = PARTTIMER.알바생번호";

    try { DB_Connect();
    //Statement : GUI에서 특정 지점 및 호실을 선택 후 알바생 목록 버튼을 클릭하면 알바생들의 정보를 DB에서 가져와 보여주는 기능
    	  Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery(query);
          while (rs.next()) {
              System.out.print("\t" + rs.getString("알바생번호"));
              System.out.print("\t" + rs.getString("이름"));
              System.out.print("\t" + rs.getString("전화번호"));
              System.out.print("\t" + rs.getString("주소"));
              System.out.print("\t" + rs.getString("계좌번호"));
              System.out.print("\t" + rs.getString("멘토알바생번호") + '\n');         
           }
          stmt.close();    rs.close();
    } catch (SQLException e) { e.printStackTrace(); 
    } finally {   con.close(); }
    
   }
   
   // PreparedStatement
   private void sqlRun2(int managerId) throws SQLException{   // 단순 검색
       
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
   
   
   // Callable Statement 1
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
       dbconquery.sqlRun("132", 1002);
       dbconquery.sqlRun2(10031);
       dbconquery.sqlRun3_callable_1("00000001", java.sql.Date.valueOf("2023-01-01"), java.sql.Date.valueOf("2023-12-31"));
       dbconquery.sqlRun3_callable_2("132", 1002, "133", 1001);
    }
}
