import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("로그인 화면");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        JLabel usernameLabel = new JLabel("아이디:");
        JLabel passwordLabel = new JLabel("패스워드:");
        usernameField = new JTextField(10);
        passwordField = new JPasswordField(10);
        JButton loginButton = new JButton("로그인");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredUsername = usernameField.getText();
                String enteredPassword = String.valueOf(passwordField.getPassword());
                
                // 오류로 잠시 로그인 주석 처리(오류는 안나는데 계속 아이디 비번을 못 찾는듯)
                //LoginHandler loginHandler = new LoginHandler();
                //if (loginHandler.authenticateUser(enteredUsername, enteredPassword)) {
                    // 로그인 성공 시 다음 화면으로 이동
                    dispose();  // 현재 프레임 닫기
                    new MenuFrame().setVisible(true);
                //} else {
                //    JOptionPane.showMessageDialog(null, "아이디 혹은 패스워드가 잘못되었습니다.");
                //}
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);

        add(panel);
        setLocationRelativeTo(null);  // 화면 중앙에 표시
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
