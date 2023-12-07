import javax.swing.*;
import java.awt.*;	// 추가
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;

import oracle.jdbc.OracleTypes;

import java.awt.*;
import java.sql.*;
import java.util.Vector;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class WorkHistoryFrame {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField[] textFields;

    private Connection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new WorkHistoryFrame();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @wbp.parser.entryPoint
     */
    public WorkHistoryFrame() throws SQLException {
        initialize();
        connectToDatabase();
        fetchDataFromDatabase();
    }

    private void initialize() {
        frame = new JFrame("출근, 퇴근기록 조회");	// 이름 수정
        frame.setBounds(100, 100, 800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.white);	// 배경색 흰색


        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"알바생번호", "이름"});

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        Color buttoncolor = Color.decode("#b2C4da");	// 버튼 색
        JButton checkButton = new JButton("조회");
        checkButton.setBackground(buttoncolor);			// 버튼 색 지정
        checkButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));	// 버튼 글씨 설정
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	new WorkHistoryFrame2().setVisible(true);
                /*try {
					checkSelectedRow();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
            }
        });

        JPanel inputPanel = new JPanel(new GridLayout(1, 8));
        inputPanel.add(checkButton);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);
        
        JButton btnNewButton = new JButton("뒤로가기");
        btnNewButton.setBackground(buttoncolor);			// 버튼 색 지정
        btnNewButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));	// 버튼 글씨 설정
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		frame.dispose();
                try {
					new EmployeeFrame().setVisible(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
        });
        
        
        inputPanel.add(btnNewButton);

        frame.setVisible(true);
        frame.setResizable(false); // 화면 크기 고정 추가
    }


	private void connectToDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "parttimerm", "1");
    }

    private void fetchDataFromDatabase() {
        String query = "SELECT * FROM PARTTIMER";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getString("알바생번호"));
                row.add(resultSet.getString("이름"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkSelectedRow() throws SQLException {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            textFields = new JTextField[2];

            for (int i = 0; i < 2; i++) {
                textFields[i] = new JTextField((String) table.getValueAt(selectedRow, i));
            }

            Object[] message = {
                    "알바생번호:", textFields[0],
                    "이름:", textFields[1],
                   
            };

            //int option = JOptionPane.showConfirmDialog(frame, message, "날짜 입력", JOptionPane.OK_CANCEL_OPTION);
            //if (option == JOptionPane.OK_OPTION) {
            	/*  출퇴근 기록 프로시저 GET_WORK_HISTORY
            	// 프로시저 호출을 위한 CallableStatement 생성
                String callStatement = "{call GET_WORK_HISTORY(?, ?, ?, ?)}";
                CallableStatement callableStatement = connection.prepareCall(callStatement);
                String selectedAlba = "00000001";

                // 입력 매개변수 설정
                callableStatement.setString(1, selectedAlba);
                callableStatement.setDate(2, new Date(System.currentTimeMillis())); // 시작 날짜
                callableStatement.setDate(3, new Date(System.currentTimeMillis())); // 종료 날짜
                callableStatement.registerOutParameter(4, OracleTypes.CURSOR);

                // 프로시저 실행
                callableStatement.execute();

                // 결과 처리
                ResultSet resultSet = (ResultSet) callableStatement.getObject(4);
                
               
                resultSet.close();
                callableStatement.close();
                connection.close();*/
            	
            	
            	/*String updateQuery = "UPDATE PARTTIMER SET 알바생번호 = ?, 이름 = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    for (int i = 0; i < 2; i++) {
                        preparedStatement.setString(i + 1, textFields[i].getText());
                    }
                    preparedStatement.setString(7, (String) table.getValueAt(selectedRow, 0));
                    preparedStatement.executeUpdate();

                    // Update data in JTable
                    for (int i = 0; i < 2; i++) {
                        tableModel.setValueAt(textFields[i].getText(), selectedRow, i);
                    }
                    JOptionPane.showMessageDialog(null, "수정 완료");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "수정 오류");
                }*/
            //}
        }
    }

	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
		
	}

}








