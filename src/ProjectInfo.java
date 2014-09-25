import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	
	public void audioCombine(String outputName) {
		String command = "avconv ";
		int inputs = 0; 
		// adds new audio to mixer
		if (_isReplaced) {
			command += "-i " + _newAudio.getPath() + " -strict experimental ";
			inputs++;
		}  
		if (_isStripped) {
			// obtains just video from video and creates a temporary file in program space
			String cmd = "avconv -i " + _currentFile.getPath() + " -map 0:v .temp1.mp4";
			_commands.add(cmd);
			command += "-i .temp1.mp4 -strict experimental " ;
			//no increase in audio inputs because there would be no audio stream
			//input is not stripped therefore include the audio
		} else {
			command += "-i " + _currentFile.getPath() + " -strict experimental " ;
			inputs++;
		}

		if (!(_overlays.isEmpty())) {
			for (int i = 0; i < _overlays.size(); i ++) {
				AudioFile f = (AudioFile)_overlays.getElementAt(i);
				command += "-i " + f.getPath() + " -strict experimental ";
				inputs++;
				//avconv -i input1 -i input2 -filter_complex amix=inputs=3:duration=first:dropout_transition=2 output
				// need swing worker
			}
		}
		
		command += "-filter_complex amix=inputs=" + inputs + ":duration=longest " + System.getProperty("user.home") 
				+ System.getProperty("file.separator") + outputName;
		_commands.add(command);
	}
	
	public void finish() {
		//delete temp files created
		File f1 = new File(".temp1.mp4");
		f1.delete();
	}
	
	public boolean anyChanges() {
		if (!_isStripped  && !_isReplaced && _overlays.isEmpty()) {
			return false;
		} else {
			return true;	
		}
		
	}
	
	public void render(String outputName) {
			audioCombine(outputName);
			//"allow non-standardized experimental things"
			//avconv -i x.mp4 -strict experimental -i a.mp3 -strict experimental -map 0:v -map 1:a output.mp4
			commandWorker _audioWorker = new commandWorker();
			_audioWorker.execute();
	}
	
	class commandWorker extends SwingWorker<Void, Integer> {
		public commandWorker() {
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			int i = 0;
			for (String s : _commands) {
				ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", s);
				System.out.println(s);
				builder.redirectErrorStream(true);
				Process process = null;
				try {
					process = builder.start();
					// when it is stripped, needs to create temp video file - first command ie when i = 0
					// when it is not stripped, first command
					if ((_isStripped && i == 1) || (!(_isStripped))) {
						// process stuff here
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				
				process.waitFor();
				
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