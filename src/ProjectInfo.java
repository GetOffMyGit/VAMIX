import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;



public class ProjectInfo {
	// text file associated
	// read and write to file
	// list of avconv commands - add
	// ie data source
	// text details start
	// text details end
	// overlay file
	private CurrentFile _currentFile = CurrentFile.getInstance();
	private String _adjustVolume;
	
	private DefaultListModel _overlays;
	private static ProjectInfo _instance;
	
	protected ProjectInfo() {
		// create 
		_overlays = new DefaultListModel<AudioFile>();
	}
	
	public static ProjectInfo getInstance() {
		if (_instance == null) {
			_instance = new ProjectInfo();
		} 
		return _instance;
	}
	
	public void addOverlay(AudioFile f) {
		_overlays.addElement(f);
	}
	
	public ListModel getOverlays() {
		return _overlays;
	}
	
<<<<<<< HEAD
	public void removeOverlay(int index) {
		_overlays.remove(index);
	}
=======
	public void adjustVolume(int initialVolume) {
		int inputVolume =initialVolume / 100;
		String stringInput = Integer.toString(inputVolume);
		String command = "avconv -i " + _currentFile.getPath() + "-filter_complex volume=volume=" + stringInput + " output.avi";
		_adjustVolume = command;
	}
	
	public String getVolume() {
		return _adjustVolume;
	}
	
>>>>>>> 34911282d97da07a509eeb0c36921a5edb9a11fd
	// singleton class accessed by evertyhing
	
	// methods 
	// add to list
	
	// compress into one output by looping through list of avconv commands
	// find latest strip/replace command work from there
	// only latest text or have text in gui - edit there rather than be a command?
}