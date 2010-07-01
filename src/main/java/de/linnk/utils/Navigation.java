package de.linnk.utils;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.fatclient.document.DocumentPanelContainer;
import de.linnk.streaming.DocumentLoader;
import de.mxro.filesystem.File;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;

public class Navigation extends de.mxro.utils.history.History<File> {
	private final DocumentPanelContainer container;
	
	public Navigation(final DocumentPanelContainer container) {
		super();
		this.container = container;
	}

	private boolean load(File file, boolean noHistory) {
		
		NodeDocument doc = DocumentLoader.singelton.loadFromFile(file);
		if (doc == null) {
			UserError.singelton.log(this, "doc could not be loaded '"+file.getURI()+"'", UserError.Priority.HIGH);
			return false;
		}
		this.container.showDocument( doc, noHistory);
		return true;
	}
	
	public void performBack() {
		
		if (this.backPossible()) {
			load(this.goBack(), true);
		}
		
	}
	
	public void performForward() {
		
		if (this.forwardPossible()) {
			load(this.goForward(), true);
		}
		
	}

	public boolean levelUpPossible() {
		if (this.container.getDocumentPanel() == null || 
				this.container.getDocumentPanel().getDocument() == null)
			return false;
		return this.container.getDocumentPanel().getDocument().getOwnerLink() != null;
	}
	
	public void perfomLevelUp() {
		if (!this.levelUpPossible())
			return;
		load(this.container.getDocumentPanel().getDocument().getFolder().getFile(
				URIImpl.create(this.container.getDocumentPanel().getDocument().getOwnerLink())), false);
		
		
	}
	
	
}
