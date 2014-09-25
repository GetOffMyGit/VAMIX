import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.SwingWorker;




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
	private boolean _isStripped = false;
	private boolean _isReplaced = false;
	private List<String> _commands;
	private AudioFile _newAudio;
	protected ProjectInfo() {
		// create 
		_overlays = new DefaultListModel<AudioFile>();
		_commands = new ArrayList<String>();
		_isStripped = false;
		_currentFile = CurrentFile.getInstance();
		
		_newAudio = null;
		_isReplaced = false;
	}
	
	public static ProjectInfo getInstance() {
		if (_instance == null) {
			_instance = new ProjectInfo();
		} 
		return _instance;
	}
	
	public static void reset() {
		_instance = new ProjectInfo();
	}
	
	public void addOverlay(AudioFile f) {
		_overlays.addElement(f);
	}
	
	public ListModel getOverlays() {
		return _overlays;
	}
	
	public boolean isStripped() {
		return _isStripped;
	}
	

	public void removeOverlay(int index) {
		_overlays.remove(index);
	}

	public void adjustVolume(int initialVolume) {
		int inputVolume =initialVolume / 100;
		String stringInput = Integer.toString(inputVolume);
		String command = "avconv -i " + _currentFile.getPath() + "-filter_complex volume=volume=" + stringInput + " output.avi";
		_adjustVolume = command;
	}
	
	public String getVolume() {
		return _adjustVolume;
	}
	
	public void Stripped() {
		_isStripped = true;
	}
	
	public void Replace(AudioFile replaceAudio) {
		_isReplaced = true;
		_newAudio = replaceAudio;
		
	}
	
	public void audioCombine() {
		String command = "avconv ";
		int inputs = 0; 
		if (_isReplaced) {
			command += "-i " + _newAudio.getPath() + " ";
			inputs++;
		} else if (!(_isStripped)) {
			// obtains audio from video and creates a temporary file in program space
			String cmd = "avconv -i " + _currentFile.getPath() + " -map 0:a .temp1.mp3";
			_commands.add(cmd);
			command += "-i .temp1.mp3 " ;
			inputs++;
		}

		if (!(_overlays.isEmpty())) {
			for (int i = 0; i < _overlays.size(); i ++) {
				AudioFile f = (AudioFile)_overlays.getElementAt(i);
				command += "-i " + f.getPath() + " ";
				inputs++;
				System.out.println(command);
				//avconv -i input1 -i input2 -filter_complex amix=inputs=3:duration=first:dropout_transition=2 output
				// need swing worker
			}
		}
		
		command += "-filter_complex amix=inputs=" + inputs + ":duration=longest .temp2.mp3";
		_commands.add(command);
	}
	
	public void finish() {
		//delete temp files created
		File f1 = new File(".temp2.mp3");
		File f2 = new File(".temp1.mp3");
		f1.delete();
		f2.delete();
	}
	
	public boolean anyChanges() {
		if (!_isStripped  && !_isReplaced && _overlays.isEmpty()) {
			return false;
		} else {
			return true;	
		}
		
	}
	
	public void render(String outputName) {
			audioCombine();
			String command = "avconv -i " + _currentFile.getPath() + " -strict experimental -i  .temp2.mp3" 
			+ " -strict experimental -map 0:v -map 1:a " + System.getProperty("user.home") + System.getProperty("file.separator") + outputName;
			_commands.add(command);
			//avconv -i x.mp4 -strict experimental -i a.mp3 -strict experimental -map 0:v -map 1:a output.mp4
			commandWorker _audioWorker = new commandWorker();
			_audioWorker.execute();
	}
	
	class commandWorker extends SwingWorker<Void, Integer> {
		private int _exitStatus;
		private String _outputName;
		public commandWorker() {
			
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			for (String s : _commands) {
				ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", s);
				
				builder.redirectErrorStream(true);
				Process process = null;
				try {
					process = builder.start();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				_exitStatus = process.waitFor();
				System.out.println("jhl");
				
	            process.destroy();
			}
			
			return null;
		}
		
		 @Override
	     protected void process(List<Integer> chunks) {

	     }
		 
		 @Override
		 protected void done() {
			 finish();
		 }
	}


	// singleton class accessed by evertyhing
	
	// methods 
	// add to list
	
	// compress into one output by looping through list of avconv commands
	// find latest strip/replace command work from there
	// only latest text or have text in gui - edit there rather than be a command?
}