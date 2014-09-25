import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;


//TODO: COMMENTS.

public class DownloadFrame extends JFrame implements ActionListener {
	private JLabel urlLabel = new JLabel("URL:");
	private JTextField url = new JTextField(35);
	private JCheckBox openSource = new JCheckBox("Open source? ");
	private JButton dlBtn = new JButton("Download");
	private JPanel dlInfo = new JPanel();
	private JPanel processInfo = new JPanel();
	private JProgressBar progressBar = new JProgressBar();
	private JButton cancelBtn = new JButton("Cancel");
	private JLabel infoBar = new JLabel("");
	private dlWorker worker = null;
	private boolean _isWorking = false;

	
	
	public DownloadFrame() {
		setTitle("Lavitasy Download");
		setLayout(new BorderLayout());
		setVisible(true);
		setPreferredSize(new Dimension(450, 117));
		pack();
		dlBtn.addActionListener(this);
		
		Color backgroundColor = new Color(70, 73, 74);
		dlInfo.setBackground(backgroundColor);
		processInfo.setBackground(backgroundColor);
		openSource.setBackground(backgroundColor);
		
		Color textColor = new Color(203, 205, 207);
		urlLabel.setForeground(textColor);
		openSource.setForeground(textColor);
		
		Color buttonColor = new Color(220, 222, 224);
		dlBtn.setBackground(buttonColor);
		cancelBtn.setBackground(buttonColor);
		cancelBtn.setBorderPainted(false);
		
		dlInfo.setLayout(new BorderLayout());
		dlInfo.add(urlLabel, BorderLayout.WEST);
		dlInfo.add(url, BorderLayout.CENTER);
		dlInfo.add(openSource, BorderLayout.EAST);
		dlInfo.add(dlBtn, BorderLayout.SOUTH);
		
		add(dlInfo, BorderLayout.NORTH);
		
		cancelBtn.addActionListener(this);
		processInfo.setLayout(new BorderLayout());
		processInfo.add(progressBar, BorderLayout.NORTH);
		processInfo.add(infoBar, BorderLayout.WEST);
		processInfo.add(cancelBtn, BorderLayout.EAST);
		
		add(processInfo, BorderLayout.SOUTH);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == dlBtn) {
			String fileName = findFileName();
	
			if (fileName == null) {
				JOptionPane.showMessageDialog(this, "ERROR: " + " no valid URL has been given.");
				return;
			}
			
			//check for open source
			if (!openSource.isSelected()) {
				JOptionPane.showMessageDialog(this, "ERROR: " + fileName + " is not open source.");
				return;
			}
			
			if (_isWorking == true) {
				int reply = JOptionPane.showConfirmDialog(this, "ERROR: " + worker._filename + " is already downloading." + System.getProperty("line.separator") 
						+ "Stop downloading " + worker._filename + "?", "Stop downloading", JOptionPane.YES_NO_OPTION);
				
				if (reply == JOptionPane.NO_OPTION) {
					return;
				}
				// stops current download and starts new one
				// confirms again incase download already finished
				if (_isWorking == true) {
					worker.cancelDl();
				}
			}
			
			
			String filePath = System.getProperty("user.home") + System.getProperty("file.separator") + fileName;
			File f = new File(filePath);
			// check for existing file
			if (f.exists() && !f.isDirectory()) {
				String[] buttons = { "Resume", "Override", "Cancel" };
				String error = "ERROR: " + fileName + " already exists";
				int reply = JOptionPane.showOptionDialog(this, error, "ERROR", 0, 0, null, buttons, buttons[0]);
				
				if (buttons[reply].equals("Cancel")) { return;
				} else if (buttons[reply].equals("Resume")) {
					dl(fileName, Options.RESUME);
				} else {
					f.delete();
					dl(fileName, Options.NEW);
				}
			} else {
				dl(fileName, Options.NEW);
			}
		} else if (e.getSource() == cancelBtn) {
			if (_isWorking == false) {
				JOptionPane.showMessageDialog(this,
						"ERROR: no file is currently being downloaded.");
			} else if (progressBar.getValue() != 100) {
				worker.cancelDl();
			}
		}
		
	}
	// extracts last part of url too generate file name
	private String findFileName() {
		String filename = null;
		String urlLink = url.getText();
		
		if (urlLink.equals("")) {
			return null;
		}

		String cmd = "basename \"" + urlLink + "\"";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		
		Process process = null;
		builder.redirectErrorStream(true);
		try {
			process = builder.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		InputStream stdout = process.getInputStream();
		BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));		
		try {
			filename = stdoutBuffered.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		process.destroy();
		return filename;
	}
	
	private void dl(String fileName, Options o)
	{
		// create worker to download on another thread
		worker = new dlWorker(fileName, o);
		worker.execute();
	}
	
	private enum Options {
		NEW (""), RESUME ("-c ");
		
		private final String addon;
		private Options(String s) {
	        addon = s;
	    }
		
		public String toString(){
		       return addon;
		}
	}
	
	private void finishDl() {
		if (!worker.cancelled) {
			
			//TODO: exit status, ASK PAUL ^_^
			if (worker._exitStatus == 0) {
				JOptionPane.showMessageDialog(this, "Successful download of " + worker._filename + "!");
				//LogDetails log = LogDetails.getInstance();
				//log.writeLog(LogDetails.Type.DOWNLOAD);
			} else {
				progressBar.setValue(0);
				infoBar.setText("");
				JOptionPane.showMessageDialog(this, "ERROR encountered, could not download " + worker._filename + ".");
			}
		 } else if (worker.alreadyFin == true) {
			 JOptionPane.showMessageDialog(this, "ERROR: " + worker._filename + " has already finished downloadng.");
			 
		 } else {
			 if (worker._exitStatus == 3) {
				 
			 }
			 // explicitly cancelled
			 JOptionPane.showMessageDialog(this, worker._filename + " has stopped downloadng.");
		 }
	}
	
	class dlWorker extends SwingWorker<Void, Integer> {
		private String _filename =  null;
		private Options _option;
		private boolean cancelled = false;
		private boolean alreadyFin = false;
		private int _exitStatus;
		
		public dlWorker(String filename, Options o) {
			_filename = filename;
			_option = o;
			_isWorking = true;
			infoBar.setText("Downloading.. " + _filename + " 0% ");
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			String urlLink = url.getText();
			String line;
			
			// progress line bars are seen as dots
			// Suppress quotation marks
			String cmd = "wget " + _option.toString() + "--progress=dot --timeout=5 -P " + System.getProperty("user.home") + " \"" + urlLink + "\"";
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			
			builder.redirectErrorStream(true);
			Process process = null;
			try {
				process = builder.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
            while ((line = stdoutBuffered.readLine()) != null && !isCancelled()) {
            	// attempting to resume completed file - error
            	if ((line.contains("The file is already fully retrieved; nothing to do."))) {
            		cancelDl();
            		alreadyFin = true;
            		publish(0);
            		return null;
            	}
            	// progress line bars are seen as dots and progress % can be found on lines containing these line bars
            	if (line.contains("..........")) {
            		// progress of current % 
            		String[] result = line.split("%");
                	String current = result[0].substring(result[0].length()-2, result[0].length());
                	// cases when it is less than 10
                	if ((current.charAt(0)) == ' ') {
                		current = current.charAt(1) + "";
                	} else if (current.equals("00")) {
                		current = "100";
                	}
                	int progress = Integer.parseInt(current);
                	publish(progress);
            		
            	}
            }
            
	            
            //_exitStatus = process.waitFor();
            if (!isCancelled()) {
            	_exitStatus = process.waitFor();
            	publish(100);
            // Interrupted error
            } else {
            	publish(0);
            }
            
            process.destroy();
			return null;
		}
		
		 @Override
	     protected void process(List<Integer> chunks) {
			 // update the progress bar intermediately
	         for (int i : chunks) {
	        	progressBar.setValue(i);
	     		infoBar.setText("Downloading.. " + _filename + " " + progressBar.getValue()
	     				+ " %");

	     		if (progressBar.getValue() == 100) {
	     			infoBar.setText("Completed download of.. " + _filename);
	     		}
	         }
	     }
		 
		 @Override
		 protected void done() {
			finishDl();
			_isWorking = false;
		 }
		 
		 
		 public void cancelDl() {
			 cancelled = true;
			 cancel(true);
		 }
	
	}

}

