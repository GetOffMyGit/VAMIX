import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class EditTextFrame extends JFrame {
	private JPanel _introPanel = new JPanel();
	private JPanel _outroPanel = new JPanel();
	private JPanel _introControl = new JPanel();
	private JPanel _outroControl = new JPanel();
	private JPanel _introTextPanel = new JPanel();
	private JPanel _outroTextPanel = new JPanel();
	private JPanel _buttons = new JPanel();
	private JButton _confirm = new JButton("Confirm");
	private JButton _cancel = new JButton("Cancel");
	private JTextField _introText = new JTextField();
	private JTextField _outroText = new JTextField();
	private JComboBox _introFont;
	private JComboBox _outroFont;
	private JComboBox _introColor;
	private JComboBox _outroColor;
	private JComboBox _introSize;
	private JComboBox _outroSize;
	private JLabel _introFontLabel = new JLabel("Font:");
	private JLabel _introColorLabel = new JLabel("Color:");
	private JLabel _introSizeLabel = new JLabel("Size:");
	private JLabel _outroFontLabel = new JLabel("Font:");
	private JLabel _outroColorLabel = new JLabel("Color:");
	private JLabel _outroSizeLabel = new JLabel("Size:");
	private JLabel _introTextLabel = new JLabel("Introduction Text:");
	private JLabel _outroTextLabel = new JLabel("Exiting Text:");

	public EditTextFrame() {
		setTitle("Edit Text");
		setLayout(new BorderLayout());
		setVisible(true);
		setPreferredSize(new Dimension(700, 400));
		pack();

		_introPanel.setLayout(new BorderLayout());
		_outroPanel.setLayout(new BorderLayout());
		_introControl.setLayout(new FlowLayout(FlowLayout.LEFT));
		_introTextPanel.setLayout(new BorderLayout());
		_outroControl.setLayout(new FlowLayout(FlowLayout.LEFT));
		_outroTextPanel.setLayout(new BorderLayout());

		String[] fonts = { "Font 1", "Font 2", "Font 3", "Font 4" };
		String[] colors = { "Color 1", "Color 2", "Color 3", "Color 4" };
		String[] size = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

		_introFont = new JComboBox(fonts);
		_introColor = new JComboBox(colors);
		_introSize = new JComboBox(size);

		_outroFont = new JComboBox(fonts);
		_outroColor = new JComboBox(colors);
		_outroSize = new JComboBox(size);
		
		//Setting initial index
		_introFont.setSelectedIndex(0);
		_introColor.setSelectedIndex(0);
		_introSize.setSelectedIndex(0);
		_outroFont.setSelectedIndex(0);
		_outroColor.setSelectedIndex(0);
		_outroSize.setSelectedIndex(0);

		//Labels and combo boxes for font, color and size
		_introControl.add(_introFontLabel);
		_introControl.add(_introFont);
		_introControl.add(_introColorLabel);
		_introControl.add(_introColor);
		_introControl.add(_introSizeLabel);
		_introControl.add(_introSize);
		_introControl.add(_introText);

		//Panel for entering text
		_introTextPanel.add(_introTextLabel, BorderLayout.NORTH);
		_introTextPanel.add(_introText, BorderLayout.CENTER);

		//Add the introduction components to the introduction panel
		_introPanel.add(_introTextPanel, BorderLayout.NORTH);
		_introPanel.add(_introControl, BorderLayout.CENTER);

		//Labels and combo boxes for font, color and size
		_outroControl.add(_outroFontLabel);
		_outroControl.add(_outroFont);
		_outroControl.add(_outroColorLabel);
		_outroControl.add(_outroColor);
		_outroControl.add(_outroSizeLabel);
		_outroControl.add(_outroSize);
		_outroControl.add(_outroText);

		//Panel for entering text
		_outroTextPanel.add(_outroTextLabel, BorderLayout.NORTH);
		_outroTextPanel.add(_outroText, BorderLayout.CENTER);

		//Add the exiting components to the exiting panel
		_outroPanel.add(_outroTextPanel, BorderLayout.NORTH);
		_outroPanel.add(_outroControl, BorderLayout.CENTER);

		add(_introPanel, BorderLayout.NORTH);
		add(_outroPanel, BorderLayout.CENTER);
	}
}
