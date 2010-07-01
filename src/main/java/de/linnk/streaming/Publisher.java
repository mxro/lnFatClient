package de.linnk.streaming;

import de.linnk.Linnk;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.documentfilter.DocumentFilter;
import de.linnk.documentfilter.IsUnchanged;
import de.linnk.domain.Document;
import de.linnk.extpack.v03.PublishView;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.streaming.views.View;
import de.linnk.streaming.views.WithoutExtensionView;
import de.mxro.filesystem.Folder;
import de.mxro.utils.log.UserError;

public class Publisher {
	private final PublishView view;
	private final boolean deleteXML;
	private final Folder destination;
	private transient NodeDocument document;
	private IsUnchanged isUnchanged;

	
	public void setDocument(NodeDocument document) {
		this.document = document;
	}

	public boolean publish() {
		// TODO Hack
		View realPublishView = new WithoutExtensionView(
				                 new WithoutExtensionView(
				                   this.view.getXmlView(), ".xsl"),
				            ".xml"
				                 );
		
		if (Linnk.S.saveDocument(this.document) == null) {
			UserError.singelton.log(this, "Publisher: Could not publish document '"+this.document.getUniqueURI()+"'. It could not be saved.", UserError.Priority.HIGH);
			return false;
		}
		de.mxro.utils.log.UserError.singelton.log(this, "Publisher.publish: Tries to publish: "+this.document.getFilename(), UserError.Priority.INFORMATION);
		final NodeDocument workDoc = DocumentLoader.singelton.loadFromFile(this.document.getFile());
		
		if (workDoc == null) {
			UserError.singelton.log("Publisher: Document could not be loaded: "+this.document.getFilename());
			return false;
		}
		workDoc.setOwner(LinnkFatClient.currentUser, null);
		
		de.mxro.utils.log.UserError.singelton.log(this, "publish: Publish to folder: '"+this.destination.getURI().toString()+"'\n" +
				" from document '"+workDoc.getUniqueURI()+"' \n " +
				" with file '"+workDoc.getFile().getURI()+"'", UserError.Priority.INFORMATION);
		
		
		final Document res = de.linnk.utils.copy.Copy.copy(workDoc, this.destination, realPublishView, this.getIsUnchanged(), DocumentFilter.NOT_PUBLISHABLE, LinnkFatClient.application.getProgress());
		
		// currently without function, so cache will not be saved
		//this.destination.clear();
		this.isUnchanged = new IsUnchanged(null, false);
		return res != null;
		
	}

	public Publisher(final PublishView view, final boolean deleteXML, final Folder destination, final NodeDocument document, IsUnchanged isUnchanged) {
		super();
		this.view = view;
		this.deleteXML = deleteXML;
		this.destination = destination;
		this.document = document;
		this.isUnchanged = isUnchanged;
	}

	public boolean isDeleteXML() {
		return this.deleteXML;
	}

	public Folder getDestination() {
		return this.destination;
	}

	public Document getDocument() {
		return this.document;
	}

	public PublishView getView() {
		return this.view;
	}

	public IsUnchanged getIsUnchanged() {
		if (this.isUnchanged == null) {
			this.isUnchanged = new IsUnchanged(null, false);
		}
		return this.isUnchanged;
	}

	
	
	
}
