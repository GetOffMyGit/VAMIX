
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;


public class MenuBar extends JPanel implements ActionListener {	
	private JMenuBar _fileTab = new JMenuBar();
	private JMenuBar _editAudioTab = new JMenuBar();
	private JMenuBar _editTextTab = new JMenuBar();
	
	private JMenu _fileMenu = new JMenu("File");
	private JMenu _editAudioMenu = new JMenu("Edit Audio");
	private JMenu _editTextMenu = new JMenu("Edit Text");
	
	private JMenuItem _openFile = new JMenuItem("Open File");
	private JMenuItem _render = new JMenuItem("Render");
	private JMenuItem _download = new JMenuItem("Download");
	private JMenuItem _save = new JMenuItem("Save");
	private JMenuItem _load = new JMenuItem("Load");
	private JMenuItem _stripAudio = new JMenuItem("Strip Audio");
	private JMenuItem _replaceAudio = new JMenuItem("Replace Audio");
	private JMenuItem _overlayAudio = new JMenuItem("Overlay Audio");
	private JMenuItem _editText = new JMenuItem("Edit Text");
	private DownloadFrame dl = null;
	private MidPanelHolder _midPanelHolder;
	private EditTextFrame _editTextFrame;
	
	
	private stripAudioWorker _audioWorker;
	
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
		

		_fileMenu.add(_render);		

		
		
		_download.addActionListener(this);
		_fileMenu.add(_download);
		
		_save.addActionListener(this);
		_fileMenu.add(_save);
		
		_load.addActionListener(this);
		_fileMenu.add(_load);
		
		_editAudioMenu.add(_stripAudio);
		_editAudioMenu.add(_replaceAudio);
		_editAudioMenu.add(_overlayAudio);
		_editTextMenu.add(_editText);
		
		_stripAudio.addActionListener(this);
		_replaceAudio.addActionListener(this);
		_overlayAudio.addActionListener(this);
		_render.addActionListener(this);
		_editText.addActionListener(this);
		
		_stripAudio.setEnabled(false);
		_replaceAudio.setEnabled(false);
		_overlayAudio.setEnabled(false);
		_render.setEnabled(false);
		_editText.setEnabled(false);
		_save.setEnabled(false);
		
		_fileTab.add(_fileMenu);
		_editAudioTab.add(_editAudioMenu);
		_editTextTab.add(_editTextMenu);
		
		
		
		add(_fileTab);
		add(_editAudioTab);
		add(_editTextTab);
			
		
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == _openFile) {
			
			JFileChooser fileChooser = new JFileChooser();
			int fileChooserReturn = fileChooser.showOpenDialog(null);
			if(fileChooserReturn == JFileChooser.APPROVE_OPTION) {
				File theFile = fileChooser.getSelectedFile();
				CurrentFile.getInstance().resetInstance();
				CurrentFile.getInstance().setInstance(theFile);
				if(CurrentFile.getInstance().getType() == null) {
					JOptionPane.showMessageDialog(this, "Please select a video file");
				} else if (!((CurrentFile.getInstance().getType().equals("Video")) || (CurrentFile.getInstance().getType().equals("Video with Audio")))) {
					JOptionPane.showMessageDialog(this, "Please select a video file");
				} else {
					_midPanelHolder.refreshMidPane();
					_stripAudio.setEnabled(true);
					_replaceAudio.setEnabled(true);
					_overlayAudio.setEnabled(true);
					_render.setEnabled(true);
					_editText.setEnabled(true);
					_save.setEnabled(true);
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
			if ((CurrentFile.getInstance().getType().equals("Video")) || (ProjectInfo.getInstance().isStripped())) { 
				JOptionPane.showMessageDialog(null, "Video file does not have audio"); 
				return;
			}
			String outputName = (String)JOptionPane.showInputDialog(this,
                    "Please enter a output audio name:",
                    "Strip Raw Audio", JOptionPane.PLAIN_MESSAGE, null, null, "");
			
			if (outputName != null) {
				_audioWorker = new stripAudioWorker(outputName);
				_audioWorker.execute();
				ProjectInfo.getInstance().Stripped();
				
			} // when
			
			
		}
		if (ae.getSource() == _replaceAudio) {
			if ((CurrentFile.getInstance().getType().equals("Video"))  || (ProjectInfo.getInstance().isStripped())) { 
				JOptionPane.showMessageDialog(this, "Video file does not have audio, please overlay"); 
				return;
			}
			JFileChooser fileChooser = new JFileChooser();
			int fileChooserReturn = fileChooser.showOpenDialog(null);
			if(fileChooserReturn == JFileChooser.APPROVE_OPTION) {
				File theFile = fileChooser.getSelectedFile();
				AudioFile replaceAudio = new AudioFile(theFile);
				if(replaceAudio.getType() == null) {
					JOptionPane.showMessageDialog(this, "Please select an audio file");
				} else if (!(replaceAudio.getType().equals("Audio"))) {
					JOptionPane.showMessageDialog(this, "Please select an audio file");
					
				} else {
					int reply = JOptionPane.showConfirmDialog(this,
		                    "Are you sure you want to replace the audio with " + replaceAudio.getName(),
		                    "Replace Audio", JOptionPane.YES_NO_OPTION);
					
					// outputName is null when cancelled or closed
					if (reply == JOptionPane.OK_OPTION) {
						ProjectInfo.getInstance().Replace(replaceAudio);
					} 
					
				}
			}
			
		}
		if (ae.getSource() == _overlayAudio) {
			OverlayPanel.OverlayAudio();		
		}
		
		if (ae.getSource() == _render) {
			if (!(ProjectInfo.getInstance().anyChanges())) {
				JOptionPane.showMessageDialog(this, "No changes made, render failed.");
				return;
			}
			String outputName = (String)JOptionPane.showInputDialog(this,
                    "Please enter a output video name:",
                    "Render", JOptionPane.PLAIN_MESSAGE, null, null, "");
			// outputName is null when cancelled or closed
			if (outputName != null) {
				if (outputName.equals("")) {
					JOptionPane.showMessageDialog(this, "Please enter a output video name.");
				} else {
					ProjectInfo.getInstance().render(outputName);
				}
			}
		}
		
		if (ae.getSource() == _editText) {
			_editTextFrame = new EditTextFrame();
			/*
			if(_editTextFrame == null) {
				_editTextFrame = new EditTextFrame();
			} else {
				if(_editTextFrame.isActive()) {
					_editTextFrame.toFront();
				} else {
					_editTextFrame = new EditTextFrame();
				}
			}
			*/
		}
		
		if (ae.getSource() == _save) {
			ProjectInfo.getInstance().saveProject();		
		}
		
		if (ae.getSource() == _load) {
			ProjectInfo.getInstance().load();
			_midPanelHolder.reloadMidPane();
			
			_stripAudio.setEnabled(ProjectInfo.getInstance().enable_Strip());
			_replaceAudio.setEnabled(true);
			_save.setEnabled(true);
			_overlayAudio.setEnabled(true);
			_render.setEnabled(true);
			_editText.setEnabled(true);
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
			String cmd = "avconv -i " + CurrentFile.getInstance().getPath() + " -map 0:a ~/"+ _outputName;
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
