import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;


public class CurrentFile {
	private static CurrentFile instanceFile;
	private File _file;
	private String _name;
	private String _duration;
	private String _type;
	private String _bitRate;
	private int _durationSeconds;

	private CurrentFile() {

	}

	public static CurrentFile getInstance() {
		if(instanceFile == null) {
			instanceFile = new CurrentFile();
		}

		return instanceFile;
	}

	public void setInstance(File passedFile) {
		_name = passedFile.getName();
		_file = passedFile;
		ProcessBuilder builder = new ProcessBuilder("avconv", "-i", passedFile.getAbsolutePath());
		try {
			Process process = builder.start();
			InputStream stdout = process.getInputStream();
			InputStream stderr = process.getErrorStream();
			BufferedReader stderrBuffered =	new BufferedReader(new InputStreamReader(stderr));

			String line = null;

			while((line = stderrBuffered.readLine()) != null) {
				if(line.contains("Stream")) {
					String[] streamSplit = line.split(" ");
					for(int i = 0; i < streamSplit.length; i++) {
						if(streamSplit[i].equals("Video:")) {
							_type = "Video";
						} else if(streamSplit[i].equals("Audio:")) {
							if(_type == null) {
								_type = "Audio";
							} else {
								_type = "Video with Audio";
							}
						}
					}
				} else if(line.contains("Duration")) {
					String[] durationSplit = line.split(" ");
					for(int i =0 ; i < durationSplit.length; i++) {
						if(durationSplit[i].equals("Duration:")) {
							_duration = durationSplit[i + 1].replace(",", "");
							_durationSeconds = calculateTime(_duration);
						} else if(durationSplit[i].equals("bitrate:")) {
							_bitRate = durationSplit[i + 1];
							if(!(_bitRate.equals("N/A"))) {
								_bitRate += " " + durationSplit[i + 2];
							}
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void resetInstance() {
		_name = null;
		_type = null;
		_type = null;
		_duration = null;
		_bitRate = null;
	}
	
	public int calculateTime(String duration) {
		String[] timeParts = duration.split(":");
		String[] secondsParts = timeParts[2].split("\\.");
		int seconds = Integer.parseInt(secondsParts[0]);
		int hours = Integer.parseInt(timeParts[0]);
		int minutes = Integer.parseInt(timeParts[1]);
		
		int time = seconds;
		time += (minutes * 60);
		time += (hours * 3600);
		
		return time;
	}
	
	public String getName() {
		return _name;
	}
	
	public String getType() {
		return _type;
	}
	
	public String getDuration() {
		return _duration;
	}
	
	public String getBitRate() {
		return _bitRate;
	}
	
	public int getDurationSeconds() {
		return _durationSeconds;
	}
	
	public String getPath() {
		return _file.getAbsolutePath();
	}
}
