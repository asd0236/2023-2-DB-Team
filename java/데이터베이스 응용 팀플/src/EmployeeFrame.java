import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import java.sql.SQLException;

public class EmployeeFrame {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField[] textFields;

    private Connection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new EmployeeFrame();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @wbp.parser.entryPoint
     */
    public EmployeeFrame() throws SQLException {
        initialize();
        connectToDatabase();
        fetchDataFromDatabase();
    }

    private void initialize() {
        frame = new JFrame("알바생 관리");
        frame.setBounds(100, 100, 800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"알바생번호", "이름", "전화번호", "주소", "계좌번호", "멘토알바생번호"});

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton deleteButton = new JButton("삭제");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedRow();
            }
        });

        JButton editButton = new JButton("수정");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedRow();
            }
        });

        JButton addButton = new JButton("추가");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewRow();
            }
        });

        JPanel inputPanel = new JPanel(new GridLayout(1, 8));
        inputPanel.add(addButton);
        inputPanel.add(deleteButton);
        inputPanel.add(editButton);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);
        
        JButton btnNewButton = new JButton("뒤로가기");
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		frame.dispose();
                new MenuFrame().setVisible(true);
        	}
        });
        
        JButton btnNewButton_1 = new JButton("출,퇴근기록 조회");
        btnNewButton_1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		frame.dispose();
                try {
					new WorkHistoryFrame().setVisible(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
        });
        inputPanel.add(btnNewButton_1);
        inputPanel.add(btnNewButton);

        frame.setVisible(true);
    }


	private void connectToDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "C##tt1", "1234");
    }

    private void fetchDataFromDatabase() {
        String query = "SELECT * FROM PARTTIMER";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getString("알바생번호"));
                row.add(resultSet.getString("이름"));
                row.add(resultSet.getString("전화번호"));
                row.add(resultSet.getString("주소"));
                row.add(resultSet.getString("계좌번호"));
                row.add(resultSet.getString("멘토알바생번호"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int option = JOptionPane.showConfirmDialog(frame, "정말 삭제하시겠습니까?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                String valueColumn1 = (String) table.getValueAt(selectedRow, 0);

                String deleteQuery = "DELETE FROM PARTTIMER WHERE 알바생번호 = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                    preparedStatement.setString(1, valueColumn1);
                    preparedStatement.executeUpdate();

                    // Remove row from JTable
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(null, "삭제 완료");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "삭제 오류");
                }
            }
        }
    }

    private void editSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            textFields = new JTextField[6];

            for (int i = 0; i < 6; i++) {
                textFields[i] = new JTextField((String) table.getValueAt(selectedRow, i));
            }

            Object[] message = {
                    "알바생번호:", textFields[0],
                    "이름:", textFields[1],
                    "전화번호:", textFields[2],
                    "주소:", textFields[3],
                    "계좌번호:", textFields[4],
                    "멘토알바생번호:", textFields[5]
            };

            int option = JOptionPane.showConfirmDialog(frame, message, "알바생 정보 수정", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String updateQuery = "UPDATE PARTTIMER SET 알바생번호 = ?, 이름 = ?, 전화번호 = ?, 주소 = ?, 계좌번호= ?, 멘토알바생번호 = ? WHERE 알바생번호 = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    for (int i = 0; i < 6; i++) {
                        preparedStatement.setString(i + 1, textFields[i].getText());
                    }
                    preparedStatement.setString(7, (String) table.getValueAt(selectedRow, 0));
                    preparedStatement.executeUpdate();

                    // Update data in JTable
                    for (int i = 0; i < 6; i++) {
                        tableModel.setValueAt(textFields[i].getText(), selectedRow, i);
                    }
                    JOptionPane.showMessageDialog(null, "수정 완료");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "수정 오류");
                }
            }
        }
    }

    private void addNewRow() {
        textFields = new JTextField[6];

        for (int i = 0; i < 6; i++) {
            textFields[i] = new JTextField();
        }

        Object[] message = {
                "알바생번호:", textFields[0],
                "이름:", textFields[1],
                "전화번호:", textFields[2],
                "주소:", textFields[3],
                "계좌번호:", textFields[4],
                "멘토알바생번호:", textFields[5]
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "알바생 추가", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String insertQuery = "INSERT INTO PARTTIMER (알바생번호,이름, 전화번호, 주소, 계좌번호, 멘토알바생번호) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                for (int i = 0; i < 6; i++) {
                    preparedStatement.setString(i + 1, textFields[i].getText());
                }
                preparedStatement.executeUpdate();

                // Add new row to JTable
                Vector<Object> newRow = new Vector<>();
                for (int i = 0; i < 6; i++) {
                    newRow.add(textFields[i].getText());
                }
                JOptionPane.showMessageDialog(null, "추가 완료");
                tableModel.addRow(newRow);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "추가 오류");
            }
        }
    }

	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
		
	}

}








