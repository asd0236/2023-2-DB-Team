import java.awt.*;	// 추가
import javax.swing.JFrame;

public class BranchFrame {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BranchFrame window = new BranchFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BranchFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.white);	// 배경색 흰색
		
		frame.setResizable(false); // 화면 크기 고정 추가
	}

	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
		
	}

}
