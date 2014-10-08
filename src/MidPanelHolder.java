import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;





public class MidPanelHolder extends JPanel {
	private JPanel _fileInfo = new FileInfo();
	private JPanel _mediaPlayerHolder = new MediaPlayer();
	private JPanel _allInfo = new JPanel();
	private OverlayPanel _overlay = new OverlayPanel();
	private JPanel _audioPlayerHolder = new JPanel();
	private JPanel _audioPlayer = new AudioPlayer();
	
	
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
		_overlay.initialSetUp();
		
		
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
		ProjectInfo.reset();
		
		_allInfo.remove(_overlay);
		_overlay = new OverlayPanel(); 
		_overlay.setPreferredSize(new Dimension(200, 360));
		_allInfo.add(_overlay, BorderLayout.SOUTH);
		revalidate();
		repaint();
		
	}
	
	public void reloadMidPane() {
		_allInfo.remove(_fileInfo);
		remove(_mediaPlayerHolder);
		_fileInfo = new FileInfo();

		Color backgroundColor = new Color(70, 73, 74);
		_fileInfo.setBackground(backgroundColor);
		_mediaPlayerHolder = new MediaPlayer();
		_fileInfo.setPreferredSize(new Dimension(200, 80));
		
		_allInfo.add(_fileInfo, BorderLayout.NORTH);
		add(_mediaPlayerHolder, BorderLayout.CENTER);
		
		_allInfo.remove(_overlay);
		_overlay = new OverlayPanel(); 
		_overlay.setPreferredSize(new Dimension(200, 360));
		_allInfo.add(_overlay, BorderLayout.SOUTH);
		revalidate();
		repaint();
		
	}
}
