import java.awt.EventQueue;

import javax.swing.JFrame;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class EmployeeFrame extends JFrame {
    private JList<String> employeeList;

    public EmployeeFrame() {
        setTitle("알바생 관리");
        setSize(836, 507);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        employeeList = new JList<>(new String[]{"Alice", "Bob", "Charlie"});
        employeeList.setBorder(new LineBorder(new Color(0, 0, 0)));
        employeeList.setBounds(10, 10, 680, 371);
        getContentPane().add(employeeList);

        JButton deleteButton = new JButton("알바생 삭제");
        deleteButton.setBounds(48, 418, 150, 25);
        deleteButton.addActionListener(e -> {
            int selected = employeeList.getSelectedIndex();
            if (selected != -1) {
                ((DefaultListModel<String>) employeeList.getModel()).remove(selected);
            }
        });
        getContentPane().add(deleteButton);
    }
}