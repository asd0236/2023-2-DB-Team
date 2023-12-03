import java.awt.EventQueue;

import javax.swing.*;

import javax.swing.JFrame;
public class LoginScreen {

    private static JTextField userIDField;
    private static JPasswordField userPasswordField;

    public static void main(String[] args) {
        JFrame frame = new JFrame("알바생 관리 프로그램");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);
        

        JLabel userLabel = new JLabel("아이디 : ");
        userLabel.setBounds(119, 80, 80, 25);
        panel.add(userLabel);

        userIDField = new JTextField(20);
        userIDField.setBounds(186, 80, 165, 25);
        panel.add(userIDField);

        JLabel passwordLabel = new JLabel("비밀번호 : ");
        passwordLabel.setBounds(119, 124, 80, 25);
        panel.add(passwordLabel);

        userPasswordField = new JPasswordField(20);
        userPasswordField.setBounds(186, 124, 165, 25);
        panel.add(userPasswordField);

        JButton loginButton = new JButton("login");
        loginButton.setBounds(378, 124, 80, 25);
        panel.add(loginButton);

        loginButton.addActionListener(e -> {
            String userID = userIDField.getText();
            String password = String.valueOf(userPasswordField.getPassword());

            if (isLoginValid(userID, password)) { // 로그인 정보 검증
            	// 로그인 정보가 맞을 경우 다른 클래스의 패널을 띄우는 코드
                JFrame nextFrame = new JFrame("알바생 리스트 화면");
                nextFrame.setSize(600, 400);
                nextFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                nextFrame.add(new ListScreen()); 
                nextFrame.setVisible(true);
            } else {
                // 로그인 정보가 틀릴 경우 팝업창 띄우기
                JOptionPane.showMessageDialog(null, "아이디 혹은 패스워드가 잘못되었습니다.");
            }
        });
    }

    // 로그인 정보 검증 메소드
    private static boolean isLoginValid(String userID, String password) {
        // 실제로는 데이터베이스에서 아이디와 패스워드를 검증하는 코드를 작성해야 함.
        // 여기서는 단순히 아이디와 패스워드가 동일한 경우 로그인이 성공한 것으로 간주.
        return userID.equals(password);
    }
}