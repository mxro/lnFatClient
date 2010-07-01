package de.linnk.basispack.v05;



import java.util.Vector;

import thewebsemantic.Namespace;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.linnk.ChangableDocument;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.domain.LinnkConstants;
import de.linnk.domain.ProxyItem;
import de.linnk.domain.TitleItem;
import de.linnk.domain.User;
import de.linnk.gwt.LinnkGWTUtils;
import de.mxro.filesystem.File;
import de.mxro.filesystem.Folder;
import de.mxro.jena.JenaTripleBuilder;
import de.mxro.jena.Semantic;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;

/**
 * 
 * Version 3:
 * Each document has a unique URI
 * 
 * @author mx
 *
 */
@XStreamAlias("v03.linnkdocument")
@Namespace("http://www.linnk.de/semantic/basic.rdf#")
public class NodeDocument extends ChangableDocument implements 
  Semantic
  {
	
	public NodeDocument(User creator, Folder folder, String name) {
		super(creator, name);
		this.folder = folder;
		this.setTitle(creator, name);
	}

    @Override
	public JenaTripleBuilder getTripleBuilder() {
		return new LinnkDocumentTripleBuilder(this);
	}
	
	public TitleItem newTitleItem(User user, String title) {
		return new TitleItem(user, "title", this, title);
	}
	
	public void setTitle(User creator, String title) {
		this.doChange(
				 ItemChange.newNewItem(
						 this.newTitleItem(creator, title), 
						 	creator) );
	}
	
	public String getTitle() {
		for (final Item i : this.getItems()) {
			if (i instanceof TitleItem)
				return ((TitleItem) i).getTitle();
		}
		return null;
	}

	
	public File getFile() {
		if (this.getFolder() == null) {
			UserError.singelton.log(this, "getFile: Document "+this.getUniqueURI()+" has no Folder", UserError.Priority.HIGH);
			return null;
		}
		File f = this.getFolder().getFile(URIImpl.create(this.getFilename()));
		if (f == null) {
			UserError.singelton.log(this, "getFile: Could not find file "+this.getFilename()+" \n" +
					" in folder "+this.getFolder().getURI().toString()+" \n" +
							" for document "+this.getUniqueURI(), UserError.Priority.HIGH);
			return null;
		}
		return f;
	}
	
	protected transient Folder folder;
	
	protected transient File file;
	/**
	 * the Folder in which document attachments are stored
	 * like png files of the images that are embedded
	 * in the document XML
	 */
	protected transient Folder docAttachments;
	
	public Folder getDocAttachmentsFolder() {
		if (docAttachments == null) {
			if (this.getFolder() == null) return null;
			docAttachments = this.getFolder().forceFolder(LinnkConstants.DOCUMENT_ATTACHMENT_FOLDER_PATH);
		}
		return docAttachments;
	}
	
	public final void setFolder(Folder folder) {
		this.folder = folder;
	}
	
	public Folder getFolder() {
		return this.folder;
	}
	
	
	/**
	 * links to all documents that are owned by this document
	 * @return
	 */
	public Vector<String> getChildNodes() {
		final Vector<String> res = new Vector<String>();
		
		
		for (final Item item : this.getItems()) {
			for (final ProxyItem pi : LinnkGWTUtils.getProxies(DocumentProxy.class, item) ) {
			//	item.getProxies(DocumentProxy.class)) {
				res.add(this.getFolder().getURI().resolve(((DocumentProxy) pi).getLink().toURI()).toString());
			}
		}
		return res;
	}
	
}
