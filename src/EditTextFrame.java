import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;


public class EditTextFrame extends JFrame implements ActionListener {
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
	
	private JButton _introPreview = new JButton("Preview");
	private JButton _outroPreview = new JButton("Preview");
	private Timer _clock = new Timer(11000, this);
	private previewWorker _previewWorker;
	
	//avconv -i a.mp4 -strict experimental -vf drawtext="fontfile=/usr/share/fonts/truetype/freefont/FreeSerif.ttf:text='hello there':draw='lt(t,10)'" p.mp4

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
		String[] colors = { "Red", "Orange", "Yellow", "Green" };
		String[] size = { "14", "16", "18", "20", "22", "24", "26", "28", "30", "32"};

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
		_introControl.add(_introPreview);

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
		_outroControl.add(_outroPreview);

		//Panel for entering text
		_outroTextPanel.add(_outroTextLabel, BorderLayout.NORTH);
		_outroTextPanel.add(_outroText, BorderLayout.CENTER);

		//Add the exiting components to the exiting panel
		_outroPanel.add(_outroTextPanel, BorderLayout.NORTH);
		_outroPanel.add(_outroControl, BorderLayout.CENTER);

		add(_introPanel, BorderLayout.NORTH);
		add(_outroPanel, BorderLayout.CENTER);
		_introPreview.addActionListener(this);
		_outroPreview.addActionListener(this);
		
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// when clock ticks
		if (e.getSource() == _clock) {
			_clock.stop();
			_previewWorker.destroyProcess();
		} 
		if (e.getSource() == _introPreview){
			String color = (String)_introColor.getSelectedItem();
			String size =  (String)_introSize.getSelectedItem();
			String text = (String)_introText.getText();
			String cmd = "avplay -i " + CurrentFile.getInstance().getPath() +
					" -t 0:00:10 -vf drawtext=\"fontfile=/usr/share/fonts/truetype/freefont/FreeSerif.ttf: text='" + text + "':" + ""
							+ "fontsize=" + size +": fontcolor=" + color + "\"";
			_clock.start();
			_previewWorker = new previewWorker(cmd);
			_previewWorker.execute();
		}
		if (e.getSource() == _outroPreview){
			String color = (String)_outroColor.getSelectedItem();
			String size =  (String)_outroSize.getSelectedItem();
			String text = (String)_outroText.getText();
			String cmd = "avplay -ss 0:05:00 -i " + CurrentFile.getInstance().getPath() +
					" -t 0:00:10 -vf drawtext=\"fontfile=/usr/share/fonts/truetype/freefont/FreeSerif.ttf: text='" + text + "':" + ""
							+ "fontsize=" + size +": fontcolor=" + color + "\"";
			_clock.start();
			_previewWorker = new previewWorker(cmd);
			_previewWorker.execute();
		}
		
		
	}
	
	class previewWorker extends SwingWorker<Void, Integer> {
		private int _exitStatus;
		private String _outputName;
		Process process = null;
		String _cmd;
		public previewWorker(String cmd) {
			_cmd = cmd;
		}
		
		@Override
		protected Void doInBackground() throws Exception {		
			// progress line bars are seen as dots
			// Suppress quotation marks
			// t is duration
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", _cmd);
			
			builder.redirectErrorStream(true);
			try {
				process = builder.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			_exitStatus = process.waitFor();
			
            process.destroy();
			return null;
		}
		
		 @Override
	     protected void process(List<Integer> chunks) {

	     }
		 
		 public void destroyProcess() {
	            process.destroy();
		 }
		 
	}
}
