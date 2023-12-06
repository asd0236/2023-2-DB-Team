import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginHandler {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String DB_USER = "C##tt1";  //본인 사용자 사용할 것
    private static final String DB_PASSWORD = "1234";  

    public boolean authenticateUser(String enteredUsername, String enteredPassword) {
        // 해시화된 패스워드를 얻기 위한 메서드(데이터 베이스에 해시로 암호화 된 정보가 전송되서 그냥 해시 뺌)
        //String hashedPassword = hashPassword(enteredPassword);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM MANAGER WHERE 이름 = ? AND 관리자번호 = ?";   
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, enteredUsername);
                preparedStatement.setString(2, enteredPassword);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();  // 결과가 있으면 로그인 성공
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // 예외 발생 시 로그인 실패
        }
    }

    // 패스워드를 해시화하는 메서드
    /*private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                stringBuilder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }*/
}