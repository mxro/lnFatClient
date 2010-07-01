package de.linnk.batch;

import java.util.Vector;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.documentfilter.DocumentFilter;
import de.linnk.domain.Document;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.streaming.DocumentLoader;
import de.mxro.filesystem.File;
import de.mxro.filesystem.Folder;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;
import de.mxro.utils.progress.Progress;

public class Batch {
	
	public boolean includeRootForChildren = true;
	
	//public static final Action ADD_BRS = new TransformationAction(new UpdateTextInTextItem(Filter.regExReplace("</body>", "<br></body>", Filter.identity), Transformation.NONE));
	//public static final Action REMOVE_PS = new TransformationAction(new UpdateTextInTextItem(Filter.regExReplace("<p>", "", Filter.regExReplace("</p>", "", Filter.identity)), Transformation.NONE));
	//public static final Action DELETE_LINKS = new TransformationAction(new RemoveItems(de.linnk.basispack.v02.Links.class, new RemoveItems(de.linnk.basicpack.v01.Links.class, Transformation.NONE)));
	//public static final Action UPDATE_TEXTITEMS = new TransformationAction(new UpdateTextItems(Transformation.NONE));
	public static final Action SAVE_ALL = new SaveAll(LinnkFatClient.application.getDefaultView());
	
	public String doForAllDocuments(Folder rootFolder, Vector<URI> vector, Action action, Progress progress) {
		String res = null;
		progress.setMaximum(vector.size());
		int counter=0;
		for (final URI u : vector) {
			
			final File file = rootFolder.getFile(u);
			
			if (file == null) {
				UserError.singelton.log(this, "doForAllDocuments: Could not find file: "+u+" in "+rootFolder.getURI(), UserError.Priority.HIGH);
				//return "doForAllDocuments: Could not find file: "+u+" in "+rootFolder.getURI();
				continue;
			}
			progress.setMessage("Processing "+file.getName());
			
			final Document doc = DocumentLoader.singelton.loadFromFile(file);
			if (doc == null) {
				UserError.singelton.log(this, "doForAllDocuments: Could load document: "+file.getURI()+" in "+rootFolder.getURI(), UserError.Priority.HIGH);
				continue;
			}
			
			res =action.doOnDocument(doc);
			counter++;
			progress.setProgress(counter);
			//if (res != null) return res;
		}
		return res;
	}
	
	public String doForAllChildren(NodeDocument doc, Action action) {
		return this.doForAllChildren(doc, action,  null);
	}
	
	public String doForAllChildren(NodeDocument doc, Action action, DocumentFilter excludeChildrenOf) {
		return this.doForAllChildren(doc, action, excludeChildrenOf, new Vector<URI>());
	}
	
	public String doForAllChildren(NodeDocument doc, Action action, DocumentFilter excludeChildrenOf, Vector<URI> processedDocs) {
	
		if (processedDocs.contains(doc.getFile().getURI())) {
			UserError.singelton.log("Double visit: "+doc.getFilename(), UserError.Priority.HIGH);
			return "double visit\n";
		}
		
		String res = "";
		if (this.includeRootForChildren) {
			processedDocs.add(doc.getFile().getURI());
		    res = action.doOnDocument(doc);
		}
		
		if (excludeChildrenOf != null) {
			if (excludeChildrenOf.acceptDocument(doc)) {
				UserError.singelton.log("Batch.doForAllChildren: "+action.getClass()+" excluded Document: "+doc.getFilename(), UserError.Priority.INFORMATION);
				return null;
			}
		}
		
		for (final String uristr : doc.getChildNodes()) {
			URI uri = URIImpl.create(uristr);
			final NodeDocument d = DocumentLoader.singelton.loadFromFile(doc.getFolder().getFile(uri));
			//	doc.loadDocumentFromSimpleLink(link);
			if (d != null) {
				final String act = this.doForAllChildren(d, action, excludeChildrenOf, processedDocs);
				
				if (!this.includeRootForChildren) {
					processedDocs.add(d.getFile().getURI());
				    res = action.doOnDocument(d);
				}
				
				if (act != null) {
					res = res + act;
				}
			} else {
				res = res + uri + ": does not exist ";
			}
			
		}
		return res;
	}

	public Batch() {
		super();
	}
	
	
}
