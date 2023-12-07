import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableCellRenderer;

public class ManagerFrame {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField[] textFields;

    private Connection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ManagerFrame();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @wbp.parser.entryPoint
     */
    public ManagerFrame() throws SQLException {
        initialize();
        connectToDatabase();
        fetchDataFromDatabase();
    }

    private void initialize() {
        frame = new JFrame("관리자 관리");
        frame.setBounds(100, 100, 800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.white);	// 배경색 흰색

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"관리자번호", "이름"});
        

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        Color buttoncolor = Color.decode("#b2C4da");	// 버튼 색
        JButton checkButton = new JButton("조회");
        checkButton.setBackground(buttoncolor);			// 버튼 색 지정
        checkButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));	// 버튼 글씨 설정
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
                try {
					checkSelectedRow();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
        connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "parttimerm", "1");	// 본인 아이디
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
    public class DB_Conn_Query {
    	   Connection con = null;
    	   String url = "jdbc:oracle:thin:@localhost:1521:XE";
    	   String id = "parttimerm";      String password = "1"; // 본인의 id를 사용해야합니다.
    	   private void DB_Connect() {
    		     try {
    		         con = DriverManager.getConnection(url, id, password);
    		         System.out.println("DB 연결 성공");
    		     } catch (SQLException e) {         System.out.println("Connection Fail");      }
    		   }
    	   // PreparedStatement : GUI에서 특정 관리자를 선택하면 해당 관리자가 관리하는 지점들의 정보를 DB에서 가져와 보여주는 기능
    	   private void sqlRun2_1(int managerId) throws SQLException{   // 단순 검색
        
    		   try { DB_Connect();
    		   // PreparedStatement : GUI에서 특정 관리자를 선택하면 해당 관리자가 관리하는 지점들의 정보를 DB에서 가져와 보여주는 기능
    		   String query = "SELECT 지점코드, 지점명, 주소 FROM BRANCH WHERE 관리자번호 = ?";
    		   PreparedStatement pstmt = con.prepareStatement(query);
    		   pstmt.setInt(1, managerId);
    		   ResultSet rs = pstmt.executeQuery();
		    
    		   while (rs.next()) {
    			   System.out.print("\t" + rs.getString("지점코드"));
    			   System.out.print("\t" + rs.getString("지점명"));
    			   System.out.print("\t" + rs.getString("주소") + '\n');       
    		   }
    		   pstmt.close();    rs.close();
    		   } catch (SQLException e) { e.printStackTrace(); 
    		   } finally {   con.close(); }
	    
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
            
            int option = JOptionPane.showConfirmDialog(frame, message, "날짜 입력", JOptionPane.OK_CANCEL_OPTION);            
            if (option == JOptionPane.OK_OPTION) {
            	
            }
        }
    }

	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
		
	}

}








