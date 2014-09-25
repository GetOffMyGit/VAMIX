import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;


public class MediaPlayer extends JPanel implements ActionListener {

//	private EmbeddedMediaPlayerComponent _mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
//	private EmbeddedMediaPlayer _video = _mediaPlayerComponent.getMediaPlayer();
	private JPanel _mediaPlayerComponent = new JPanel();
	private JButton _play = new JButton();
	private JButton _pause = new JButton();
	private JButton _stop = new JButton();
	private JButton _mute = new JButton();
	private JButton _fastForward = new JButton();
	private JButton _rewind = new JButton();
	private JSlider _volumeControl;
	private JPanel _controls = new JPanel();
	private JProgressBar _progressBar = new JProgressBar();
	private Timer _clock;
	private JLabel _durationLabel = new JLabel("\\ 00:00:00");
	private JLabel _timer = new JLabel("00:00:00");

	private CurrentFile _currentFile = CurrentFile.getInstance();

	public MediaPlayer() {
	/*	NativeLibrary.addSearchPath(
				RuntimeUtil.getLibVlcLibraryName(), "/usr/lib"
				);

		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class); */

		setLayout(new BorderLayout());
		
		_mediaPlayerComponent.setBackground(Color.black);

		//Media player and control dimensions
		_mediaPlayerComponent.setPreferredSize(new Dimension(800, 470)); //475
		
		_controls.setPreferredSize(new Dimension(800, 60));

		//Create and start clock
		_clock = new Timer(500, this);
		_clock.start();

		//Set starting value and minimum/maximum of the progress bar
		_progressBar.setValue(0);
		_progressBar.setMinimum(0);
		_progressBar.setMaximum(_currentFile.getDurationSeconds());

		//Set Up buttons with images
		try {
			Image playImage = ImageIO.read(new File("res/Play.png"));
			_play.setBorderPainted(false);
			_play.setContentAreaFilled(false);
			_play.setPreferredSize(new Dimension(45, 45));
			playImage = playImage.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
			_play.setIcon(new ImageIcon(playImage));
			
			Image fastForwardImage = ImageIO.read(new File("res/Fastforward.png"));
			_fastForward.setBorderPainted(false);
			_fastForward.setContentAreaFilled(false);
			_fastForward.setPreferredSize(new Dimension(35, 35));
			fastForwardImage = fastForwardImage.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
			_fastForward.setIcon(new ImageIcon(fastForwardImage));

			Image muteImage = ImageIO.read(new File("res/Mute.png"));
			_mute.setBorderPainted(false);
			_mute.setContentAreaFilled(false);
			_mute.setPreferredSize(new Dimension(35, 35));
			muteImage = muteImage.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
			_mute.setIcon(new ImageIcon(muteImage));
			
			Image stopImage = ImageIO.read(new File("res/Stop.png"));
			_stop.setBorderPainted(false);
			_stop.setContentAreaFilled(false);
			_stop.setPreferredSize(new Dimension(45, 45));
			stopImage = stopImage.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
			_stop.setIcon(new ImageIcon(stopImage));
			
			Image rewindImage = ImageIO.read(new File("res/Rewind.png"));
			_rewind.setBorderPainted(false);
			_rewind.setContentAreaFilled(false);
			_rewind.setPreferredSize(new Dimension(35, 35));
			rewindImage = rewindImage.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
			_rewind.setIcon(new ImageIcon(rewindImage));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Disable all buttons except the play button at initial startup
		_pause.setEnabled(false);
		_stop.setEnabled(false);
		_mute.setEnabled(false);
		_fastForward.setEnabled(false);
		_rewind.setEnabled(false);

		//Slider configurations
		_volumeControl = new JSlider();
		UIManager.getLookAndFeelDefaults().put("Slider.horizontalThumbIcon", new ImageIcon() {
			@Override
			public int getIconHeight() {
				return 0;
			}
			
			@Override public int getIconWidth() {
				return 0;
			}
			
		//	@Override
		//	public void paintIcon(Component c, Graphics g, int x, int y) {
				
		//	}
		}); 
		
		//Display video duration on the label
		if(_currentFile.getDuration() != null) {
			String[] durationParts = _currentFile.getDuration().split("\\.");
			String duration = "\\ " + durationParts[0];
			_durationLabel.setText(duration);
		}
		
		//Set colour for control panel
		Color backgroundColor = new Color(70, 73, 74);
		Color textColor = new Color(203, 205, 207);
		_controls.setBackground(backgroundColor);
		_durationLabel.setBackground(backgroundColor);
		_volumeControl.setBackground(backgroundColor);
		
		_durationLabel.setForeground(textColor);
		_timer.setForeground(textColor);
		
		
		
		//Add control components to the control panel
		_controls.setLayout(new FlowLayout(FlowLayout.LEFT));
		_controls.add(_rewind);
	//	_controls.add(_pause);
		_controls.add(_play);
		_controls.add(_fastForward);
		_controls.add(_stop);
		_controls.add(_mute);
		_controls.add(_volumeControl);
		_controls.add(_timer);
		_controls.add(_durationLabel);

		//Add action listeners to the control components
		_play.addActionListener(this);
		_pause.addActionListener(this);
		_stop.addActionListener(this);
		_mute.addActionListener(this);
		_fastForward.addActionListener(this);
		_rewind.addActionListener(this);

		//Add the media player, progress bar and controls to the
		//MediaPlayer panel
		add(_mediaPlayerComponent, BorderLayout.NORTH);
		add(_progressBar, BorderLayout.CENTER);
		add(_controls, BorderLayout.SOUTH);

	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == _play) {
		/*	if(!_video.canPause()) {
				_currentFile = CurrentFile.getInstance();
				if(_currentFile.getType() == null) {
					JOptionPane.showMessageDialog(null, "Please open an audio or video file before playing");
					return;
				} else {
					_video.playMedia(_currentFile.getPath());
				}
			} else {
				_video.pause();
			} */
			_play.setEnabled(false);
			_stop.setEnabled(true);
			_pause.setEnabled(true);
			_mute.setEnabled(true);
			_fastForward.setEnabled(true);
			_rewind.setEnabled(true);
		} else if(ae.getSource() == _pause) {
	//		_video.pause();
			_play.setEnabled(true);
			_pause.setEnabled(false);
		} else if(ae.getSource() == _stop) {
	//		_video.stop();
			_stop.setEnabled(false);
			_play.setEnabled(true);
			_pause.setEnabled(false);
			_mute.setEnabled(false);
			_fastForward.setEnabled(false);
			_rewind.setEnabled(false);
		} else if(ae.getSource() == _fastForward) {
			_fastForward.setEnabled(false);
			while(!_fastForward.isEnabled()) {
	//			_video.skip(1000);
			}
		} else if(ae.getSource() == _rewind) {
	//		_video.skip(-1000);
		} else if(ae.getSource() == _mute) {
	//		_video.mute();
		} else if(ae.getSource() == _clock) {
	//		int time = (int) (_video.getTime()/1000.0);
	//		_progressBar.setValue(time);
	//		String stringTime = formatTime(time);
	//		_timer.setText(stringTime);
		}
	}
	
	private String formatTime(int time) {
		int hours = time / 3600;
		int remainder = time - (hours * 3600);
		int minutes = remainder / 60;
		remainder = remainder - (minutes * 60);
		int seconds = remainder;
		
		String stringHours = Integer.toString(hours);
		String stringMinutes = Integer.toString(minutes);
		String stringSeconds = Integer.toString(seconds);
		
		if(hours < 10) {
			stringHours = "0" + Integer.toString(hours);
		}
		if(minutes < 10) {
			stringMinutes = "0" + Integer.toString(minutes);
		}
		if(seconds < 10) {
			stringSeconds = "0" + Integer.toString(seconds);
		}
		String format = stringHours + ":" + stringMinutes + ":" + stringSeconds;
		
		return format;
	}
}
