import javax.swing.*;
import java.awt.*;	// 추가
import java.awt.Container;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
    	Container c = getContentPane();
        setTitle("관리자 로그인");
        setSize(600, 430);	// 세로 30 증가
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c.setBackground(Color.white);	// 배경색 흰색 

        // JPanel panel = new JPanel();
        JLabel usernameLabel = new JLabel("이름  :");
        usernameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));	// 라벨 글씨 설정
        usernameLabel.setBounds(225, 259, 76, 67);	// 아래로 내리고 크기 조정
        JLabel passwordLabel = new JLabel("관리자번호  :");
        passwordLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));	// 라벨 글씨 설정
        passwordLabel.setBounds(181, 322, 88, 25);	// 아래로 내리고 크기 조정
        usernameField = new JTextField(10);
        usernameField.setBounds(277, 284, 97, 21);	// 아래로 내리고 크기 조정
        passwordField = new JPasswordField(10);
        passwordField.setBounds(277, 326, 97, 21);	// 아래로 내리고 크기 조정


        // 이미지 추가
        ImageIcon icon = new ImageIcon(LoginFrame.class.getResource("/office.jpg"));
        Image img = icon.getImage();
        
        // 이미지 크기 조절
        Image updateImg = img.getScaledInstance(500, 300, Image.SCALE_SMOOTH);
        ImageIcon updateIcon = new ImageIcon(updateImg);
        
        JLabel imgLabel = new JLabel(updateIcon);
        imgLabel.setBounds(0, 0, 600, 250); // 이미지 위치와 크기 명시
        c.add(imgLabel);

        /* panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField); */
        c.setLayout(null);	// c로 수정

        c.add(usernameLabel);
        c.add(usernameField);
        c.add(passwordLabel);
        c.add(passwordField);
        
        JButton loginButton = new JButton("로그인");
        Color buttoncolor = Color.decode("#b2C4da");	// 버튼 색
        loginButton.setBackground(buttoncolor);			// 버튼 색 지정
        loginButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));	// 버튼 글씨 설정
        loginButton.setBounds(414, 324, 100, 25);		// 아래로 내리고 크기 조정
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
                            JOptionPane.showMessageDialog(null, "이름 혹은 관리자번호를 확인해주세요");
                        }
                    }
                });
                c.add(loginButton);
        setLocationRelativeTo(null);  // 화면 중앙에 표시
        setResizable(false); // 화면 크기 고정 추가
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
