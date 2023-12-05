import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class MenuFrame extends JFrame {
    public MenuFrame() {
        setTitle("메뉴 화면");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        JButton employeeButton = new JButton("알바생 관리");
        employeeButton.setBounds(331, 214, 200, 100);
        JButton managerButton = new JButton("관리자");
        managerButton.setBounds(48, 214, 200, 100);
        JButton CashButton = new JButton("급여 관리");
        CashButton.setBounds(48, 49, 200, 100);

        employeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 알바생 화면으로 이동
                dispose();
				try {
					new EmployeeFrame().setVisible(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
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
        managerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 급여 관리 화면으로 이동
                dispose();
                new ManagerFrame().setVisible(true);
            }
        });
        
        panel.setLayout(null);

        panel.add(employeeButton);
        panel.add(managerButton);
        panel.add(CashButton);

        getContentPane().add(panel);
        JButton branchButton = new JButton("지점 관리");
        branchButton.setBounds(331, 49, 200, 100);
        
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