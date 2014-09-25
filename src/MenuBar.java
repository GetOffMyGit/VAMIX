import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;


public class MenuBar extends JPanel implements ActionListener {	
	private JMenuBar _fileTab = new JMenuBar();
	private JMenuBar _editAudioTab = new JMenuBar();
	private JMenuBar _editTextTab = new JMenuBar();
	
	private JMenu _fileMenu = new JMenu("File");
	private JMenu _editAudioMenu = new JMenu("Edit Audio");
	private JMenu _editTextMenu = new JMenu("Edit Text");
	
	private JMenuItem _openFile = new JMenuItem("Open File");
	private JMenuItem _save = new JMenuItem("Save");
	private JMenuItem _download = new JMenuItem("Download");
	private JMenuItem _stripAudio = new JMenuItem("Strip Audio");
	private JMenuItem _replaceAudio = new JMenuItem("Replace Audio");
	private JMenuItem _overlayAudio = new JMenuItem("Overlay Audio");
	private JMenuItem _editText = new JMenuItem("Edit Text");
	private DownloadFrame dl = null;
	private CurrentFile _currentFile;
	private MidPanelHolder _midPanelHolder;
	private EditTextFrame _editTextFrame;
	
	
	private stripAudioWorker _audioWorker;
	private static ProjectInfo _projectInfo;
	
	public MenuBar(MidPanelHolder midPanelHolder) {
		Color backgroundColor = new Color(42, 46, 53);
		Color textColor = new Color(203, 205, 207);
		setBackground(backgroundColor);
		
		_fileTab.setBackground(backgroundColor);
		_editAudioTab.setBackground(backgroundColor);
		_editTextTab.setBackground(backgroundColor);
		_editTextTab.setBackground(backgroundColor);
		
		_fileMenu.setForeground(textColor);
		_editAudioMenu.setForeground(textColor);
		_editTextMenu.setForeground(textColor);
		
		_midPanelHolder = midPanelHolder;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		
		_openFile.addActionListener(this);
		_fileMenu.add(_openFile);
		
		_save.addActionListener(this);
		_fileMenu.add(_save);	
		
		_download.addActionListener(this);
		_fileMenu.add(_download);
		
		_editAudioMenu.add(_stripAudio);
		_editAudioMenu.add(_replaceAudio);
		_editAudioMenu.add(_overlayAudio);
		_editTextMenu.add(_editText);
		
		_stripAudio.addActionListener(this);
		_replaceAudio.addActionListener(this);
		_overlayAudio.addActionListener(this);
		_save.addActionListener(this);
		_editText.addActionListener(this);
		
		_fileTab.add(_fileMenu);
		_editAudioTab.add(_editAudioMenu);
		_editTextTab.add(_editTextMenu);
		
		
		
		add(_fileTab);
		add(_editAudioTab);
		add(_editTextTab);
		
		_projectInfo = ProjectInfo.getInstance();
		
		
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == _openFile) {
			
			JFileChooser fileChooser = new JFileChooser();
			int fileChooserReturn = fileChooser.showOpenDialog(null);
			if(fileChooserReturn == JFileChooser.APPROVE_OPTION) {
				File theFile = fileChooser.getSelectedFile();
				_currentFile = CurrentFile.getInstance();
				_currentFile.resetInstance();
				_currentFile.setInstance(theFile);
				if(_currentFile.getType() == null) {
					JOptionPane.showMessageDialog(null, "Please select an audio or video file");
					_currentFile = null;
				} else if ((!(_currentFile.getType().equals("Video")) && (_currentFile.getType().equals("Audio") && (_currentFile.getType().equals("Video with Audio"))))) {
					JOptionPane.showMessageDialog(null, "Please select an audio or video file");
					_currentFile = null;
				} else {
					_midPanelHolder.refreshMidPane();
				}
			}
		}
		if (ae.getSource() == _download) {
			// first time selecting download option
			if (dl == null) {
				dl = new DownloadFrame();
			} else {
				// download frame already opened, brings to user
				// doesn't create duplicates and allows only one dl at a time
				if (dl.isActive()) {
					dl.toFront();
				} else {
					dl = new DownloadFrame();
				}
			}	
		}
		if (ae.getSource() == _stripAudio) {
			JPanel panel = new JPanel(new BorderLayout());
			// splits jLabel at <br> causing a new line
			panel.add(new JLabel("<html>Stripping out audio, would you like to save a copy? <br> If yes please enter an output name:</html>"), BorderLayout.NORTH);
			JTextField outputName = new JTextField(10);
			panel.add(outputName, BorderLayout.CENTER);
			String[] buttons = { "Yes +Raw", "Yes +Overlays", "No", "Cancel" };
			String message  = "Strip Audio";
			int reply = JOptionPane.showOptionDialog(this, panel, message, 0, 0, null, buttons, buttons[0]);
			
			if (buttons[reply].equals("Cancel")) { return;
			} else if (buttons[reply].equals("Yes +Raw")) {
				//avconv -i x.mp4 -map 0:a l.mp3
				_audioWorker = new stripAudioWorker(outputName.getText());
				_audioWorker.execute();
				// check if empty
				
			} else if (buttons[reply].equals("Yes +Overlays")) {
				// when
			}
			
			// is stripped
			
			// need swing worker
			// extract source input 0 (x.mp4) and only take its video
			//avconv -i x.mp4 -map 0:v l.mp4
			//avconv -i x.mp4 -map 0:a l.mp3
			
			// ask for raw or with overlays
			// create avconv command
			
		}
		if (ae.getSource() == _replaceAudio) {
			//main audio replaced
			//avconv -i input1 -i input2 -map 0:v -map 1:a output
			//avconv -i x.mp4 -strict experimental -i a.mp3 -strict experimental -map 0:v -map 1:a output.mp4
			//only video and audio of input1
			//"allow non-standardized experimental things"
			//"‘-strict[:stream_specifier] integer (input/output,audio,video)’ how strictly to follow the standards"
			String[] buttons = { "Yes", "Yes with overlays deleted", "No", "Cancel" };
			String message  = "Replace current audio with ";
			int reply = JOptionPane.showOptionDialog(this, message, "ERROR", 0, 0, null, buttons, buttons[0]);
			
		}
		if (ae.getSource() == _overlayAudio) {
			OverlayPanel.OverlayAudio();
			// mix two audio
			//avconv -i input1 -i input2 -filter_complex amix=input=3:duration=first:dropout_transition=2 output
			// need swing worker
			

		}
		if (ae.getSource() == _editText) {
			if(_editTextFrame == null) {
				_editTextFrame = new EditTextFrame();
			} else {
				if(_editTextFrame.isActive()) {
					_editTextFrame.toFront();
				} else {
					_editTextFrame = new EditTextFrame();
				}
			}
		}
	}
	
	
	public void finish() {
		JOptionPane.showMessageDialog(this, "Successful download of !");
	}
	
	class stripAudioWorker extends SwingWorker<Void, Integer> {
		private int _exitStatus;
		private String _outputName;
		public stripAudioWorker(String outputName) {
			_outputName = outputName;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			
			
			// progress line bars are seen as dots
			// Suppress quotation marks
			String cmd = "avconv -i " + _currentFile.getPath() + " -map 0:a ~/"+ _outputName;
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			
			builder.redirectErrorStream(true);
			Process process = null;
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
		 
		 @Override
		 protected void done() {
			 finish();
		 }
	}

}
