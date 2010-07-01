package de.linnk.utils;

import java.util.Map;
import java.util.Vector;

import de.linnk.basispack.v05.LinnkProxy;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.domain.ProxyItem;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.linnk.streaming.DocumentLoader;
import de.linnk.transform.ItemTransformation;
import de.mxro.filesystem.File;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;

public class Utils {
	
	public static final void setDocumentForItems(Document doc) {
		for (final Item i : doc.getItems()) {
			i.setDocument(doc);
		}
	}
	
	public static final void removeDocumentForItems(Document doc) {
		for (final Item i : doc.getItems()) {
			i.setDocument(null);
		}
	}
	
	
	
	
	
	
	
	public static boolean equals(Object a, Object b) {
		if (a == b) return true;
		if (a == null || b == null) return false;
		return a.equals(b);
	}
	
	
	
	private static Item replace(ProxyItem i, Item toReplace, Item newItem) {
		if (i.getItem() == toReplace) {
			i.setItem(newItem);
			
			return i;
		}
		
		return replace((ProxyItem) i.getItem(), toReplace, newItem);
	}
	
	public static Item replace(Document doc, Item toReplace, Item newItem) {
		for (final Item i : doc.getItems()) {
			
 			if (i.hasItem(toReplace)) {
				if (i == toReplace)
					return newItem;
				
				if (!(i instanceof ProxyItem)) {
					de.mxro.utils.log.UserError.singelton.log("linnk.Utils replace: Could not find Item "+toReplace.getId(), UserError.Priority.HIGH);
					return null;
					
				}
				
				return replace((ProxyItem) i, toReplace, newItem);
			}
		}
		de.mxro.utils.log.UserError.singelton.log("linnk.Utils replace: Could not find Item "+toReplace.getId(), UserError.Priority.HIGH);
		return null;
	}
	
	private static NodeDocument getRootDocument(NodeDocument doc, int counter) {
		if (counter > 100) {
			UserError.singelton.log(Utils.class, "getRootDocument: Deeper than 100 documents!", UserError.Priority.HIGH);
			return null;
		}
		if (doc.getOwnerLink() == null)
			return doc;
		File ownerFile = doc.getFolder().getFile(URIImpl.create(doc.getOwnerLink()));
		if (ownerFile == null) return null;
		return getRootDocument(DocumentLoader.singelton.loadFromFile(ownerFile), counter+1);
	}
	
	public static NodeDocument getRootDocument(NodeDocument doc) {
		final NodeDocument res = getRootDocument(doc, 1);
		if (res == null) {
			UserError.singelton.log("linnk Utils: Document has no root: "+doc.getFilename(), UserError.Priority.HIGH);
		}
		return res;
	}
	
	/**
	 * specify itemsPanel if you want the changes to be displayed ...
	 * 
	 * @param i
	 * @param itemsPanel
	 * @param transformation
	 * @return
	 */
	public static boolean applyItemTransformation(Item i, ItemsPanel itemsPanel, ItemTransformation transformation) {
		boolean res=true;
		if (transformation.accept(i)) {
			final Item newItem = transformation.doOnItem(i);
			if (newItem == null)
				return false;
			final Item newRootItem = replace(i.getDocument(), i, newItem);
			final Item oldRootItem = i.getDocument().getRootItem(i);
			final ItemChange change = ItemChange.newModifyItem(newRootItem, oldRootItem, LinnkFatClient.currentUser, ItemChange.Type.IMPLICIT);
			if (itemsPanel == null) {
				res = i.getDocument().doChange(change);
			} else {
				res = itemsPanel.doChange(change);
			}
		}
		if (i instanceof ProxyItem)
			return applyItemTransformation(((ProxyItem) i).getItem(), transformation);
		return res;
	}
	
	public static boolean applyItemTransformation(Item i, ItemTransformation transformation) {
		return applyItemTransformation(i, null, transformation);
	}
	
	public static boolean applyItemTransfomration(Document doc, ItemTransformation transformation) {
		for (final Item i : doc.getItems()) {
			if (!applyItemTransformation(i, transformation))
				return false;
		}
		return true;
		
	}
	 
	protected static boolean getAllDocumentsWorkhorse(NodeDocument root, Vector<String> processedDocs)  {
		if (root.getFile() == null) return false;
		
		if (processedDocs.contains(root.getFile().getURI().toString()))
			return true;
		processedDocs.add(root.getFile().getURI().toString());
		for (final Item i : root.getItems()) {
			if (i instanceof LinnkProxy) {
				final LinnkProxy lp = (LinnkProxy) i;
				final File fso = root.getFolder().getFile(lp.getLink().toURI());
				if (fso != null) {
					
					final NodeDocument child = DocumentLoader.singelton.loadFromFile( fso );
					if (child != null) {
						if (!getAllDocumentsWorkhorse(child, processedDocs)) {
							if (child.getFile() != null) {
								UserError.singelton.log(Utils.class, "getAllDocuments: Error while processing: "+child.getFile().getURI(), UserError.Priority.NORMAL);
							} else {
								UserError.singelton.log(Utils.class, "getAllDocuments: Error while processing: "+child.getUniqueURI(), UserError.Priority.NORMAL);
							}
						}
					} else {
						UserError.singelton.log(Utils.class, "getAllDocuments: Could not load document "+fso.getURI(), UserError.Priority.NORMAL);
					}
					
				} else {
					UserError.singelton.log(Utils.class, "getAllDocuments: LinnkProxy points to nonexistent file. in document: "+root.getFile().getURI()+" to "+lp.getLink().link, UserError.Priority.NORMAL);
				}
			}
		}
		return true;
	}
	
	public static Vector<URI> getAllDocuments(NodeDocument root)  {
		final Vector<String> processedDocs = new Vector<String>();
		if (!getAllDocumentsWorkhorse(root, processedDocs)) {
			UserError.singelton.log("Utils.getAllDocuments failed on "+root.getFile().getURI(), UserError.Priority.HIGH);
		}
		final Vector<URI> res = new Vector<URI>();
		for (final String s: processedDocs) {
			res.add(URIImpl.create(s));
		}
		return res;
	}
	
	public static boolean getLinkedCount(NodeDocument root, Vector<String> processedDocs, Map<URI, Integer> linkedTimes) {
		if (processedDocs.contains(root.getFile().getURI().toString()))
			return true;
		processedDocs.add(root.getFile().getURI().toString());
		//System.out.println("processed: "+root.getFile().getURI());
		//System.out.println("processedDocs: "+processedDocs);
		for (final Item i : root.getItems()) {
			if (i instanceof LinnkProxy) {
				final LinnkProxy lp = (LinnkProxy) i;
				final File fso = root.getFolder().getFile(lp.getLink().toURI());
				if (fso != null) {
					if (linkedTimes.containsKey(fso.getURI())) {
						linkedTimes.put(fso.getURI(), linkedTimes.get(fso.getURI())+1);
					}
					final NodeDocument child = DocumentLoader.singelton.loadFromFile( fso );
					if (child != null) {
						if (!getLinkedCount(child, processedDocs, linkedTimes)) {
							UserError.singelton.log(Utils.class, "getLinkedCount: Error while processing: "+child.getFile().getURI(), UserError.Priority.NORMAL); 
						}
					} else {
						UserError.singelton.log(Utils.class, "getLinkedCount: Could not load document "+fso.getURI(), UserError.Priority.NORMAL);
					}
					
				} else {
					UserError.singelton.log(Utils.class, "getLinkedCount: LinnkProxy points to nonexistent file. in document: "+root.getFile().getURI()+" to "+lp.getLink().link, UserError.Priority.NORMAL);
				}
			}
		}
		return true;
	}
}
