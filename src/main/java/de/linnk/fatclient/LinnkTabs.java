package de.linnk.fatclient;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.DocumentPanelContainer;

public class LinnkTabs extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTabbedPane jTabbedPane = null;


	private final WindowInterface windowInterface;
	
	
	
	/**
	 * This is the default constructor
	 */
	public LinnkTabs(WindowInterface windowInterface) {
		super();
		this.windowInterface = windowInterface;
		this.initialize();
		
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(371, 232);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(this.getJTabbedPane(), null);
	}

	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	public JTabbedPane getJTabbedPane() {
		if (this.jTabbedPane == null) {
			this.jTabbedPane = new JTabbedPane();
			this.jTabbedPane.addChangeListener(new ChangeListener() {
		        // This method is called whenever the selected tab changes
		        public void stateChanged(ChangeEvent evt) {
		            LinnkTabs.this.deselectAll();
		            final Component comp = LinnkTabs.this.getJTabbedPane().getSelectedComponent();
		            if (comp instanceof LinnkTab) {
		            	final DocumentPanelContainer holder = ((LinnkTab) comp).getHolder();
		            	if (holder != null) {
		            		if (holder.getDocumentPanel() != null)
		            			holder.getDocumentPanel().select();
		            	}
		            }
		        	LinnkTabs.this.getWindowInterface().check();
		        	
		        }
		    });
		}
		return this.jTabbedPane;
	}
	
	private void removeTab(Component comp) {
		if (comp instanceof LinnkTab ) {
			final LinnkTab tab = (LinnkTab) comp;
			tab.getHolder().removePanel();
		}
		this.getJTabbedPane().remove(comp);
	}
	
	public void removeActiveTab() {
		final Component comp = this.getJTabbedPane().getSelectedComponent();
		this.removeTab(comp);
		
	}
	
	public void deselectAll() {
		for (final Component comp: this.getJTabbedPane().getComponents()) {
			if (comp instanceof LinnkTab) {
				((LinnkTab) comp).getHolder().deselect();
			}
		}
	}
	
	
	
	public final WindowInterface getWindowInterface() {
		return this.windowInterface;
	}

	public void addTab(NodeDocument doc) {
		final DocumentPanelContainer holder = new DocumentPanelContainer(this.getWindowInterface());
		final LinnkTab tab = new LinnkTab(holder);
		this.getJTabbedPane().addTab(doc.getName().substring(0, Math.min(20, doc.getName().length())), tab);
		
		
		holder.showDocument(doc);
		
		this.getJTabbedPane().setSelectedComponent(tab);
	}
	
	public LinnkTab addTab(java.io.File file) {
		final DocumentPanelContainer holder = new DocumentPanelContainer(this.getWindowInterface());
		final LinnkTab tab = new LinnkTab(holder);
		this.getJTabbedPane().addTab(file.getName(), tab);
	
		
		if (holder.showDocument(file) == null)
			return null;
		LinnkFatClient.application.checkUserRoot(holder.getDocumentPanel().getDocument());
		this.getJTabbedPane().setSelectedComponent(tab);
		return tab;
	}
	

}  //  @jve:decl-index=0:visual-constraint="10,10"
