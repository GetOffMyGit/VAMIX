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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


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
	private JComboBox _introFont1;
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
	private JLabel _introDurationLabel = new JLabel("Duration:");
	private JLabel _outroDurationLabel = new JLabel("Duration:");
	private JSpinner _introDuration = new JSpinner();
	private JSpinner _outroDuration = new JSpinner();
	private JButton _introPreview = new JButton("Preview");
	private JButton _outroPreview = new JButton("Preview");
	private JPanel _introSpace = new JPanel();
	private JPanel _outroSpace = new JPanel();
	private CurrentFile _currentFile;
	private MediaPlayer _mediaPlayer = new MediaPlayer();
	
	private Timer _clock = new Timer(11000, this);
	private previewWorker _previewWorker;
	
	//avconv -i a.mp4 -strict experimental -vf drawtext="fontfile=/usr/share/fonts/truetype/freefont/FreeSerif.ttf:text='hello there':draw='lt(t,10)'" p.mp4

	public EditTextFrame() {
		setTitle("Edit Text");
		setLayout(new BorderLayout());
		setVisible(true);
		setPreferredSize(new Dimension(700, 200));
		pack();

		_introSpace.setPreferredSize(new Dimension(80, 20));
		_outroSpace.setPreferredSize(new Dimension(80, 20));
		
		_introPanel.setLayout(new BorderLayout());
		_outroPanel.setLayout(new BorderLayout());
		_introControl.setLayout(new FlowLayout(FlowLayout.LEFT));
		_introTextPanel.setLayout(new BorderLayout());
		_outroControl.setLayout(new FlowLayout(FlowLayout.LEFT));
		_outroTextPanel.setLayout(new BorderLayout());
		_buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		//Set background color
		Color backgroundColor = new Color(70, 73, 74);
		_introControl.setBackground(backgroundColor);
		_outroControl.setBackground(backgroundColor);
		_introTextPanel.setBackground(backgroundColor);
		_outroTextPanel.setBackground(backgroundColor);
		_buttons.setBackground(backgroundColor);
		_introSpace.setBackground(backgroundColor);
		_outroSpace.setBackground(backgroundColor);
		
		//Set text color
		Color textColor = new Color(203, 205, 207);
		_introFontLabel.setForeground(textColor);
		_introColorLabel.setForeground(textColor);
		_introSizeLabel.setForeground(textColor);
		_outroFontLabel.setForeground(textColor);
		_outroColorLabel.setForeground(textColor);
		_outroSizeLabel.setForeground(textColor);
		_introTextLabel.setForeground(textColor);
		_outroTextLabel.setForeground(textColor);
		_introDurationLabel.setForeground(textColor);
		_outroDurationLabel.setForeground(textColor);
		
		//Set up combo boxes
		String[] fonts = { "FreeMono", "FreeSans", "FreeSerif" };
		String[] colors = { "Red", "Orange", "Yellow", "Green" };
		String[] size = { "14", "16", "18", "20", "22", "24", "26", "28", "30", "32"};

		_introFont1 = new JComboBox(colors);
		_introColor = new JComboBox(colors);
		_introSize = new JComboBox(size);

		_outroFont = new JComboBox(fonts);
		_outroColor = new JComboBox(colors);
		_outroSize = new JComboBox(size);
		
		TextInfo intro = ProjectInfo.getInstance()._intro;
		TextInfo outro = ProjectInfo.getInstance()._outro;
		
		//Setting current values
		_introFont1.setSelectedItem(intro.get_font());
		_introColor.setSelectedIndex(intro.get_color());
		_introSize.setSelectedIndex(intro.get_size());
		_outroFont.setSelectedIndex(outro.get_font());
		_outroColor.setSelectedIndex(outro.get_color());
		_outroSize.setSelectedIndex(outro.get_size());
		
		_introText.setText(intro.get_text());
		_outroText.setText(outro.get_text());
		
		//Set up spinners
		_currentFile = CurrentFile.getInstance();
		int max = (int) (_currentFile.getDurationSeconds() * 0.2);
		SpinnerModel introSpinner = new SpinnerNumberModel((int)intro.get_integerDuration(), 0, max, 1);
		_introDuration.setModel(introSpinner);
		SpinnerModel outroSpinner = new SpinnerNumberModel((int)outro.get_integerDuration(), 0, max, 1);
		_outroDuration.setModel(outroSpinner);
		
		//Set up buttons
		Color buttonColor = new Color(220, 222, 224);
		_confirm.setBorderPainted(false);
		_confirm.setBackground(buttonColor);
		_cancel.setBorderPainted(false);
		_cancel.setBackground(buttonColor);
		_introPreview.setBackground(buttonColor);
		_outroPreview.setBackground(buttonColor);
		
		//Set up text fields listeners. Max word limit and label setting
		_introText.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				String input = _introText.getText();
				int i = 0;
				int count = 0;
				
				while(i < input.length()) {
					if(input.charAt(i) == ' ') {
						count++;
					}
					i++;
				}
				
				if(count > 19) {
					JOptionPane.showMessageDialog(null, "Maximum Word Limit Reached");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		_outroText.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				String input = _outroText.getText();
				int i = 0;
				int count = 0;
				
				while(i < input.length()) {
					if(input.charAt(i) == ' ') {
						count++;
					}
					i++;
				}
				
				if(count > 19) {
					JOptionPane.showMessageDialog(null, "Maximum Word Limit Reached");
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});

		//Labels and combo boxes for font, color and size
		_introControl.add(_introFontLabel);
		_introControl.add(_introFont1);
		_introControl.add(_introColorLabel);
		_introControl.add(_introColor);
		_introControl.add(_introSizeLabel);
		_introControl.add(_introSize);
		_introControl.add(_introText);
		_introControl.add(_introDurationLabel);
		_introControl.add(_introDuration);
		_introControl.add(_introSpace);
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
		_outroControl.add(_outroDurationLabel);
		_outroControl.add(_outroDuration);
		_outroControl.add(_outroSpace);
		_outroControl.add(_outroPreview);
		
		//Add action listeners
		_confirm.addActionListener(this);
		_cancel.addActionListener(this);
		_introPreview.addActionListener(this);
		_outroPreview.addActionListener(this);
		

		//Panel for entering text
		_outroTextPanel.add(_outroTextLabel, BorderLayout.NORTH);
		_outroTextPanel.add(_outroText, BorderLayout.CENTER);

		//Add the exiting components to the exiting panel
		_outroPanel.add(_outroTextPanel, BorderLayout.NORTH);
		_outroPanel.add(_outroControl, BorderLayout.CENTER);
		
		//Add buttons to button panel
		_buttons.add(_confirm);
		_buttons.add(_cancel);

		add(_introPanel, BorderLayout.NORTH);
		add(_outroPanel, BorderLayout.CENTER);

		add(_buttons, BorderLayout.SOUTH);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// when clock ticks
		if (e.getSource() == _clock) {
			_clock.stop();
			_previewWorker.destroyProcess();
			_introPreview.setEnabled(true);
			_outroPreview.setEnabled(true);
		} 
		if (e.getSource() == _introPreview){
			String color = (String)_introColor.getSelectedItem();
			String size =  (String)_introSize.getSelectedItem();
			String font = (String)_introFont1.getSelectedItem();
			String text = (String)_introText.getText();
			Integer integerDuration = (Integer) _introDuration.getValue();
			String duration = _mediaPlayer.formatTime(integerDuration);
			String cmd = "avplay -i " + CurrentFile.getInstance().getPath() +
					" -t " + duration + " -vf drawtext=\"fontfile=/usr/share/fonts/truetype/freefont/" + font + ".ttf: text='" + text + "':" + ""
							+ "fontsize=" + size +": fontcolor=" + color + ": x=50: y=50" + "\"";
			// sets preview for as long as the duration
			_clock = new Timer(integerDuration*1000 + 100, this);
			// disable so only allows one previeworker
			_introPreview.setEnabled(false);
			_outroPreview.setEnabled(false);
			_clock.start();
			_previewWorker = new previewWorker(cmd);
			_previewWorker.execute();
		}
		if (e.getSource() == _outroPreview){
			String color = (String)_outroColor.getSelectedItem();
			String size =  (String)_outroSize.getSelectedItem();
			String text = (String)_outroText.getText();
			String font = (String)_outroFont.getSelectedItem();
			Integer integerDuration = (Integer) _outroDuration.getValue();
			Integer totalSeconds = CurrentFile.getInstance().getDurationSeconds();
			String duration = _mediaPlayer.formatTime(integerDuration);
			String startTime = _mediaPlayer.formatTime(totalSeconds- integerDuration);
			String cmd = "avplay -ss " + startTime + " -i " + CurrentFile.getInstance().getPath() +
					" -t " + duration + " -vf drawtext=\"fontfile=/usr/share/fonts/truetype/freefont/" + font + ".ttf: text='" + text + "':" + ""
							+ "fontsize=" + size +": fontcolor=" + color + "\"";
			// sets preview for as long as the duration
			_clock = new Timer(integerDuration*1000 + 100, this);
			_clock.start();
			// disable so only allows one preview worker
			_introPreview.setEnabled(false);
			_outroPreview.setEnabled(false);
			_previewWorker = new previewWorker(cmd);
			_previewWorker.execute();
		} 
		if (e.getSource() == _confirm) {
			int color = _introColor.getSelectedIndex();
			int size =  _introSize.getSelectedIndex();
			int font = _introFont1.getSelectedIndex();
			String text = (String)_introText.getText();
			Integer integerDuration = (Integer) _introDuration.getValue();
			String duration = _mediaPlayer.formatTime(integerDuration);
			ProjectInfo.getInstance()._intro = new TextInfo(color, size, font, text, integerDuration);
			
			
			color = _outroColor.getSelectedIndex();
			size =  _outroSize.getSelectedIndex();
			font = _outroFont.getSelectedIndex();
			text = (String)_outroText.getText();
			integerDuration = (Integer) _outroDuration.getValue();
			duration = _mediaPlayer.formatTime(integerDuration);
			ProjectInfo.getInstance()._outro = new TextInfo(color, size, font, text, integerDuration);
			dispose();
		}
		if (e.getSource() == _cancel) {
			dispose();
		}
		
		
	}
	
	class previewWorker extends SwingWorker<Void, Integer> {
		Process process = null;
		String _cmd;
		public previewWorker(String cmd) {
			_cmd = cmd;
		}
		
		@Override
		protected Void doInBackground() throws Exception {		
			// Suppress quotation marks
			// t is duration
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", _cmd);
			
			builder.redirectErrorStream(true);
			try {
				process = builder.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			process.waitFor();
			
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
