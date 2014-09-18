import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


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
	private JMenuItem _editAudio = new JMenuItem("Edit Audio");
	private JMenuItem _editText = new JMenuItem("Edit Text");
	private JMenuItem _download = new JMenuItem("Download");
	
	
	public MenuBar() {
		setLayout(new GridLayout(1, 0));
		
		
		_openFile.addActionListener(this);
		_fileMenu.add(_openFile);
		
		_openRecent.addActionListener(this);
		_fileMenu.add(_openRecent);		
		
		_editAudioMenu.add(_editAudio);
		_editTextMenu.add(_editText);
		_downloadMenu.add(_download);
		
		_download.addActionListener(this);
		
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
			}
		}
		if (ae.getSource() == _download) {
			DownloadPanel dl = new DownloadPanel();
		}
	}
}
