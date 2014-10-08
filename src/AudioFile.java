import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class AudioFile {
	private File _file;
	private String _name;
	private String _type;
	private String _duration;
	private int _durationSeconds;
	
	public AudioFile(File passedFile) {
		_name = passedFile.getName();
		_file = passedFile;
		ProcessBuilder builder = new ProcessBuilder("avconv", "-i", passedFile.getAbsolutePath());
		try {
			Process process = builder.start();
			InputStream stderr = process.getErrorStream();
			BufferedReader stderrBuffered =	new BufferedReader(new InputStreamReader(stderr));

			String line = null;

			while((line = stderrBuffered.readLine()) != null) {
				if(line.contains("Stream")) {
					String[] streamSplit = line.split(" ");
					for(int i = 0; i < streamSplit.length; i++) {
						if(streamSplit[i].equals("Video:")) {
							if(streamSplit[i+1].equals("mjpeg,")) {
								_type = "Audio";
							} else {
								if(_type == null) {
									_type = "Video";
								} else {
									_type = "Video with Audio";
								}
							}
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
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public int getDurationSeconds() {
		int durationSeconds = _durationSeconds;
		return durationSeconds;
	}
	
	public String getType() {
		return _type;
	}
	
	public String getPath() {
		return _file.getAbsolutePath();
	}
	
	@Override 
	public String toString() {
		return this._name;
	}
}
