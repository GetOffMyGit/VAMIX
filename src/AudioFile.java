import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class AudioFile {
	private File _file;
	private String _name;
	private String _type;
	
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
							_type = "Video";
						} else if(streamSplit[i].equals("Audio:")) {
							if(_type == null) {
								_type = "Audio";
							} else {
								_type = "Video with Audio";
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
	
	public String getName() {
		return _name;
	}
	
	public String getType() {
		return _type;
	}
	
	public String getPath() {
		return _file.getAbsolutePath();
	}
}
