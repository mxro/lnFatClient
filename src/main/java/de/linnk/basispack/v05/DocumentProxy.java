package de.linnk.basispack.v05;


import de.linnk.ExtendedItemBuilder;
import de.linnk.Linnk;
import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.SimpleLink;
import de.linnk.domain.User;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.streaming.DocumentLoader;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;


public class DocumentProxy extends LinnkProxy {
	
	
	public DocumentProxy(User creator, String id, Document document, Item item, SimpleLink link, boolean expanded) {
		super(creator, id, document, item, link, expanded);
	}

	@Override
	public void afterInsert() {
		super.afterInsert();
		final Document doc = DocumentLoader.singelton.loadFromFile(((NodeDocument) this.getDocument()).getFolder().getFile(this.link.toURI()));
			
		if (doc instanceof NodeDocument) {
			
			URI relativizer = ((NodeDocument) doc).getFolder().getURI();
			URI toRelativize = ((NodeDocument) this.getDocument()).getFile().getURI();
			URI newLink = relativizer.relativize(toRelativize);
			((NodeDocument) doc).setOwner(LinnkFatClient.currentUser, newLink.toString());
			Linnk.S.saveDocument(doc);
			
		}
	}

	
	
	@Override
	public LinnkProxy newInstanceFromLinkVector(
			de.mxro.utils.distributedtree.Linking.LinkVector vector) {
		if (!this.getClass().equals(DocumentProxy.class)) {
			UserError.singelton.log(this, "newInstanceFromLinkVector: has to be implemented for class "+this.getClass(), UserError.Priority.HIGH);
			return null;
		}
		
		return (DocumentProxy) new ExtendedItemBuilder(this.getDocument(), LinnkFatClient.currentUser).newDocumentProxy(((LinnkProxy) this).getItem(), SimpleLink.fromURI(URIImpl.create(vector.firstElement())), this.isExpanded());
	}

	@Override
	public boolean isLinkOwner() {
		return true;
	}
	
	
	
}
