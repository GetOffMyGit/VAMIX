import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


public class OverlayPanel  extends JPanel  implements ActionListener  {
	private JList _overlaylist;
	
	private static ProjectInfo _projectInfo;
	private JLabel _label = new JLabel("Audio overlays");
	private static JButton _delete = new JButton("Delete");
	private JButton _add = new JButton("Add");
	private JPanel _buttons = new JPanel(new FlowLayout());

	public OverlayPanel() {
		super(new BorderLayout());
		_projectInfo = ProjectInfo.getInstance();
		_overlaylist = new JList(_projectInfo.getOverlays());
		
		int size = _projectInfo.getOverlays().getSize();
		if (size == 0) {
	    	_delete.setEnabled(false);

	    }
		// only one selection
		_overlaylist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_overlaylist.setLayoutOrientation(JList.VERTICAL);

		JScrollPane listScroller = new JScrollPane(_overlaylist);
		listScroller.setPreferredSize(new Dimension(200,800));
		add(listScroller, BorderLayout.CENTER);
		_buttons.add(_add);
		_buttons.add(_delete);
		add(_buttons, BorderLayout.SOUTH);
		_delete.addActionListener(this);
		_add.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _delete) {
			int index = _overlaylist.getSelectedIndex();
			if (index == -1) {
				JOptionPane.showMessageDialog(this,
						"ERROR: no audio file selected");
				return;
			}
			_projectInfo.removeOverlay(index);
			
			int size = _projectInfo.getOverlays().getSize();

		    if (size == 0) {
		    	_delete.setEnabled(false);

		    } else { //Select next index
		        if (index == size) {
		            //removed item in last position
		            index--;
		        }

		        _overlaylist.setSelectedIndex(index);
		        _overlaylist.ensureIndexIsVisible(index);
		    }
			
		} else if (e.getSource() == _add) {
			OverlayAudio();
		}
		
	}
	
	public static void OverlayAudio() {
		JFileChooser fileChooser = new JFileChooser();
		int fileChooserReturn = fileChooser.showOpenDialog(null);
		if(fileChooserReturn == JFileChooser.APPROVE_OPTION) {
			File theFile = fileChooser.getSelectedFile();
			AudioFile overlay = new AudioFile(theFile);
			if(overlay.getType() == null) {
				JOptionPane.showMessageDialog(null, "Please select an audio file");
			} else if (!(overlay.getType().equals("Audio"))) {
				JOptionPane.showMessageDialog(null, "Please select an audio file");
				
			} else {
				_projectInfo.addOverlay(overlay);
				int size = _projectInfo.getOverlays().getSize();
				 if (size != 0) {
				    	_delete.setEnabled(true);

				 }
			}
		}
	}
}
