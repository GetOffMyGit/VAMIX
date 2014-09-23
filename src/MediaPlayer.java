import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.Timer;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;


public class MediaPlayer extends JPanel implements ActionListener {

	private EmbeddedMediaPlayerComponent _mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
	private EmbeddedMediaPlayer _video = _mediaPlayerComponent.getMediaPlayer();
	private JButton _play = new JButton("Play");
	private JButton _pause = new JButton("Pause");
	private JButton _stop = new JButton("Stop");
	private JButton _mute = new JButton("Mute");
	private JButton _fastForward = new JButton(">>");
	private JButton _rewind = new JButton("<<");
	private JSlider _volumeControl;
	private JPanel _controls = new JPanel();
	private JProgressBar _progressBar = new JProgressBar();
	private Timer _clock;

	private CurrentFile _currentFile = CurrentFile.getInstance();

	public MediaPlayer() {
		NativeLibrary.addSearchPath(
				RuntimeUtil.getLibVlcLibraryName(), "/usr/lib"
				);

		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

		setLayout(new BorderLayout());

		//Media player and control dimensions
		_mediaPlayerComponent.setPreferredSize(new Dimension(800, 475));
		_controls.setPreferredSize(new Dimension(800, 50));

		//Create and start clock
		_clock = new Timer(500, this);
		_clock.start();

		//Set starting value and minimum/maximum of the progress bar
		_progressBar.setValue(0);
		_progressBar.setMinimum(0);
		_progressBar.setMaximum(_currentFile.getDurationSeconds());

		//Disable all buttons except the play button at initial startup
		_pause.setEnabled(false);
		_stop.setEnabled(false);
//		_mute.setEnabled(false);
		_fastForward.setEnabled(false);
		_rewind.setEnabled(false);

		//Slider configurations
		_volumeControl = new JSlider();
		
		//Add control components to the control panel
		_controls.setLayout(new FlowLayout(FlowLayout.LEFT));
		_controls.add(_play);
		_controls.add(_pause);
		_controls.add(_stop);
		_controls.add(_mute);
		_controls.add(_fastForward);
		_controls.add(_rewind);
		_controls.add(_volumeControl);

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
			if(!_video.canPause()) {
				_currentFile = CurrentFile.getInstance();
				if(_currentFile.getType() == null) {
					JOptionPane.showMessageDialog(null, "Please open an audio or video file before playing");
					return;
				} else {
					_video.playMedia(_currentFile.getPath());
				}
			} else {
				_video.pause();
			}
			_play.setEnabled(false);
			_stop.setEnabled(true);
			_pause.setEnabled(true);
			_mute.setEnabled(true);
			_fastForward.setEnabled(true);
			_rewind.setEnabled(true);
		} else if(ae.getSource() == _pause) {
			_video.pause();
			_play.setEnabled(true);
			_pause.setEnabled(false);
		} else if(ae.getSource() == _stop) {
			_video.stop();
			_stop.setEnabled(false);
			_play.setEnabled(true);
			_pause.setEnabled(false);
			_mute.setEnabled(false);
			_fastForward.setEnabled(false);
			_rewind.setEnabled(false);
		} else if(ae.getSource() == _fastForward) {
			_video.skip(1000);
		} else if(ae.getSource() == _rewind) {
			_video.skip(-1000);
		} else if(ae.getSource() == _mute) {
			_video.mute();
		} else if(ae.getSource() == _clock) {
			int time = (int) (_video.getTime()/1000.0);
			_progressBar.setValue(time);
		}
	}
}
