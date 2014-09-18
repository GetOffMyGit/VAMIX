import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;


public class Timeline extends JPanel {
	private JButton _volumeControl = new JButton();
	private JButton _timeline = new JButton();
	
	public Timeline() {
		setLayout(new BorderLayout());
		
		_volumeControl.setPreferredSize(new Dimension(150, 100));
		
		add(_volumeControl, BorderLayout.WEST);
		add(_timeline, BorderLayout.CENTER);
	}
}
