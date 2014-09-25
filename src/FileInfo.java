

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


public class FileInfo extends JPanel {
	private JLabel _name;
	private JLabel _duration;
	private JLabel _type;
	private JLabel _bitRate;
	private JLabel _durationSeconds;
	private JLabel _overlaylabel = new JLabel("Audio overlays:");
	private CurrentFile _currentFile;
	private JPanel _overlay;
	
	
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
		add(_overlaylabel);
	}
}
