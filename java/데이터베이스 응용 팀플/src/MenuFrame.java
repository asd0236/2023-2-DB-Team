import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuFrame extends JFrame {
    public MenuFrame() {
        setTitle("메뉴 화면");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        JButton employeeButton = new JButton("알바생");
        JButton managerButton = new JButton("관리자");

        employeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 알바생 화면으로 이동
                dispose();
                new EmployeeFrame().setVisible(true);
            }
        });

        managerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 관리자 화면으로 이동
                dispose();
                new ManagerFrame().setVisible(true);
            }
        });

        panel.add(employeeButton);
        panel.add(managerButton);

        getContentPane().add(panel);
        JButton branchButton = new JButton("지점");
        
                branchButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // 지점 화면으로 이동
                        dispose();
                        new BranchFrame().setVisible(true);
                    }
                });
                panel.add(branchButton);
        setLocationRelativeTo(null);
    }
}