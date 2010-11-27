package de.linnk.batch;

import java.util.Vector;

import de.linnk.Linnk;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.documentfilter.DocumentFilter;
import de.linnk.domain.Document;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.streaming.views.View;
import de.linnk.transform.RemoveAllItems;
import de.linnk.transform.Transformation;
import de.mxro.filesystem.Folder;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.Utils;
import de.mxro.utils.distributedtree.ChangedLink;
import de.mxro.utils.log.UserError;

public class Publish extends Action {

	private final View view;
	//private final boolean deleteXML;
	private final Document root;
	private final Folder rootFolder;
	private final Vector<URI> writtenDocuments;
	private final Vector<ChangedLink> changedLinks;
	
	@Override
	public String doOnDocument(Document doc) {
		doc.touch();
		// find out which directory to use
		URI docFolder = ((NodeDocument) this.root).getFolder().relativizeURI(((NodeDocument) doc).getFolder().getURI());
		
		if (docFolder.toString().length() > 1) {
			if (docFolder.toString().charAt(0) == '.' && docFolder.toString().charAt(1) == '.') {
				docFolder = URIImpl.create(this.rootFolder.getUniqueFileName(((NodeDocument) doc).getFolder().getName())+"/");
			}
		}
		
		Folder pubFolder;
		
		if (doc == this.root) {
			pubFolder = this.rootFolder;
		} else {
			pubFolder = this.rootFolder.forceFolder(docFolder);
		}
		
		if (pubFolder == null) {
			UserError.singelton.log("batch.Publish: Publishing folder could not be created: "+docFolder, UserError.Priority.HIGH);
			return "batch.Publish: Publishing folder could not be created";
		}
		

		if (doc != this.root) {
			
			final URI newLink = this.rootFolder.resolveURI(docFolder);
			final URI oldLink = URIImpl.create(Utils.assertAtEnd(this.rootFolder.resolveURI(((NodeDocument) this.root).getFolder().relativizeURI(((NodeDocument) doc).getFolder().getURI())).toString(), '/'));
			if (!oldLink.getPath().equals(newLink.getPath())) {
				
				this.changedLinks.add(new ChangedLink(oldLink.toString(), newLink.toString()));
				doc.setOwner(LinnkFatClient.currentUser,  "../"+mx.gwtutils.MxroGWTUtils.lastElement(doc.getOwnerLink(), "/"));
			}
			
		}
		
		//doc.setFolder(pubFolder);
		// Apply transformation for not publishable documents
		if (DocumentFilter.NOT_PUBLISHABLE.acceptDocument(doc)) {
			new RemoveAllItems(Transformation.NONE).transform(doc);
		}
		
		if (Linnk.S.saveDocument(doc, pubFolder, this.view) == null) {
			UserError.singelton.log("batch.Publish: Could not save document: "+doc.getName(), UserError.Priority.HIGH);
			return "Could not save document: "+doc.getName()+"\n";
		}
		
		this.writtenDocuments.add(pubFolder.getFile(URIImpl.create(doc.getFilename())).getURI());
		
		
		//if (this.deleteXML) {
		//	final String docFileName = doc.getFile().getName();
		//	pubFolder.deleteObject(docFileName);
		//	pubFolder.deleteObject(Utils.removeExtension(docFileName)+de.linnk.application.Linnk.xslExtension);
		//}
		
		return null;
	}

	public Publish(final View view, final Document root, final Folder rootFolder, final Vector<URI> writtenDocuments, final Vector<ChangedLink> changedLinks) {
		super();
		this.view = view;
		//this.deleteXML = deleteXML;
		this.root = root;
		this.rootFolder = rootFolder;
		this.writtenDocuments = writtenDocuments;
		this.changedLinks = changedLinks;
	}

	

	

}
