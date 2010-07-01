package de.linnk.maintenance;

import java.util.Vector;

import de.linnk.ExtendedItemBuilder;
import de.linnk.Linnk;
import de.linnk.basispack.v05.DocumentProxy;
import de.linnk.basispack.v05.LinnkProxy;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.batch.Batch;
import de.linnk.batch.SaveAll;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.domain.ProxyItem;
import de.linnk.domain.SimpleLink;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.gwt.LinnkGWTUtils;
import de.linnk.streaming.DocumentLoader;
import de.linnk.streaming.views.View;
import de.linnk.utils.Utils;
import de.mxro.filesystem.File;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;

public class Maintenance {
	
	public static long getSize(java.io.File dir) {
		long res=0;
		if (!dir.isDirectory()) return dir.length();
		
		for (final java.io.File f: dir.listFiles()) {
			if (f.isDirectory()) {
				res = res + getSize(f);
			} else {
				res = res + f.length();
			}
				
		}
		return res;
	}
	
	@Deprecated
	public static void getEmptyFiles(java.io.File root, Vector<String> emptyFiles) {
		if (!root.isDirectory()) {
			if (root.length() == 0) {
				emptyFiles.add(root.getPath());
				System.out.println(root.getPath());
				//root.delete();
			}
			return;
		}
		for (final java.io.File f: root.listFiles()) {
			if (f.getName().contains(".graffle") || f.getName().contains(".pages") || f.getName().contains(".key") || f.getName().equals(".DS_Store") ||
					f.getName().contains(".indd") || f.getName().contains(".rwtheme")) {
				if (f.isDirectory() && getSize(f) == 0) {
					emptyFiles.add(f.getPath());
					
					System.out.println(f.getPath());
				}
				continue;
			}
			getEmptyFiles(f, emptyFiles);
		}
	}
	
	public static boolean saveAll(File file, View view) {
		final NodeDocument doc = DocumentLoader.singelton.loadFromFile(file);
		final Vector<URI> docs = de.linnk.utils.Utils.getAllDocuments(doc);
		return new Batch().doForAllDocuments(doc.getFolder(), docs,new SaveAll(view), LinnkFatClient.application.getProgress()) == null;
		
	}
	
	/*public static boolean checkDocumentProxies(File file) {
		Document root = DocumentLoader.singelton.loadFromFile(file);
		if (root == null) return false;
		Vector<String> processedDocs = new Vector<String>();
		HashMap<URI, Integer> linkedTimes = new HashMap<URI, Integer>();
		if (!de.linnk.utils.Utils.getLinkedCount(root, processedDocs, linkedTimes)) {
			return false;
		}
		UserError.singelton.log(Maintenance.class, "LinkedCount found documents: "+processedDocs.size(), UserError.Priority.INFORMATION);
		
		Vector<URI> processedDocs2 = new Vector<URI>();
		new Batch().doForAllChildren(root, new TransformationAction(new Transformation(Transformation.NONE) {

			@Override
			public Document transform(Document doc) {
				doc.nukeVersions();
				doc.touch();
				return doc;
			}
			
		}), DocumentFilter.ACCEPT_NONE, processedDocs2);
		
		UserError.singelton.log(Maintenance.class, "Batch found documents: "+processedDocs2.size(), UserError.Priority.INFORMATION);
		return processedDocs.size() == processedDocs2.size();
		//throw new NotYetSupportedException();
		
	}*/
	
	public static boolean checkDocumentProxiesWithOwnerItems(File file, View view) {
		final NodeDocument root = DocumentLoader.singelton.loadFromFile(file);
		//System.out.println("processing "+file.getURI());
		
		
		if (root == null) {
			UserError.singelton.log(Maintenance.class, "checkDocumentProxiesWithOwnerItems: Could not laad document: "+file.getURI(), UserError.Priority.NORMAL);
			return false;
		}
		for (final Item item : root.getItems()) {
			//Vector<ProxyItem> v = (Vector<ProxyItem>) ;
			//v.addAll(item.getProxies(DocumentProxy.class));
				
			for (final ProxyItem i : LinnkGWTUtils.getProxies(LinnkProxy.class, item)) {
				
				final NodeDocument linked = DocumentLoader.singelton.loadFromFile(root.getFolder().getFile(((LinnkProxy) i).getLink().toURI()));
				if (linked == null) {
					UserError.singelton.log(Maintenance.class, "checkDocumentProxiesWithOwnerItems: Could not laad document: "+root.getFolder().getFile(((LinnkProxy) i).getLink().toURI()), UserError.Priority.NORMAL);
				} else {
					final SimpleLink owner = new SimpleLink(linked.getOwnerLink());
					boolean makeDP=false;
					if (owner == null) {
						linked.setOwner(LinnkFatClient.currentUser, linked.getFolder().relativizeURI(root.getFile().getURI()).toString());
						Linnk.S.saveDocument(linked);
						
						makeDP = true;
					} else {
						//System.out.println(linked.getFolder().getFile(owner.toURI()).getURI().toString());
						//System.out.println(file.getURI().toString());
						if (linked.getFolder().getFile(owner.toURI()).getURI().toString().equals(file.getURI().toString())) {
							if (!(i instanceof DocumentProxy)) {
								makeDP = true;
							}
						}
					}
					
					if (makeDP) {
						final ExtendedItemBuilder ib = new ExtendedItemBuilder(root, LinnkFatClient.currentUser);
						final Item newItem = Utils.replace(root, i, ib.newDocumentProxy(i.getItem(), ((LinnkProxy) i).getLink(), false));
						final ItemChange ic = ItemChange.newModifyItem(newItem, item, LinnkFatClient.currentUser);
						if (!root.doChange(ic)) {
							UserError.singelton.log(Maintenance.class, "checkDocumentProxiesWithOwnerItems: Error updating document: "+root.getFolder().getFile(((LinnkProxy) i).getLink().toURI()), UserError.Priority.NORMAL);
						} else {
							UserError.singelton.log(Maintenance.class, "checkDocumentProxiesWithOwnerItems: Created documentproxy in "+root.getFile().getURI()+" to "+((LinnkProxy) i).getLink().toURI(), UserError.Priority.INFORMATION);
							
						}
					}
				}
				
			}
		}
		Linnk.S.saveDocument(root, root.getFolder(), view);
		
		for (final String uristr : root.getChildNodes()) {
			URI uri = URIImpl.create(uristr);
			final File childFile = root.getFolder().getFile(uri);
			if (childFile != null)
				checkDocumentProxiesWithOwnerItems(childFile, view);
		}
		return true;
	}
}
