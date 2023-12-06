import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import oracle.jdbc.OracleTypes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class CashFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField startDateField;
    private JTextField endDateField;

    public CashFrame() {
        // GUI 초기화
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // JTable 및 모델 초기화
        model = new DefaultTableModel();
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 알바생 선택을 위한 JComboBox 추가
        String[] albaList = {"00000001", "알바생2", "알바생3"}; // 알바생 목록은 필요에 따라 수정
        JComboBox<String> albaComboBox = new JComboBox<>(albaList);
        albaComboBox.addActionListener(e -> {
            String selectedAlba = (String) albaComboBox.getSelectedItem();
            showWorkHistory(selectedAlba);
        });
        add(albaComboBox, BorderLayout.NORTH);

        // 시작 날짜 및 종료 날짜를 입력받을 JTextField 추가
        JPanel datePanel = new JPanel();
        startDateField = new JTextField(10);
        endDateField = new JTextField(10);
        JButton showHistoryButton = new JButton("Show History");
        showHistoryButton.addActionListener(e -> showWorkHistory((String) albaComboBox.getSelectedItem()));
        datePanel.add(new JLabel("Start Date:"));
        datePanel.add(startDateField);
        datePanel.add(new JLabel("End Date:"));
        datePanel.add(endDateField);
        datePanel.add(showHistoryButton);
        add(datePanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void showWorkHistory(String selectedAlba) {
        // JDBC 연결 정보
        String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:XE";
        String user = "C##tt1";
        String password = "1234";

        try {
            // JDBC 드라이버 로드
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // 데이터베이스 연결
            Connection connection = DriverManager.getConnection(jdbcUrl, user, password);

            // 프로시저 호출을 위한 CallableStatement 생성
            String callStatement = "{call GET_WORK_HISTORY(?, ?, ?, ?)}";
            CallableStatement callableStatement = connection.prepareCall(callStatement);

            // 입력 매개변수 설정
            callableStatement.setString(1, selectedAlba);
            callableStatement.setDate(2, Date.valueOf(startDateField.getText()));
            callableStatement.setDate(3, Date.valueOf(endDateField.getText()));
            callableStatement.registerOutParameter(4, OracleTypes.CURSOR);

            // 프로시저 실행
            callableStatement.execute();

            // 결과 처리
            ResultSet resultSet = (ResultSet) callableStatement.getObject(4);

            // JTable 모델 초기화
            model.setRowCount(0);
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getDate("근무날짜"));
                row.add(resultSet.getString("출근시간"));
                row.add(resultSet.getString("퇴근시간"));
                row.add(resultSet.getString("총근무시간"));
                model.addRow(row);
            }

            // 자원 해제
            resultSet.close();
            callableStatement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CashFrame());
    }
}
