package de.linnk.fatclient.document;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.iharder.dnd.FileDrop;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;
import de.linnk.fatclient.WindowInterface;
import de.linnk.streaming.DocumentLoader;
import de.linnk.utils.Navigation;
import de.mxro.utils.log.UserError;


public class DocumentPanelContainer extends JPanel {

	private static final long serialVersionUID = 1L;
	protected DocumentPanel panel=null;
	
	protected JComponent fileDropTargetComponent = null;
	protected final WindowInterface windowInterface;
	
	private Navigation navigation;
	
	
	
	public final WindowInterface getWindowInterface() {
		return this.windowInterface;
	}

	

	public JComponent getFileDropTargetComponent() {
		return this.fileDropTargetComponent;
	}

	public void setFileDropTargetComponent(JComponent fileDropTargetComponent) {
		this.fileDropTargetComponent = fileDropTargetComponent;
		new  FileDrop( this, this.windowInterface.getLinnkDocumentActions().getFileDropListener());
	}

	public void removePanel() {
		if (this.panel!=null) {
			this.panel.setVisible(false);
			this.panel.deselect();
			this.revalidate();
			this.remove(this.panel);
			this.panel = null;
		}
	}
	
	
	
	/*public void saveDocument(Link link) {
		panel.save();
		OutputStream os = link.getOutputStream();
		if (os != null) {
			DocumentStreamer.domDocumentStreamer.writeToStream(os, panel.getDocument());
			
		}
	}*/
	
	public DocumentPanel getDocumentPanel() {
		return this.panel;
	}
	
	public final Navigation getNavigation() {
		return this.navigation;
	}

	/**
	 * default is to save as xml view. so it is poosible to view at document with 
	 * any browser and the document can easily be reloaded in the editor
	 * @param link
	 */
	public void saveDocument() {
		if (this.panel != null) {
			this.panel.save();
			
		}
	}
	
	
	
	public void showDocument(NodeDocument doc) {
		this.showDocument(doc, false);
	}
	
	public void showDocument(NodeDocument doc, boolean noHistory) {
		 if (doc == null) {
			 de.mxro.utils.log.UserError.singelton.log("DocumentPanelHolder.showDocument: Document null was passed");
			 return;
		 }
		 
		 this.saveDocument();
		 this.removePanel();
		 //panel.setBounds(0,0,400,400);
		 this.panel = new DocumentPanel(doc, this);
		 this.add(this.panel);
		
		 if (!noHistory) {
			this.navigation.jumpedTo(doc.getFile());
		}
		 this.panel.select();
		 this.getDocumentPanel().getItemspanel().selectFirst();
		 //this.panel.validate();
		 
		 
		 this.revalidate();
		 //setBounds(new Rectangle(0,0,507, 175));
		 //this.validate();
		 this.getWindowInterface().check();
	}
	
//	public void showDocument(Link link) {
//		this.showDocument(link, false);
//	}
//	
//	public void showDocument(Link link, boolean noHistory) {
//		this.saveDocument();
//		this.removePanel();
//		this.showDocument(DocumentLoader.singelton.loadFromLink(link), noHistory);
//		this.getWindowInterface().check();
//		
//	}
	
	public Document showDocument(java.io.File file) {
		de.mxro.utils.log.UserError.singelton.log("DocumentPanelHolder.showDocument: "+file.getAbsolutePath(), UserError.Priority.LOW);
		final NodeDocument doc = (NodeDocument) DocumentLoader.singelton.loadFromJavaFile(file);
		if (doc == null) {
			de.mxro.utils.log.UserError.singelton.log(this, "Could not load document from file: "+file.getAbsolutePath(), UserError.Priority.NORMAL);
			return null;
		}
		this.showDocument(doc);
		return doc;
		
	}
	
	/*public void showDocument(URI uri) {
		showDocument( DocumentLoader.singelton.loadFormFile(uri) );
	}*/
	/**
	 * This is the default constructor
	 */
	public DocumentPanelContainer(WindowInterface windowInterface) {
		super();
		this.initialize();
		this.navigation = new Navigation(this);
		
		this.windowInterface = windowInterface;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setAlignmentY(Component.TOP_ALIGNMENT);
		//this.setOpaque(true);
		//this.setBackground(Color.WHITE);
		//this.setForeground(Color.WHITE);
	}



	

	public void deselect() {
		if (this.getDocumentPanel() != null) {
			this.getDocumentPanel().deselect();
		}
	}

	

}  //  @jve:decl-index=0:visual-constraint="46,119"
