import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.*;

import oracle.jdbc.OracleTypes;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class WorkHistoryFrame2 extends JFrame {
    private JTextField albaIdField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextArea resultArea;

    public WorkHistoryFrame2() {
        super("Work History GUI");
        getContentPane().setLayout(null);

        // Components
        JLabel label = new JLabel("알바생번호:");
        label.setBounds(102, 15, 84, 59);
        getContentPane().add(label);
        albaIdField = new JTextField();
        albaIdField.setBounds(194, 26, 236, 37);
        getContentPane().add(albaIdField);

        JLabel label_1 = new JLabel("시작 날짜 (yy-MM-dd):");
        label_1.setBounds(36, 67, 137, 59);
        getContentPane().add(label_1);
        startDateField = new JTextField();
        startDateField.setBounds(194, 78, 236, 37);
        getContentPane().add(startDateField);

        JLabel label_2 = new JLabel("종료 날짜 (yy-MM-dd):");
        label_2.setBounds(36, 120, 137, 59);
        getContentPane().add(label_2);
        endDateField = new JTextField();
        endDateField.setBounds(194, 131, 236, 37);
        getContentPane().add(endDateField);

        resultArea = new JTextArea();
        resultArea.setBounds(0, 231, 585, 129);
        resultArea.setEditable(false);
        getContentPane().add(resultArea);

        JButton submitButton = new JButton("확인");
        submitButton.setBounds(321, 178, 109, 37);
        getContentPane().add(submitButton);
        
        JButton btnNewButton = new JButton("뒤로가기");
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
				dispose();
        	}
        });
        btnNewButton.setBounds(464, 178, 109, 37);
        getContentPane().add(btnNewButton);

        // Event handling
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getWorkHistory();
            }
        });
    }

    private void getWorkHistory() {
        String albaId = albaIdField.getText();
        String startDateStr = startDateField.getText();
        String endDateStr = endDateField.getText();

        try {
            // Parse dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            // Call stored procedure
            String url = "jdbc:oracle:thin:@localhost:1521:XE";
            String user = "C##tt1";
            String password = "1234";

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                CallableStatement callableStatement = connection.prepareCall("{call GET_WORK_HISTORY(?, ?, ?, ?)}");
                callableStatement.setString(1, albaId);
                callableStatement.setTimestamp(2, new Timestamp(startDate.getTime()));
                callableStatement.setTimestamp(3, new Timestamp(endDate.getTime()));
                callableStatement.registerOutParameter(4, OracleTypes.CURSOR);

                callableStatement.execute();

                // Retrieve result set
                ResultSet resultSet = (ResultSet) callableStatement.getObject(4);

                // Display result
                StringBuilder resultText = new StringBuilder("근무날짜\t출근시간\t퇴근시간\t총근무시간\n");
                while (resultSet.next()) {
                    resultText.append(resultSet.getDate("근무날짜")).append("\t")
                            .append(resultSet.getTime("출근시간")).append("\t")
                            .append(resultSet.getTime("퇴근시간")).append("\t")
                            .append(resultSet.getDouble("총근무시간")).append("\n");
                }

                resultArea.setText(resultText.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resultArea.setText("에러 발생: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        WorkHistoryFrame2 gui = new WorkHistoryFrame2();
        gui.setSize(600, 400);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
    }
}
