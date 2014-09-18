import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
	private JLabel _timeDisplay = new JLabel("0 seconds");
	private JPanel _controls = new JPanel();
	
	public MediaPlayer() {
		NativeLibrary.addSearchPath(
                RuntimeUtil.getLibVlcLibraryName(), "/usr/lib"
            );
		
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		
		setLayout(new BorderLayout());
		
		_mediaPlayerComponent.setPreferredSize(new Dimension(800, 400));
		_controls.setPreferredSize(new Dimension(800, 50));
		
		_controls.setLayout(new GridLayout());
		_controls.add(_play);
		_controls.add(_pause);
		_controls.add(_stop);
		_controls.add(_mute);
		_controls.add(_fastForward);
		_controls.add(_rewind);
		
		_play.addActionListener(this);
		_pause.addActionListener(this);
		_stop.addActionListener(this);
		_mute.addActionListener(this);
		_fastForward.addActionListener(this);
		_rewind.addActionListener(this);
		
		add(_mediaPlayerComponent, BorderLayout.NORTH);
		add(_controls, BorderLayout.SOUTH);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == _play) {
			_video.playMedia("/home/paul/workspace/Lavitasy/big_buck_bunny_480p_stereo.avi");
			_video.play();
			_play.setEnabled(false);
			_stop.setEnabled(true);
		} else if(ae.getSource() == _pause) {
			_video.pause();
			_play.setEnabled(true);
		} else if(ae.getSource() == _stop) {
			_video.stop();
			_stop.setEnabled(false);
			_play.setEnabled(true);
		} else if(ae.getSource() == _fastForward) {
			_video.skip(1000);
		} else if(ae.getSource() == _rewind) {
			_video.skip(-1000);
		} else if(ae.getSource() == _mute) {
			_video.mute();
		}
	}
}
