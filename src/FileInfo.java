import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class FileInfo extends JPanel {
	private JLabel _name;
	private JLabel _duration;
	private JLabel _type;
	private JLabel _bitRate;
	private JLabel _durationSeconds;
	private CurrentFile _currentFile;
	
	
	public FileInfo() {
		_currentFile = CurrentFile.getInstance();
		if(_currentFile.getName() == null) {
			_name = new JLabel("Name:");
		} else {
			_name = new JLabel("Name: " + _currentFile.getName());
		}
		
		if (_currentFile.getDuration() == null) {
			_duration = new JLabel("Duration:");
		} else {
			_duration = new JLabel("Duration: " + _currentFile.getDuration());
		}
		
		if(_currentFile.getType() == null) {
			_type = new JLabel("Type:");
		} else {
			_type = new JLabel("Type: " + _currentFile.getType());
		}
		
		if(_currentFile.getBitRate() == null) {
			_bitRate = new JLabel("BitRate:");
		} else {
			_bitRate = new JLabel("BitRate: " + _currentFile.getBitRate());
		}
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		_durationSeconds = new JLabel("Duration in Seconds: " + _currentFile.getDurationSeconds());
		
		add(_name);
		add(_duration);
		add(_type);
		add(_bitRate);
		add(_durationSeconds);
	}
}
