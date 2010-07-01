package de.linnk.utils.copy;

import java.io.IOException;
import java.util.Vector;

import de.linnk.ExtendedItemBuilder;
import de.linnk.Linnk;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.batch.Batch;
import de.linnk.batch.TransformationAction;
import de.linnk.documentfilter.DocumentFilter;
import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.extpack.v03.HyperlinkProxy;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.streaming.DocumentLoader;
import de.linnk.streaming.views.PlainXMLView;
import de.linnk.streaming.views.View;
import de.linnk.transform.RemoveAllItems;
import de.linnk.transform.Transformation;
import de.linnk.transform.UpdateLinksForLinkingProxies;
import de.linnk.utils.Utils;
import de.mxro.filesystem.File;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.filesystem.Folder;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.distributedtree.ChangedLink;
import de.mxro.utils.log.UserError;
import de.mxro.utils.progress.Progress;

public abstract class Copy {

	/**
	 * Copy file objects that hyperlinks are pointing to and update the Hyperlinkproxies
	 * 
	 * @param oldRoot
	 * @param newRoot
	 * @param doc
	 * @param changedLinks
	 */
	private static void checkHyperlinkProxies(NodeDocument oldRoot,
			NodeDocument newRoot,
			NodeDocument doc,
			Vector<ChangedLink> changedLinks) {
		for (final Item i : doc.getItems(HyperlinkProxy.class)) {
			if (i instanceof HyperlinkProxy) {
				final URI oldLink = URIImpl.create(((HyperlinkProxy) i).getHyperlink());
				if (oldLink.isAbsolute()) continue;
				
				if (!((HyperlinkProxy) i).isLinkOwner()) continue;
			//	URI oldAbsolute = linkedFile.getURI();
			//	URI newAbsolute = doc.getFolder().resolveURI(URI.create(linkedFile.getName()));
			//	URI oldRelative = oldRoot.getFolder().relativizeURI(oldAbsolute);
			//	URI newRelative = newRoot.getFolder().relativizeURI(newAbsolute);
			//	if (oldRelative.getPath().equals(newRelative.getPath())) continue;
			//	changedLinks.add(new Publish.ChangedLink(oldRelative, newRelative));
				
				// object that hyperlink is pointing to
				final FileSystemObject fso = oldRoot.getFolder().get(oldLink);
				if (fso == null) {
					UserError.singelton.log("Utils.checkHyperlinkProxies: invalid linked file: '"+oldRoot.getFolder().resolveURI(oldLink)+"'", UserError.Priority.NORMAL);
					continue;
				}
				
				final FileSystemObject copied;
				try {
					copied = doc.getFolder().importObject(fso);
				} catch (IOException e) {
					UserError.singelton.log("Utils.checkHyperlinkProxies: file could not be copied: '"+oldRoot.getFolder().resolveURI(oldLink)+"'", UserError.Priority.NORMAL);
					UserError.singelton.log(e);
					continue;
				}
				
					
				final ExtendedItemBuilder ib = new ExtendedItemBuilder(doc, LinnkFatClient.currentUser);
				final Item newHp= ib.newHyperlinkProxy(((HyperlinkProxy) i).getItem(), copied.getName());
				final ItemChange ic = ItemChange.newModifyItem(newHp, i, LinnkFatClient.currentUser);
				doc.doChange(ic);
			}
		}
	}

	public static Document copy(NodeDocument root, 
			Folder destination, 
			View view, 
			DocumentFilter skipDocuments, 
			DocumentFilter dontcopy, 
			Progress progress) {
		final NodeDocument oldRoot = root;
		final NodeDocument newRoot = DocumentLoader.singelton.loadFromFile(root.getFile());
		final NodeDocument doc = DocumentLoader.singelton.loadFromFile(root.getFile());
		newRoot.setFolder(destination);
		newRoot.setFilename(destination.forceFile(oldRoot.getFilename()).getName());
		
		final NodeDocument realRootDocument = de.linnk.utils.Utils.getRootDocument(root);
		
		//final Vector<ChangedLink> changedLinks = new Vector<ChangedLink>();
		//final Vector<ChangedLink> newDocuments = new Vector<ChangedLink>();
		//final Vector<URI> copiedDocuments= new Vector<URI>();
		
		final CopyHelper helper = new CopyHelper(oldRoot, newRoot, realRootDocument, new PlainXMLView());
		helper.setSkipDocuments(skipDocuments);
		helper.setExcludeChildrenOf(dontcopy);
		helper.setProgress(progress);
		
		//if (!copyWorkhorse(oldRoot, newRoot, doc, realRootDocument, new XMLView(), destination, skipDocuments, excludeChildrenOf, changedLinks, newDocuments, copiedDocuments, progress))
		if (!helper.copy(doc, destination))	{
			//return null;
		}
		
		
		
		progress.setMaximum(helper.getChangedLinks().size());
		//if (changedLinks.size() > 0) {
		
		
		final String res=new Batch().doForAllDocuments(destination,
					helper.getCopiedDocuments(), 
					new TransformationAction(view, new UpdateLinksForLinkingProxies(helper.getChangedLinks(), destination, realRootDocument.getFolder(), helper.getNewDocuments(), Transformation.NONE)),
					//new TransformationAction(view,  Transformation.NONE),
					progress);
		//}
		if (res != null) {
			UserError.singelton.log(Utils.class, "copy: UpdateLinksForProxies failed! "+res, UserError.Priority.HIGH);
		}
		return newRoot;
	}

	private static boolean copyWorkhorse(NodeDocument oldRoot,
			NodeDocument newRoot,
			NodeDocument doc, 
			NodeDocument realRootDocument,
			View view, 
			Folder destination, 
			DocumentFilter skipDocuments,
			DocumentFilter dontcopy, 
			Vector<ChangedLink> changedLinks,
			Vector<ChangedLink> newDocuments,
			Vector<URI> copiedDocuments,
			Progress progress) {
		final Vector<File> childrenFiles = new Vector<File>();
		progress.setMessage("Copy: "+doc.getName());
		if (!dontcopy.acceptDocument(doc)) {
			for (final String ustr: doc.getChildNodes()) {
				URI u = URIImpl.create(ustr);
				final File file = doc.getFolder().getFile(u);
				if (file == null) {
					UserError.singelton.log(Utils.class, "copy: Could not find file: "+u.toString()+" in "+doc.getFolder().getURI(), UserError.Priority.NORMAL);
				} else {
					childrenFiles.add(file);
				}
			}
		}
		
		// empty documents that are not to be copied
		if (dontcopy.acceptDocument(doc)) {
			new RemoveAllItems(Transformation.NONE).transform(doc);
		}
		
		if (!skipDocuments.acceptDocument(doc)) {
			final URI oldDocURI = doc.getFile().getURI();
			if (Linnk.S.saveDocument(doc, destination, view) == null) {
				UserError.singelton.log(Utils.class, "copy: Could not save document: "+doc.getFilename(), UserError.Priority.HIGH);
				return false;
			}
			
		    // this is a hack !!!
			// TODO Hack!!!
			final Folder localDestination = FileSystemObject.newLocalRootFolder(destination.getURI());
		    doc.setFolder(localDestination);
			doc.setFilename(localDestination.forceFile(doc.getFilename()).getName());
			
			final URI newDocURI = doc.getFile().getURI();
			newDocuments.add(new ChangedLink(oldDocURI.toString(), newDocURI.toString()));
			
			if (doc.getOwnerLink() != null && !doc.getFile().getURI().getPath().equals(newRoot.getFile().getURI().getPath())) {
				doc.setOwner(LinnkFatClient.currentUser, "../"+de.mxro.utils.Utils.lastElement(doc.getOwnerLink(), "/"));
			} else {
				doc.setOwner(LinnkFatClient.currentUser, null);
			}
			doc.touch();
			Linnk.S.saveDocument(doc, doc.getFolder(), view);
			
			checkHyperlinkProxies(oldRoot, newRoot, doc, changedLinks);
			Linnk.S.saveDocument(doc, doc.getFolder(), view);
			
			copiedDocuments.add(doc.getFile().getURI());
		} else {
			UserError.singelton.log("de.linnk.Utisl.copyWorkhorse skiped document: "+doc.getFile().getURI());
		}
		
		
		for (final File file : childrenFiles) {
			if (LinnkFatClient.application != null) {
				if (LinnkFatClient.application.getProgress().isAborting())
					return false;
			}
			final NodeDocument child = DocumentLoader.singelton.loadFromFile(file);
			if (child == null) {
				UserError.singelton.log(Utils.class, "copy: Could open document: "+file.getURI(), UserError.Priority.NORMAL);
			} else {
				String newFolderName = child.getFolder().getName();
				if (newFolderName.equals("")) {
					newFolderName = de.mxro.utils.Utils.lastElement(child.getFolder().getURI().toString(), "/");
				}
				final Folder childFolder = destination.forceFolder(newFolderName);
				if (!skipDocuments.acceptDocument(child)) {
					
	//				System.out.println("childFolder: "+childFolder.getURI());
					final File childFile = childFolder.forceFile(child.getFilename());
				
				
					if (childFile == null) {
						UserError.singelton.log(Utils.class, "copyWorkhorse: Could not create file: "+child.getFilename()+" in "+childFolder.getURI(), UserError.Priority.HIGH);
						continue;
					}
					
	
					final URI oldURI = realRootDocument.getFolder().relativizeURI(child.getFile().getURI());
					final URI newURI = newRoot.getFolder().relativizeURI(childFile.getURI());
					
					if (!oldURI.getPath().equals(newURI.getPath())) {
						changedLinks.add(new ChangedLink(oldURI.toString(), newURI.toString()) );
					
					}
				}
				
				
				if (!copyWorkhorse(oldRoot, newRoot, child, realRootDocument, view, childFolder, skipDocuments, dontcopy, changedLinks, newDocuments, copiedDocuments, progress)) {
					UserError.singelton.log(Utils.class, "copyWorkhorse: could not copy document: "+child.getFilename(), UserError.Priority.HIGH);
				}
			}
		}
		
		return true;
	}

}
