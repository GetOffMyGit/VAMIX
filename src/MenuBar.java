import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class MenuBar extends JPanel implements ActionListener {	
	private JMenuBar _fileTab = new JMenuBar();
	private JMenuBar _editAudioTab = new JMenuBar();
	private JMenuBar _editTextTab = new JMenuBar();
	private JMenuBar _downloadTab = new JMenuBar();
	
	private JMenu _fileMenu = new JMenu("File");
	private JMenu _editAudioMenu = new JMenu("Edit Audio");
	private JMenu _editTextMenu = new JMenu("Edit Text");
	private JMenu _downloadMenu = new JMenu("Download");
	
	private JMenuItem _openFile = new JMenuItem("Open File");
	private JMenuItem _openRecent = new JMenuItem("Open Recent");
	private JMenuItem _stripAudio = new JMenuItem("Strip Audio");
	private JMenuItem _replaceAudio = new JMenuItem("Replace Audio");
	private JMenuItem _overlayAudio = new JMenuItem("Overlay Audio");
	private JMenuItem _editText = new JMenuItem("Edit Text");
	private JMenuItem _download = new JMenuItem("Download");
	private DownloadFrame dl = null;
	private CurrentFile _currentFile;
	private MidPanelHolder _midPanelHolder;
	
	
	public MenuBar(MidPanelHolder midPanelHolder) {
		_midPanelHolder = midPanelHolder;
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		
		_openFile.addActionListener(this);
		_fileMenu.add(_openFile);
		
		_openRecent.addActionListener(this);
		_fileMenu.add(_openRecent);		
		
		_editAudioMenu.add(_stripAudio);
		_editAudioMenu.add(_replaceAudio);
		_editAudioMenu.add(_overlayAudio);
		_editTextMenu.add(_editText);
		_downloadMenu.add(_download);
		
		_download.addActionListener(this);
		_stripAudio.addActionListener(this);
		_replaceAudio.addActionListener(this);
		_overlayAudio.addActionListener(this);
		_openRecent.addActionListener(this);
		
		_fileTab.add(_fileMenu);
		_editAudioTab.add(_editAudioMenu);
		_editTextTab.add(_editTextMenu);
		_downloadTab.add(_downloadMenu);
		
		
		
		add(_fileTab);
		add(_editAudioTab);
		add(_editTextTab);
		add(_downloadTab);
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
			// need swing worker
			// extract source input 0 (x.mp4) and only take its video
			//avconv -i x.mp4 -map 0:v l.mp4

		}
		if (ae.getSource() == _replaceAudio) {
			
		}
		if (ae.getSource() == _overlayAudio) {
			// mix two audio
			//avconv -i input1 -i input2 -filter_complex amix=input=3:duration=first:dropout_transition=2 output
			// need swing worker
			
			//avconv -i input1 -i input2 -map 0:v -map 1:a output
			//only video and audio of input1
		}
	}
}
