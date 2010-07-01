package de.linnk.fatclient.document;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.text.DateFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import de.linnk.Linnk;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.mxro.swing.JMyPanel;

public class DocumentPanel extends JMyPanel {

	private static final long serialVersionUID = 1L;
	protected final NodeDocument document;
	protected final DocumentPanelContainer holder;
	private JMyPanel jPanelHeading = null;
	private JMyPanel jPanelFooter = null;
	private JMyPanel jPanelItems = null;
	private JLabel jLabel = null;
	private JLabel jLabelCreated = null;
	private JLabel jURILabel = null;
	protected ItemsPanel itemspanel;
	
	
	public ItemsPanel getItemspanel() {
		return this.itemspanel;
	}

	public void select() {
		this.itemspanel.select();
	}
	
	/*public void scrollRectToVisible(Rectangle rect) {
		this.getHolder().scrollRectToVisible(aRect);
	}*/
	
	/**
	 * This is the default constructor
	 */
	public DocumentPanel(NodeDocument document, DocumentPanelContainer holder) {
		super();
		this.initialize();
		this.document = document;
		
//		if (document instanceof ChangableDocument) {
//			((ChangableDocument) document).setChangeHandler(new ChangeHandler() {
//
//				public boolean doChange(ItemChange change) {
//					// TODO Auto-generated method stub
//					return false;
//				}
//
//				public boolean undoChange(ItemChange change) {
//					// TODO Auto-generated method stub
//					return false;
//				}
//				
//			});
//		}
		this.holder = holder;
		this.jLabelCreated.setText(DateFormat.getDateTimeInstance().format( document.getCreated()));
		this.jURILabel.setText(document.getUniqueURI());
		//this.jPanelHeading.validate();
		
		this.itemspanel = new ItemsPanel( document, this);
		//itemspanel.setItemToolbars(this.getHolder().getItemToolbars());
		
		this.jPanelItems.add(this.itemspanel);
		//this.itemspanel.updateItems();
		//this.itemspanel.updatePanels();
		//this.jPanelItems.revalidate();
		this.setOpaque(true);
		//this.validate();
		//this.setForeground(Color.WHITE);
		//this.setBackground(Color.YELLOW);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
		
		this.jLabel = new JLabel();
		this.jLabel.setText("created: ");
	//	this.jLabel.setFont(Linnk.smallFont);
		this.jLabel.setOpaque(false);
		this.setSize(346, 162);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		this.setAlignmentY(Component.TOP_ALIGNMENT);
		this.add(this.getJMyPanelHeading(), null);
		this.add(this.getJMyPanelItems(), null);
		//this.add(this.getJMyPanelFooter(), null);
	}

	/**
	 * This method initializes jPanelFooter	
	 * 	
	 * @return javax.swing.JMyPanel	
	 */
	private JMyPanel getJMyPanelFooter() {
		if (this.jPanelFooter == null) {
			
			//this.jLabelCreated.setText("JLabel");
			this.jPanelFooter = new JMyPanel();
			this.jPanelFooter.setLayout(new BoxLayout(this.jPanelFooter, BoxLayout.X_AXIS));
			this.jPanelFooter.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			this.jPanelFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.jPanelFooter.add(this.jURILabel, null);
			this.jPanelFooter.setBackground(this.getBackground());
		}
		return this.jPanelFooter;
	}
	
	/**
	 * This method initializes jPanelHeading	
	 * 	
	 * @return javax.swing.JMyPanel	
	 */
	private JMyPanel getJMyPanelHeading() {
		if (this.jPanelHeading == null) {
			this.jLabelCreated = new JLabel();
			
			this.jURILabel = new JLabel();
			//this.jLabelCreated.setText("JLabel");
			this.jPanelHeading = new JMyPanel();
			this.jPanelHeading.setLayout(new BoxLayout(this.getJMyPanelHeading(), BoxLayout.X_AXIS));
			this.jPanelHeading.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			this.jPanelHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.jPanelHeading.add(this.jLabel, null);
			this.jPanelHeading.add(this.jLabelCreated, null);
			this.jPanelHeading.add(new JLabel(" "));
			this.jPanelHeading.add(this.jURILabel, null);
			this.jPanelHeading.setBackground(this.getBackground());
		}
		return this.jPanelHeading;
	}

	/**
	 * This method initializes jPanelItems	
	 * 	
	 * @return javax.swing.JMyPanel	
	 */
	private JMyPanel getJMyPanelItems() {
		if (this.jPanelItems == null) {
			this.jPanelItems = new JMyPanel();
			this.jPanelItems.setLayout(new BoxLayout(this.getJMyPanelItems(), BoxLayout.Y_AXIS));
			this.jPanelItems.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return this.jPanelItems;
	}

	public NodeDocument getDocument() {
		return this.document;
	}

	public DocumentPanelContainer getHolder() {
		return this.holder;
	}

	public void save() {
		if (this.itemspanel !=null) {
			this.itemspanel.save();
		}
		
		Linnk.S.saveDocument(this.getDocument());
	}
	
	public void deselect() {
		if (this.itemspanel !=null) {
			this.itemspanel.deselect();
		}
	}
}  //  @jve:decl-index=0:visual-constraint="128,80"
