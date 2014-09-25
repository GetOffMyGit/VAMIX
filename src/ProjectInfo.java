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
	// singleton class accessed by evertyhing
	
	// methods 
	// add to list
	
	// compress into one output by looping through list of avconv commands
	// find latest strip/replace command work from there
	// only latest text or have text in gui - edit there rather than be a command?
}