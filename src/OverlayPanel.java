import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


public class OverlayPanel  extends JPanel  {
	private JList _overlaylist;
	
	private ProjectInfo _projectInfo;
	private JLabel _label = new JLabel("Audio overlays");
	private JButton _delete = new JButton("Delete");
	private JButton _add = new JButton("Add");
	private JPanel _buttons = new JPanel(new FlowLayout());

	public OverlayPanel() {
		super(new BorderLayout());
		_projectInfo = ProjectInfo.getInstance();
		_overlaylist = new JList(_projectInfo.getOverlays());
		
		// only one selection
		_overlaylist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_overlaylist.setLayoutOrientation(JList.VERTICAL);
		
		JScrollPane listScroller = new JScrollPane(_overlaylist);
		listScroller.setPreferredSize(new Dimension(200,800));
		add(listScroller, BorderLayout.CENTER);
		_buttons.add(_add);
		_buttons.add(_delete);
		add(_buttons, BorderLayout.SOUTH);
	}
}
