package de.linnk.transform;

import java.util.Vector;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.SimpleLink;
import de.mxro.filesystem.Folder;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.distributedtree.ChangedLink;
import de.mxro.utils.distributedtree.Linking;
import de.mxro.utils.log.UserError;

public class UpdateLinksForLinkingProxies extends Transformation {

	private final Vector<ChangedLink> changedLinks;
	private final Folder rootFolder;
	private final Folder oldRootFolder;
	private final Vector<ChangedLink> newDocuments;
	
	private static class LinksInItemsTransformation extends ItemTransformation<Item> {

		private final Vector<ChangedLink> changedLinks;
		private final Folder rootFolder;
		private final Folder oldRootFolder;
		private final Vector<ChangedLink> newDocuments;
		
		
		@Override
		public boolean accept(Item i) {
			return i instanceof Linking;
		}

		@Override
		public Item doOnItem(Item item) {
			//System.out.println("itemURI "+ rootFolder.resolveURI(item.getLink().toURI()));
			SimpleLink oldLink=null;
//			if (item instanceof LinnkProxy) { oldLink = ((LinnkProxy) item).getLink(); }
//			if (item instanceof HyperlinkProxy) { 
//				if (!URI.create(((HyperlinkProxy) item).getHyperlink()).isAbsolute()) {
//					oldLink = SimpleLink.fromURI(URI.create(((HyperlinkProxy) item).getHyperlink()));
//				}
//			}
			if (!(item instanceof Linking)) {
				return item;
			}
			
			
				Linking.LinkVector lV = ((Linking<Item>) item).getLinkVector();
				Linking.LinkVector fixedLV = new Linking.LinkVector();
				for (String uristr : lV) {
					URI uri = URIImpl.create(uristr);
					oldLink = SimpleLink.fromURI(uri);
					
					// get old URI for Document
					final int idx = this.newDocuments.indexOf(new ChangedLink(null, item.getDocument().getUniqueURI()));
					if (idx == -1) {
						UserError.singelton.log(this, "Could not find old document uri: "+ ((NodeDocument) item.getDocument()).getFile().getURI(), UserError.Priority.HIGH);
						return item;
					}
					
					final URI URIrelativeOldRoot = URIImpl.create( this.newDocuments.get(idx).oldLink);
					// becomes file:///Users/mx/Desktop/temp/publish/Subconcept1.xml
					
					// oldLink is Concept3/Concept5/Concept5.xml
					final URI resolvedItem = URIrelativeOldRoot.getFolder().resolve(oldLink.toURI());
					// becomes file:/Users/mx/Desktop/temp/publish/Concept3/Concept5/Concept5.xml
					// Should be the same as resolvedOld
					
					URI newURI=uri;
					for (final ChangedLink cl : this.changedLinks) {
						final URI resolvedOld = this.oldRootFolder.resolveURI(URIImpl.create(cl.oldLink));
						// resolvedOld becomes http://www.linnk.de/Subconcept1/Concept3/Concept5/Concept5.xml
						
						
						if (!resolvedOld.getPath().equals(resolvedItem.getPath())) {
							// nothing to fix
							//fixedLV.add(uri);
						} else {
							URI relativizer = ((NodeDocument) item.getDocument()).getFolder().getURI();
							URI relativized = relativizer.relativize(URIImpl.create(cl.newLink));
							newURI = relativized;//item.getDocument().getFolder().relativizeURI(this.rootFolder.resolveURI(cl.newLink));
							
							
							
//							if (item instanceof DocumentProxy)
//								//System.out.println("updated DocumentProxy! new URI: "+newURI);
//								return new ItemBuilder(item.getDocument(), User.currentUser).newDocumentProxy(((DocumentProxy) item).getItem(), SimpleLink.fromURI(newURI), ((DocumentProxy) item).isExpanded());
//							else if (item instanceof LinnkProxy)
//								//System.out.println("updated LinnkProxy!");
//								return new ItemBuilder(item.getDocument(), User.currentUser).newLinnkProxy(((LinnkProxy) item).getItem(), SimpleLink.fromURI(newURI));
//							else if (item instanceof HyperlinkProxy)
//								//System.out.println("updated Hyperlink!");
//								return new ItemBuilder(item.getDocument(), User.currentUser).newHyperlinkProxy(((HyperlinkProxy) item).getItem(), newURI.getPath());
							
						}
						
					}
					fixedLV.add(newURI.toString());
					
				}
				
					
				return ((Linking<Item>) item).newInstanceFromLinkVector(fixedLV);
				
			
			
			
		}

		public LinksInItemsTransformation(final Vector<ChangedLink> changedLinks, Folder rootFolder, Folder oldRootFolder, final Vector<ChangedLink> newDocuments) {
			super();
			this.changedLinks = changedLinks;
			this.rootFolder = rootFolder;
			this.oldRootFolder = oldRootFolder;
			this.newDocuments = newDocuments;
		}
		
		
	}
	
	@Override
	public void applyTransformation(Document doc) {
		if (!de.linnk.utils.Utils.applyItemTransfomration(doc, new LinksInItemsTransformation(this.changedLinks, this.rootFolder, this.oldRootFolder, this.newDocuments))) {
			UserError.singelton.log(this, "transform: could not apply item transformation", UserError.Priority.HIGH);
		}
		
		
	}

	public UpdateLinksForLinkingProxies(final Vector<ChangedLink> changedLinks, final Folder rootFolder, final Folder oldRootFolder,final Vector<ChangedLink> newDocuments, Transformation next) {
		super(next);
		this.changedLinks = changedLinks;
		this.rootFolder = rootFolder;
		this.oldRootFolder = oldRootFolder;
		this.newDocuments = newDocuments;
		
	}

	
	
}
