import java.awt.Color;
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
		
		Color textColor = new Color(203, 205, 207);
		
		if(_currentFile.getName() == null) {
			_name = new JLabel("Name:");
			_name.setForeground(textColor);
		} else {
			_name = new JLabel("Name: " + _currentFile.getName());
			_name.setForeground(textColor);
		}
		
		if (_currentFile.getDuration() == null) {
			_duration = new JLabel("Duration:");
			_duration.setForeground(textColor);
		} else {
			_duration = new JLabel("Duration: " + _currentFile.getDuration());
			_duration.setForeground(textColor);
		}
		
		if(_currentFile.getType() == null) {
			_type = new JLabel("Type:");
			_type.setForeground(textColor);
		} else {
			_type = new JLabel("Type: " + _currentFile.getType());
			_type.setForeground(textColor);
		}
		
		if(_currentFile.getBitRate() == null) {
			_bitRate = new JLabel("BitRate:");
			_bitRate.setForeground(textColor);
		} else {
			_bitRate = new JLabel("BitRate: " + _currentFile.getBitRate());
			_bitRate.setForeground(textColor);
		}
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		_durationSeconds = new JLabel("Duration in Seconds: " + _currentFile.getDurationSeconds());
		_durationSeconds.setForeground(textColor);
		
		add(_name);
		add(_duration);
		add(_type);
		add(_bitRate);
		add(_durationSeconds);
	}
}
