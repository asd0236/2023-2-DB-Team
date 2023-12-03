import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerFrame extends JFrame {
    private JList<String> managerList;

    public ManagerFrame() {
        setTitle("관리자 관리 화면");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        // 데이터베이스에서 관리자 리스트를 가져오는 메서드 호출
        List<String> managerData = retrieveManagerData();
        managerList = new JList<>(managerData.toArray(new String[0]));

        managerList.addListSelectionListener(e -> {
            // 선택한 관리자의 지점 정보를 가져오는 메서드 호출
            String selectedManager = managerList.getSelectedValue();
            String branchInfo = retrieveBranchInfo(selectedManager);
            JOptionPane.showMessageDialog(null, branchInfo);
        });

        panel.add(new JScrollPane(managerList));

        add(panel);
        setLocationRelativeTo(null);
    }

    // 데이터베이스에서 관리자 리스트를 가져오는 메서드 (임시로 더미 데이터 반환)
    private List<String> retrieveManagerData() {
        // 여기에 데이터베이스 연동 및 쿼리 수행 로직을 추가
        // 임시로 더미 데이터 반환
        return List.of("관리자1", "관리자2", "관리자3");
    }

    // 선택한 관리자의 지점 정보를 가져오는 메서드 (임시로 더미 데이터 반환)
   