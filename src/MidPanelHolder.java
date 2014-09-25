import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;


public class MidPanelHolder extends JPanel {
	private JPanel _fileInfo = new FileInfo();
	private JPanel _mediaPlayerHolder = new MediaPlayer();
	private JPanel _allInfo = new JPanel();
	private JPanel _overlay = new OverlayPanel();
	private JPanel _audioPlayerHolder = new JPanel();
	private JPanel _audioPlayer = new AudioPlayer();
	private ProjectInfo _projectInfo;
	
	
	public MidPanelHolder() {
		setLayout(new BorderLayout());
		_audioPlayerHolder.setLayout(new BorderLayout());
		
		_fileInfo.setPreferredSize(new Dimension(200, 80));
		_allInfo.setLayout(new BorderLayout());
		_allInfo.add(_fileInfo, BorderLayout.NORTH);
		
		_overlay.setPreferredSize(new Dimension(200, 360));
		_allInfo.add(_overlay, BorderLayout.SOUTH);
		add(_allInfo, BorderLayout.WEST);
		Color backgroundColor = new Color(70, 73, 74);
		_fileInfo.setBackground(backgroundColor);
		add(_mediaPlayerHolder, BorderLayout.CENTER);
		
		_audioPlayerHolder.add(_audioPlayer, BorderLayout.CENTER);
		
		_allInfo.add(_audioPlayerHolder, BorderLayout.CENTER);
		
		
	}
	
	public void refreshMidPane() {
		
		_allInfo.remove(_fileInfo);
		remove(_mediaPlayerHolder);
		_fileInfo = new FileInfo();

		Color backgroundColor = new Color(70, 73, 74);
		_fileInfo.setBackground(backgroundColor);
		_mediaPlayerHolder = new MediaPlayer();
		_fileInfo.setPreferredSize(new Dimension(200, 80));
		
		_allInfo.add(_fileInfo, BorderLayout.NORTH);
		add(_mediaPlayerHolder, BorderLayout.CENTER);
		revalidate();
		repaint();
		
	}
}
