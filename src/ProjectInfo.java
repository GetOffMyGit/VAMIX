import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListModel;
import javax.swing.SwingWorker;




public class ProjectInfo {
	private String _adjustVolume;

	private DefaultListModel<AudioFile> _overlays;
	private static ProjectInfo _instance;
	private boolean _isStripped;
	private boolean _isReplaced;
	private List<String> _commands;
	private AudioFile _newAudio;
	public TextInfo _intro;
	public TextInfo _outro;
	private final String _tempFileName = ".temp2.mp4";
	private ProgressFrame _progressFrame;
	private JProgressBar _progressBar = new JProgressBar();
	private JLabel _durationLabel = new JLabel();
	private JLabel _timerLabel = new JLabel();
	private Process _process;
	final private static String _projectloc = System.getProperty("user.home")
			//+ System.getProperty("file.separator") + "projects"
			+ System.getProperty("file.separator") + "project001.txt";
	
	protected ProjectInfo() {
		// create 
		_overlays = new DefaultListModel<AudioFile>();
		_commands = new ArrayList<String>();
		_isStripped = false;

		_newAudio = null;
		_isReplaced = false;
		_intro = new TextInfo();
		_outro = new TextInfo();
	}
	
	public static ProjectInfo getInstance() {
		if (_instance == null) {
			_instance = new ProjectInfo();
		} 
		return _instance;
	}
	
	public void saveProject() {
		File f = new File(_projectloc);
		if (f.exists() && !f.isDirectory()) {
			// 
		} else {
			// make any necessary parent directories
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// append to file with corresponding operation
		try {
			// no appending, overwrites file
			// writes every new detail on the file
			String ls = System.getProperty("line.separator");
			FileWriter fw = new FileWriter(_projectloc, false);
			fw.write(CurrentFile.getInstance().getPath() + ls);
			// true = 1, false = 0
			fw.write((_isStripped? "1" : "0") + ls);
			fw.write((_isReplaced? "1" : "0") + ls);
			if (_newAudio == null) {
				fw.write("" + ls);
			} else {
				fw.write(_newAudio.getPath() + ls);
			}
			fw.write(_overlays.size() + ls);
			
			for (int i = 0; i < _overlays.size(); i ++) {
				AudioFile a = (AudioFile)_overlays.getElementAt(i);
				fw.write(a.getPath() + ls);
			}
			
			fw.write(_intro.forFile() + ls);
			fw.write(_outro.forFile() + ls);
			// text stuff
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void load() {
		File f = new File(_projectloc);
		if (f.exists() && !f.isDirectory()) {

			try {
				InputStream input = new FileInputStream(_projectloc);
				BufferedReader br = new BufferedReader(new InputStreamReader(input));
				// set up current file
				reset();
				File currentFile = new File(br.readLine());
				CurrentFile.getInstance().resetInstance();
				CurrentFile.getInstance().setInstance(currentFile);
				if (br.readLine().equals("0")) {
					_isStripped = false;
				} else {
					_isStripped = true;
				}
				
				if (br.readLine().equals("0")) {
					_isReplaced = false;
				} else {
					_isReplaced = true;
				}
				String newAudioPath = br.readLine();
				if (!newAudioPath.equals("")) {
					File newAudioFile = new File(newAudioPath);
					_newAudio = new AudioFile(newAudioFile);	
				}
				
				int numOverlays = Integer.parseInt(br.readLine());
				for (int i = 0; i < numOverlays; i ++) {
					String overlayPath = br.readLine();
					File overlay = new File(overlayPath);
					addOverlay(new AudioFile(overlay));
				}
				
				String introText = br.readLine();
				String[] details = introText.split("#");

				//int color, int size, int font, String text, Integer integerDuration, String colort, String sizet, String fontt)
				_intro = new TextInfo(Integer.parseInt(details[0]),
						Integer.parseInt(details[1]),
						Integer.parseInt(details[2]),
						details[3], Integer.parseInt(details[4]), details[5], details[6], details[7]);
				String outroText = br.readLine();
				details = outroText.split("#");
				_outro = new TextInfo(Integer.parseInt(details[0]), Integer.parseInt(details[1]), Integer.parseInt(details[2]),
						details[3], Integer.parseInt(details[4]), details[5], details[6], details[7]);
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Done with the file
		} else {
			// project not existing
		}
	}

	public static void reset() {
		_instance = new ProjectInfo();
	}

	public void addOverlay(AudioFile f) {
		_overlays.addElement(f);
	}

	public ListModel<AudioFile> getOverlays() {
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
		String command = "avconv -i " + CurrentFile.getInstance().getPath() + "-filter_complex volume=volume=" + stringInput + " output.avi";
		_adjustVolume = command;
	}

	public String getVolume() {
		return _adjustVolume;
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
			String cmd = "avconv -i " + CurrentFile.getInstance().getPath() + " -map 0:v " + _tempFileName;
			_commands.add(cmd);
			System.out.println(cmd);
			command += "-i " + _tempFileName + " -strict experimental " ;
			//no increase in audio inputs because there would be no audio stream
			//input is not stripped therefore include the audio
		} else {
			command += "-i " + CurrentFile.getInstance().getPath() + " -strict experimental " ;
			inputs++;
		}

		//avconv -i "video_file" -map 0:v "output_file"

		if (!(_overlays.isEmpty())) {
			for (int i = 0; i < _overlays.size(); i ++) {
				AudioFile f = (AudioFile)_overlays.getElementAt(i);
				command += "-i " + f.getPath() + " -strict experimental ";
				inputs++;
			}
		} else {
			// no overlays and when they just want the no audio video file
		}
		
		
		command += _intro.toCommandIntro() + _outro.toCommandOutro();
		// adds filter complex when it is not the special case of 
		// no overlays, stripped and isnt replaced ie just want no audio video file
		if (!(_overlays.isEmpty() & _isStripped & !_isReplaced)) {
			command += "-filter_complex amix=inputs=" + inputs + ":duration=longest ";
		}
		command += System.getProperty("user.home")  + System.getProperty("file.separator") + outputName;
		_commands.add(command);
		System.out.println(command);
	}


	public void finish() {
		//delete temp files created
		File f1 = new File(_tempFileName);
		if (f1.exists() && !f1.isDirectory()) {
			f1.delete();
		}
	}

	public boolean anyChanges() {
		if (!_isStripped  && !_isReplaced && _overlays.isEmpty()) {
			return false;
		} else {
			return true;	
		}

	}

	public void render(String outputName) {
		File f = new File(_tempFileName);
		// ensure temp file is deleted
		if (f.exists() && !f.isDirectory()) {
			f.delete();
		}
		audioCombine(outputName);
		//"allow non-standardized experimental things"
		//avconv -i x.mp4 -strict experimental -i a.mp3 -strict experimental -map 0:v -map 1:a output.mp4
		commandWorker _audioWorker = new commandWorker(outputName);
		System.out.println(_isStripped);
		_audioWorker.execute();
	}

	public boolean enable_Strip() {
		if (_isStripped) {
			// disable button
			return false;
		} else {
			return true;
		}
	}


	class commandWorker extends SwingWorker<Void, Integer> {
		String _outputName = "";
		public commandWorker(String outputName) {
			_outputName = outputName;
		}

		@Override
		protected Void doInBackground() throws Exception {
			int i = 1;

			for (String s : _commands) {
				ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", s);
				builder.redirectErrorStream(true);
				try {
					_process = builder.start();
						InputStream stdout = _process.getInputStream();
						InputStream stderr = _process.getErrorStream();
						BufferedReader stdoutBuffered =	new BufferedReader(new InputStreamReader(stdout));
						String line = null;
						if (!(_isStripped)) {
							_progressFrame = new ProgressFrame(_outputName, 0);
						} else {
							//when it is stripped, needs to create temp video file - first command ie when i = 0
									// when it is not stripped, first command
							// there are two stages to the process
							_progressFrame = new ProgressFrame(_outputName, i);
						}

						
						Pattern timePattern = Pattern.compile("time=\\d\\d\\.\\d\\d|\\d\\d\\d\\.\\d\\d|\\d\\d\\d\\d\\.\\d\\d");
						Matcher timeMatcher;
						while((line = stdoutBuffered.readLine()) != null) {
							timeMatcher = timePattern.matcher(line);
							if(timeMatcher.find()) {
								String time = timeMatcher.group(0).replaceAll("time=", "");
								String[] timeParts = time.split("\\.");
								Integer intTime = Integer.parseInt(timeParts[0]);
								publish(intTime);
							}
						}
					//}
				} catch (IOException e1) {
					e1.printStackTrace();
				}


			_process.waitFor();

			_process.destroy();
				i++;
				
				_progressFrame.dispose();
			}

			return null;
		}

		@Override
		protected void process(List<Integer> chunks) {
			for(int i : chunks) {
				_progressBar.setValue(i);
				String stringI = Integer.toString(i);
				_timerLabel.setText(stringI);
			}
		}

		@Override
		protected void done() {
			finish();
			try {
				if(_process.waitFor() != 0) {
					JOptionPane.showMessageDialog(null, "An error occured while rendering"); 
				} else {
					JOptionPane.showMessageDialog(null, "Render successful");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// removes the frame
		}
	}
	
	public class ProgressFrame extends JFrame {
		private JPanel _progressPanel = new JPanel();
		private JPanel _labelsPanel = new JPanel();
		private JLabel _renderingLabel = new JLabel();
		
		public ProgressFrame(String _outputName, int i) {
			setTitle("Progress");
			setLayout(new BorderLayout());
			setVisible(true);
			setPreferredSize(new Dimension(600, 100));
			
			_progressPanel.setLayout(new BorderLayout());
			_labelsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			
			_progressBar.setMaximum(CurrentFile.getInstance().getDurationSeconds());
			
			_timerLabel.setText("0");
			_durationLabel.setText("/ " + CurrentFile.getInstance().getDurationSeconds());
			if (i == 0) {
				_renderingLabel.setText("Rendering: " + _outputName);	
			} else {
				_renderingLabel.setText("Rendering: " + _outputName + " Stage: " + i + "/2");
			}
			
			_labelsPanel.add(_timerLabel);
			_labelsPanel.add(_durationLabel);
			
			_progressPanel.add(_progressBar, BorderLayout.CENTER);
			_progressPanel.add(_renderingLabel, BorderLayout.NORTH);
			_progressPanel.add(_labelsPanel, BorderLayout.SOUTH);
			
			add(_progressPanel, BorderLayout.CENTER);
			pack();
		}

	}

	public void Stripped() {
		_isStripped = true;
	}


	// singleton class accessed by evertyhing

	// methods 
	// add to list

	// compress into one output by looping through list of avconv commands
	// find latest strip/replace command work from there
	// only latest text or have text in gui - edit there rather than be a command?
}