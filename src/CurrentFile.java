import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;


public class CurrentFile {
	private static CurrentFile instanceFile;
	private String _duration;
	private String _type;
	private String _bitRate;

	private CurrentFile() {

	}

	public static CurrentFile getInstance() {
		if(instanceFile == null) {
			instanceFile = new CurrentFile();
		}

		return instanceFile;
	}

	public void setInstance(File passedFile) {
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
							_duration = durationSplit[i + 1];
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
	
	public String getType() {
		return _type;
	}
	
	public String getDuration() {
		return _duration;
	}
	
	public String getBitRate() {
		return _bitRate;
	}
}
