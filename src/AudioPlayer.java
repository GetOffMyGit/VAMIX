import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;


public class AudioPlayer extends JPanel implements ActionListener {
	private EmbeddedMediaPlayerComponent _mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
	private EmbeddedMediaPlayer _video = _mediaPlayerComponent.getMediaPlayer();
	private JPanel _controls = new JPanel();
	private JPanel _statusPanel = new JPanel();
	private JPanel _filePanel = new JPanel();
	private JLabel _fileName = new JLabel("Name:");
	private JButton _select = new JButton("Select");
	private JButton _play = new JButton();
	private JButton _fastForward = new JButton();
	private JButton _rewind = new JButton();
	private JButton _stop = new JButton();
	private JButton _mute = new JButton();
	private JSlider _volumeControl = new JSlider(0, 200);
	private JProgressBar _progressBar = new JProgressBar();
	private AudioFile _audioFile;
	private File _chosenFile;
	private boolean _isPaused = false;
	private Timer _clock;
	private Timer _navigateClock;
	private boolean _isFastForward = false;
	private boolean _isRewind = false;
	private boolean _isMuted = false;

	public AudioPlayer() {
		NativeLibrary.addSearchPath(
				RuntimeUtil.getLibVlcLibraryName(), "/usr/lib"
				);

		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

		setLayout(new BorderLayout());
		_controls.setLayout(new FlowLayout());
		_statusPanel.setLayout(new BorderLayout());
		_filePanel.setLayout(new BorderLayout());
		
		//Set Media Player Component size
		_mediaPlayerComponent.setSize(new Dimension(0, 0));

		//Set Up buttons with images
		try {
			Image playImage = ImageIO.read(new File("res/Play.png"));
			_play.setBorderPainted(false);
			_play.setContentAreaFilled(false);
			_play.setPreferredSize(new Dimension(30, 30));
			playImage = playImage.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
			_play.setIcon(new ImageIcon(playImage));

			Image fastForwardImage = ImageIO.read(new File("res/Fastforward.png"));
			_fastForward.setBorderPainted(false);
			_fastForward.setContentAreaFilled(false);
			_fastForward.setPreferredSize(new Dimension(25, 25));
			fastForwardImage = fastForwardImage.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
			_fastForward.setIcon(new ImageIcon(fastForwardImage));

			Image muteImage = ImageIO.read(new File("res/Mute.png"));
			_mute.setBorderPainted(false);
			_mute.setContentAreaFilled(false);
			_mute.setPreferredSize(new Dimension(25, 25));
			muteImage = muteImage.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
			_mute.setIcon(new ImageIcon(muteImage));

			Image stopImage = ImageIO.read(new File("res/Stop.png"));
			_stop.setBorderPainted(false);
			_stop.setContentAreaFilled(false);
			_stop.setPreferredSize(new Dimension(30, 30));
			stopImage = stopImage.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
			_stop.setIcon(new ImageIcon(stopImage));

			Image rewindImage = ImageIO.read(new File("res/Rewind.png"));
			_rewind.setBorderPainted(false);
			_rewind.setContentAreaFilled(false);
			_rewind.setPreferredSize(new Dimension(30, 30));
			rewindImage = rewindImage.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
			_rewind.setIcon(new ImageIcon(rewindImage));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		_volumeControl.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int volume = _volumeControl.getValue();
				_video.setVolume(volume);
			}
		});
		
		//Set colour scheme
		Color backgroundColor = new Color(70, 73, 74);
		Color textColor = new Color(203, 205, 207);
		Color buttonColor = new Color(220, 222, 224);
		_controls.setBackground(backgroundColor);
		_volumeControl.setBackground(backgroundColor);
		_filePanel.setBackground(backgroundColor);
		_fileName.setForeground(textColor);
		_select.setBackground(buttonColor);
		
		_stop.setEnabled(false);
		_fastForward.setEnabled(false);
		_rewind.setEnabled(false);
		_mute.setEnabled(false);

		//Add action listeners to the control components
		_play.addActionListener(this);
		_stop.addActionListener(this);
		_mute.addActionListener(this);
		_fastForward.addActionListener(this);
		_rewind.addActionListener(this);
		_select.addActionListener(this);

		//Set up clocks
		_clock = new Timer(500, this);
		_clock.start();
		_navigateClock = new Timer(100, this);

		_controls.add(_rewind);
		_controls.add(_play);
		_controls.add(_fastForward);
		_controls.add(_stop);
		_controls.add(_mute);

		_filePanel.add(_select, BorderLayout.NORTH);
		_filePanel.add(_fileName, BorderLayout.SOUTH);

		_statusPanel.add(_progressBar, BorderLayout.NORTH);
		_statusPanel.add(_controls, BorderLayout.CENTER);
		_statusPanel.add(_volumeControl, BorderLayout.SOUTH);

		add(_filePanel, BorderLayout.NORTH);
		add(_mediaPlayerComponent, BorderLayout.CENTER);
		add(_statusPanel, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == _select) {
			JFileChooser fileChooser = new JFileChooser();
			int fileChooserReturn = fileChooser.showOpenDialog(null);
			if(fileChooserReturn == JFileChooser.APPROVE_OPTION) {
				_chosenFile = fileChooser.getSelectedFile();
				_audioFile = new AudioFile(_chosenFile);
				_progressBar.setMaximum(_audioFile.getDurationSeconds());
				if(_audioFile.getType() == null) {
					JOptionPane.showMessageDialog(null, "Please select an audio");
					_audioFile = null;
				} else if ((!(_audioFile.getType().equals("Audio")))) {
					JOptionPane.showMessageDialog(null, "Please select an audio file");
					_audioFile = null;
				} else {
					_fileName.setText("Name: " + _audioFile.getName());
				}
			}
		}
		if(ae.getSource() == _play) {
			if(!_video.canPause()) {
				if(_chosenFile == null) {
					JOptionPane.showMessageDialog(null, "Please open an audio file before playing");
					return;
				} else {
					try {
						Image pauseImage = ImageIO.read(new File("res/Pause.png"));
						pauseImage = pauseImage.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
						_play.setIcon(new ImageIcon(pauseImage));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					_video.playMedia(_audioFile.getPath());
				}
			} else {
				if(!_isPaused) {
					try {
						Image playImage = ImageIO.read(new File("res/Play.png"));
						playImage = playImage.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
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
						pauseImage = pauseImage.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
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
			_mute.setEnabled(true);
			_fastForward.setEnabled(true);
			_rewind.setEnabled(true);
			_navigateClock.stop();
			_isFastForward = false;
			_isRewind = false;
		} else if(ae.getSource() == _stop) {
			_video.stop();
			try {
				Image playImage = ImageIO.read(new File("res/Play.png"));
				playImage = playImage.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
				_play.setIcon(new ImageIcon(playImage));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			_stop.setEnabled(false);
			_play.setEnabled(true);
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
			if(_isMuted) {
				try {
					Image unMuteImage = ImageIO.read(new File("res/Unmute.png"));
					unMuteImage = unMuteImage.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
					_mute.setIcon(new ImageIcon(unMuteImage));
					_video.mute();
					_isMuted = false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					Image muteImage = ImageIO.read(new File("res/Mute.png"));
					muteImage = muteImage.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
					_mute.setIcon(new ImageIcon(muteImage));
					_video.mute();
					_isMuted = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if(ae.getSource() == _clock) {
			int time = (int) (_video.getTime()/1000.0);
			_progressBar.setValue(time);
		} else if(ae.getSource() == _navigateClock) {
			if(_isFastForward) {
				_video.skip(1000);
			} else if(_isRewind) {
				_video.skip(-1000);
			}
		}
	}
}
