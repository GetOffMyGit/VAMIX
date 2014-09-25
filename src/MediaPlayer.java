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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;


public class MediaPlayer extends JPanel implements ActionListener {

	private EmbeddedMediaPlayerComponent _mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
	private EmbeddedMediaPlayer _video = _mediaPlayerComponent.getMediaPlayer();
	private JPanel _statusPanel = new JPanel();
	private JButton _play = new JButton();
	private JButton _pause = new JButton();
	private JButton _stop = new JButton();
	private JButton _mute = new JButton();
	private JButton _fastForward = new JButton();
	private JButton _rewind = new JButton();
	private JSlider _volumeControl = new JSlider(0, 200);
	private JPanel _controls = new JPanel();
	private JProgressBar _progressBar = new JProgressBar();
	private Timer _clock;
	private Timer _navigateClock;
	private JLabel _durationLabel = new JLabel("\\ 00:00:00");
	private JLabel _timer = new JLabel("00:00:00");
	private boolean _isPaused = false;
	private boolean _isFastForward = false;
	private boolean _isRewind = false;
	private int _setVolume = 100;
	private ProjectInfo _projectInfo;

	private CurrentFile _currentFile = CurrentFile.getInstance();

	public MediaPlayer() {

		NativeLibrary.addSearchPath(
				RuntimeUtil.getLibVlcLibraryName(), "/usr/lib"
				);

		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		_projectInfo = ProjectInfo.getInstance();

		setLayout(new BorderLayout());
		_mediaPlayerComponent.revalidate();
		_mediaPlayerComponent.repaint();

		//Set layout of status panel
		_statusPanel.setLayout(new BorderLayout());

		//Media player, controls and progress bar dimensions
		_mediaPlayerComponent.setPreferredSize(new Dimension(800, 470));
		_controls.setPreferredSize(new Dimension(800, 60));

		//Create and start the clocks
		_clock = new Timer(500, this);
		_clock.start();
		_navigateClock = new Timer(100, this);

		//Set starting value and minimum/maximum of the progress bar
		_progressBar.setValue(0);
		_progressBar.setMinimum(0);
		_progressBar.setMaximum(_currentFile.getDurationSeconds());

		//Configure volume slider
		_volumeControl.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int volume = _volumeControl.getValue();
				if(!(_currentFile.getName() == null)) {
					_volumeControl.setEnabled(true);
					_video.setVolume(volume);
					_setVolume = volume;
					_projectInfo.adjustVolume(_setVolume);
				} else {
					_volumeControl.setEnabled(false);
				}
			}
		});

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

		//Add controls panel and progress bar to status panel
		_statusPanel.add(_controls, BorderLayout.SOUTH);
		_statusPanel.add(_progressBar, BorderLayout.NORTH);

		//Add the media player, progress bar and controls to the
		//MediaPlayer panel
		add(_mediaPlayerComponent, BorderLayout.CENTER);
		add(_statusPanel, BorderLayout.SOUTH);

	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == _play) {
			if(!_video.canPause()) {
				_currentFile = CurrentFile.getInstance();
				if(_currentFile.getType() == null) {
					JOptionPane.showMessageDialog(null, "Please open a video file before playing");
					return;
				} else {
					Image pauseImage;
					try {
						pauseImage = ImageIO.read(new File("res/Pause.png"));
						pauseImage = pauseImage.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
						_play.setIcon(new ImageIcon(pauseImage));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					_video.playMedia(_currentFile.getPath());
				}
			} else {
				if(!_isPaused) {
					try {
						Image playImage = ImageIO.read(new File("res/Play.png"));
						playImage = playImage.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
						_play.setIcon(new ImageIcon(playImage));
						_video.pause();
						_isPaused = true;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						Image pauseImage = ImageIO.read(new File("res/Pause.png"));
						pauseImage = pauseImage.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
						_play.setIcon(new ImageIcon(pauseImage));
						_video.pause();
						_isPaused = false;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			_stop.setEnabled(true);
			_pause.setEnabled(true);
			_mute.setEnabled(true);
			_fastForward.setEnabled(true);
			_rewind.setEnabled(true);
			_navigateClock.stop();
			_isFastForward = false;
			_isRewind = false;
		} else if(ae.getSource() == _stop) {
			_video.stop();
			Image playImage;
			try {
				playImage = ImageIO.read(new File("res/Play.png"));
				playImage = playImage.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
				_play.setIcon(new ImageIcon(playImage));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			_stop.setEnabled(false);
			_play.setEnabled(true);
			_pause.setEnabled(false);
			_mute.setEnabled(false);
			_fastForward.setEnabled(false);
			_rewind.setEnabled(false);
			_navigateClock.stop();
			_isFastForward = false;
			_isRewind = false;
		} else if(ae.getSource() == _fastForward) {
			_fastForward.setEnabled(false);
			_navigateClock.start();
			_isFastForward = true;
		} else if(ae.getSource() == _rewind) {
			_rewind.setEnabled(false);
			_navigateClock.start();
			_isRewind = true;
		} else if(ae.getSource() == _mute) {
			if(_video.isMute()) {
				try {
					Image muteImage = ImageIO.read(new File("res/Mute.png"));
					muteImage = muteImage.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
					_mute.setIcon(new ImageIcon(muteImage));
					_video.mute();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					Image unMuteImage = ImageIO.read(new File("res/Unmute.png"));
					unMuteImage = unMuteImage.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
					_mute.setIcon(new ImageIcon(unMuteImage));
					_video.mute();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if(ae.getSource() == _clock) {
			int time = (int) (_video.getTime()/1000.0);
			_progressBar.setValue(time);
			String stringTime = formatTime(time);
			_timer.setText(stringTime);
		} else if(ae.getSource() == _navigateClock) {
			if(_isFastForward) {
				_video.skip(1000);
			} else if(_isRewind) {
				_video.skip(-1000);
			}
		}
	}


	public String formatTime(int time) {
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
