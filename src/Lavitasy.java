import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Lavitasy extends JFrame {
	private JPanel _top;
	private JPanel _mid = new MidPanelHolder();
//	private JPanel _bot = new Timeline();
	
	public static void main(String[] args) {
		Lavitasy lavitasy = new Lavitasy();
		lavitasy.createAndShowGUI();
	}
	
	private void createAndShowGUI() {
		setTitle("Lavitasy");
		setPreferredSize(new Dimension(1000, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_top = new MenuBar((MidPanelHolder) _mid);
		
		setLayout(new BorderLayout());
		
		setVisible(true);
		pack();
		
		add(_top, BorderLayout.NORTH);
		add(_mid, BorderLayout.CENTER);
	//	add(_bot, BorderLayout.SOUTH);
	}
}
