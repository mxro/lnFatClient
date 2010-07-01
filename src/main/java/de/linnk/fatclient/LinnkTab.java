package de.linnk.fatclient;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import de.linnk.fatclient.document.DocumentPanelContainer;

public class LinnkTab extends JPanel {

	private static final long serialVersionUID = 1L;
	private JScrollPane jScrollPane = null;

	private final DocumentPanelContainer holder;
	

	/**
	 * This is the default constructor
	 */
	public LinnkTab(DocumentPanelContainer holder) {
		super();
		this.holder = holder;
		
		this.initialize();
		this.jScrollPane.setViewportView(holder);
		//itemToolbars = new ItemToolbars();
		//this.getJPanelToolbars().add(itemToolbars);
		//holder.setItemToolbars(itemToolbars);
		
		
		//holder.setDocumentActionsPanel(this.getJPanelDocumentActions());
		//holder.setSelectedItemActionsPanel(this.getJPanelItemActions());
		holder.setFileDropTargetComponent(this);
	}

	
	public DocumentPanelContainer getHolder() {
		return this.holder;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(this.getJScrollPane(), null);
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (this.jScrollPane == null) {
			this.jScrollPane = new JScrollPane();
			this.jScrollPane.setBorder(null);
			this.jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);//JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			this.jScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return this.jScrollPane;
	}
	

}
