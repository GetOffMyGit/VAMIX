import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;


public class MenuBar extends JPanel {
	private JButton _btn1 = new JButton("File");
	private JButton _btn2 = new JButton("Edit");
	private JButton _btn3 = new JButton("Tools");
	private JButton _btn4 = new JButton("Preferences");
	private JButton _btn5 = new JButton("Help");
	
	public MenuBar() {
		setLayout(new GridLayout(1, 0));
		
		add(_btn1);
		add(_btn2);
		add(_btn3);
		add(_btn4);
		add(_btn5);		
	}
}
