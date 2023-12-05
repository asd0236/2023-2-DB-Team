import javax.swing.*;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
    	Container c = getContentPane();
        setTitle("로그인 화면");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // JPanel panel = new JPanel();
        JLabel usernameLabel = new JLabel("아이디 :");
        usernameLabel.setBounds(218, 216, 76, 67);
        JLabel passwordLabel = new JLabel("패스워드 :");
        passwordLabel.setBounds(206, 279, 59, 25);
        usernameField = new JTextField(10);
        usernameField.setBounds(277, 239, 97, 21);
        passwordField = new JPasswordField(10);
        passwordField.setBounds(277, 281, 97, 21);

        /* panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField); */
        getContentPane().setLayout(null);

        c.add(usernameLabel);
        c.add(usernameField);
        c.add(passwordLabel);
        c.add(passwordField);
        
        JButton loginButton = new JButton("로그인");
        loginButton.setBounds(394, 279, 150, 25);
                loginButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String enteredUsername = usernameField.getText();
                        String enteredPassword = String.valueOf(passwordField.getPassword());
                        
                        LoginHandler loginHandler = new LoginHandler();
                        if (loginHandler.authenticateUser(enteredUsername, enteredPassword)) {
                            // 로그인 성공 시 다음 화면으로 이동
                            dispose();  // 현재 프레임 닫기
                            new MenuFrame().setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(null, "아이디 혹은 비밀번호를 확인해주세요");
                        }
                    }
                });
                c.add(loginButton);
        setLocationRelativeTo(null);  // 화면 중앙에 표시
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
