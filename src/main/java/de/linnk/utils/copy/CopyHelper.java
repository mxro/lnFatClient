package de.linnk.utils.copy;

import java.io.IOException;
import java.util.Vector;

import de.linnk.ExtendedItemBuilder;
import de.linnk.Linnk;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.documentfilter.DocumentFilter;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.extpack.v03.HyperlinkProxy;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.streaming.DocumentLoader;
import de.linnk.streaming.views.View;
import de.linnk.utils.Utils;
import de.mxro.filesystem.File;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.filesystem.Folder;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.distributedtree.ChangedLink;
import de.mxro.utils.log.UserError;
import de.mxro.utils.progress.Progress;

public class CopyHelper {
	final NodeDocument oldRoot;
	final NodeDocument newRoot;
	final NodeDocument realRootDocument;
	final View view;

	DocumentFilter skipDocuments;
	DocumentFilter excludeChildrenOf;
	Vector<ChangedLink> changedLinks;
	Vector<ChangedLink> newDocuments;
	Vector<URI> copiedDocuments;
	Progress progress;
	
	
	public boolean copy(NodeDocument doc, Folder destination) {
		this.changedLinks = new Vector<ChangedLink>();
		this.newDocuments = new Vector<ChangedLink>();
		this.copiedDocuments = new Vector<URI>();
		return this.copyDocumentAndChildren(doc, destination);
	}
	

	
	private NodeDocument copyDocument(NodeDocument sourceDocument, Folder destination) {
		final URI oldDocURI = sourceDocument.getFile().getURI();
		final NodeDocument newDocument = DocumentLoader.singelton.loadFromFile(sourceDocument.getFile());
		if (Linnk.S.saveDocument(newDocument, destination, view) == null
				) {
			UserError.singelton.log(this, "copyDocument: Could not save document: "+newDocument.getFilename(), UserError.Priority.HIGH);
			return null;
		}
	
		//final Folder localDestination = FileSystemObject.newLocalRootFolder(destination.getURI());
	    newDocument.setFolder(destination);
	    final File newDocFile = destination.forceFile(sourceDocument.getFilename());
	    if (newDocFile == null) {
	    	UserError.singelton.log(this, "copyDocument: Could not create file "+newDocument.getFilename(), UserError.Priority.HIGH);
	    	return null;
	    }
		newDocument.setFilename(newDocFile.getName());
		
		final URI newDocURI = newDocument.getFile().getURI();
		newDocument.setUniqueURI(newDocURI.toString());
		newDocuments.add(new ChangedLink(oldDocURI.toString(), newDocURI.toString()));
		
		if (newDocument.getOwnerLink() != null && !newDocument.getFile().getURI().getPath().equals(newRoot.getFile().getURI().getPath())) {
			newDocument.setOwner(LinnkFatClient.currentUser, "../"+de.mxro.utils.Utils.lastElement(newDocument.getOwnerLink(), "/"));
		} else {
			newDocument.setOwner(LinnkFatClient.currentUser, null);
		}
		newDocument.touch();
		Linnk.S.saveDocument(newDocument, newDocument.getFolder(), view);
		
		copyHyperlinkedFiles(newDocument, sourceDocument);
		Linnk.S.saveDocument(newDocument, newDocument.getFolder(), view);
		
		copiedDocuments.add(newDocument.getFile().getURI());
		return newDocument;
	}
	
	private boolean copyChild(NodeDocument sourceDocument, File sourceFile, NodeDocument newParent, Folder parentFolder) {
		final NodeDocument sourceChildDocument = DocumentLoader.singelton.loadFromFile(sourceFile);
		if (sourceChildDocument == null) {
			UserError.singelton.log(this, "copy: Could open document: "+sourceFile.getURI(), UserError.Priority.NORMAL);
			return false;
		} 
		
			String newFolderName = sourceChildDocument.getFolder().getName();
			if (newFolderName.equals("")) {
				newFolderName = de.mxro.utils.Utils.lastElement(sourceChildDocument.getFolder().getURI().toString(), "/");
			}
			final Folder childFolder = parentFolder.forceFolder(newFolderName);
			if (!skipDocuments.acceptDocument(sourceChildDocument)) {
				
				final File childFile = childFolder.forceFile(sourceChildDocument.getFilename());
			
			
				if (childFile == null) {
					UserError.singelton.log(this, "copy: Could not create file: "+sourceChildDocument.getFilename()+" in "+childFolder.getURI(), UserError.Priority.HIGH);
					return false;
				}
				

				final URI oldURI = sourceDocument.getFolder().getURI().resolve(sourceFile.getURI());
				final URI newURI = newParent.getFolder().getURI().resolve(childFile.getURI());
				
				if (!oldURI.getPath().equals(newURI.getPath())) {
					changedLinks.add(new ChangedLink(oldURI.toString(), newURI.toString()) );
				
				}
			}
			
			
			if (!copyDocumentAndChildren(sourceChildDocument, childFolder)) {
				UserError.singelton.log(this, "copy: could not copy document: "+sourceChildDocument.getFilename(), UserError.Priority.HIGH);
			}
		
		return true;
	}
	
	private Vector<File> getChildrenOf(NodeDocument doc) {
		final Vector<File> childrenFiles = new Vector<File>();
		
		if (!excludeChildrenOf.acceptDocument(doc)) {
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
		return childrenFiles;
	}

	
	private boolean copyDocumentAndChildren(NodeDocument sourceDocument, Folder destination) {
		if (this.getProgress() != null) this.getProgress().setMessage("Copy: "+sourceDocument.getName());
		
		final Vector<File> childrenFiles = getChildrenOf(sourceDocument);
		
		NodeDocument copiedDocument = null;
		if (!skipDocuments.acceptDocument(sourceDocument)) {
			copiedDocument = copyDocument(sourceDocument, destination);
		} else {
			UserError.singelton.log(this, "copy skiped document: "+sourceDocument.getFile().getURI(), UserError.Priority.INFORMATION);
		}
		
		
		for (final File sourceFile : childrenFiles) {
			if (LinnkFatClient.application != null) {
				if (LinnkFatClient.application.getProgress().isAborting())
					return false;
			}
			if (!copyChild(sourceDocument, sourceFile, copiedDocument, destination)) {
				
			}
		}
		
		return true;
	}
	
	/**
	 * Copy file objects that hyperlinks are pointing to and update the Hyperlinkproxies
	 * 
	 *
	 * @param newDocument
	 * 
	 */
	private void copyHyperlinkedFiles(
			NodeDocument newDocument,
			NodeDocument sourceDocument
			) {
		for (final Item i : sourceDocument.getItems(HyperlinkProxy.class)) {
			if (i instanceof HyperlinkProxy) {
				final URI oldLink = URIImpl.create(((HyperlinkProxy) i).getHyperlink());
				URI relativized = sourceDocument.getFolder().getURI().relativize(oldLink);
				if (relativized != null && relativized.isAbsolute()) continue;
				
				
				if (!((HyperlinkProxy) i).isLinkOwner()) continue;
			
				
				// object that hyperlink is pointing to
				final FileSystemObject fso = sourceDocument.getFolder().get(oldLink);
				if (fso == null) {
					UserError.singelton.log("Utils.checkHyperlinkProxies: invalid linked file: '"+oldRoot.getFolder().resolveURI(oldLink)+"'", UserError.Priority.NORMAL);
					continue;
				}
				
				final FileSystemObject copied;
				try {
					copied = newDocument.getFolder().importObject(fso);
				} catch (IOException e) {
					UserError.singelton.log("Utils.checkHyperlinkProxies: file could not be copied: '"+oldRoot.getFolder().resolveURI(oldLink)+"'", UserError.Priority.NORMAL);
					UserError.singelton.log(e);
					continue;
				}
				
					
				final ExtendedItemBuilder ib = new ExtendedItemBuilder(newDocument, LinnkFatClient.currentUser);
				final Item newHp= ib.newHyperlinkProxy(((HyperlinkProxy) i).getItem(), copied.getName());
				final ItemChange ic = ItemChange.newModifyItem(newHp, i, LinnkFatClient.currentUser);
				newDocument.doChange(ic);
			}
		}
	}
	
	
	public CopyHelper(NodeDocument oldRoot, NodeDocument newRoot,
			NodeDocument realRootDocument, View view) {
		super();
		this.oldRoot = oldRoot;
		this.newRoot = newRoot;
		this.realRootDocument = realRootDocument;
		this.view = view;
		
	}



	public DocumentFilter getSkipDocuments() {
		return skipDocuments;
	}



	public void setSkipDocuments(DocumentFilter skipDocuments) {
		this.skipDocuments = skipDocuments;
	}



	public DocumentFilter getExcludeChildrenOf() {
		return excludeChildrenOf;
	}



	public void setExcludeChildrenOf(DocumentFilter excludeChildrenOf) {
		this.excludeChildrenOf = excludeChildrenOf;
	}



	public Progress getProgress() {
		return progress;
	}



	public void setProgress(Progress progress) {
		this.progress = progress;
	}



	public NodeDocument getOldRoot() {
		return oldRoot;
	}



	public NodeDocument getNewRoot() {
		return newRoot;
	}



	public NodeDocument getRealRootDocument() {
		return realRootDocument;
	}



	public View getView() {
		return view;
	}




	public Vector<ChangedLink> getChangedLinks() {
		return changedLinks;
	}



	public Vector<ChangedLink> getNewDocuments() {
		return newDocuments;
	}



	public Vector<URI> getCopiedDocuments() {
		return copiedDocuments;
	}
	
	
}
