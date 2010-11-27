package de.linnk.batch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import de.linnk.Linnk;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;
import de.linnk.fatclient.application.LinnkAlias;
import de.linnk.streaming.views.PlainXMLView;
import de.mxro.filesystem.File;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.filesystem.Folder;

public class CheckValidity extends Action {
	
	private final boolean repair;
	
	public CheckValidity(final boolean repair) {
		super();
		this.repair = repair;
	}


	@Override
	public String doOnDocument(Document doc) {
		final String docId = doc.getFilename().toString();
		de.mxro.utils.log.UserError.singelton.log("begin check of: "+docId);
		String res="";
		if (!mx.gwtutils.MxroGWTUtils.getExtension(doc.getFilename()).equals("xml")) {
			res = res + docId+ ": File is no xml-File\n";
			de.mxro.utils.log.UserError.singelton.log(docId+ ": File is no xml-File ");
			if (this.repair) {
				final Folder newRoot = FileSystemObject.newLocalRootFolder(((NodeDocument) doc).getFolder());
				final File newFile = newRoot.forceFile(mx.gwtutils.MxroGWTUtils.removeExtension(doc.getFilename())+".xml");
				((NodeDocument) doc).setFolder(newRoot);
				doc.setFilename(newFile.getName());
				Linnk.S.saveDocument(doc, ((NodeDocument) doc).getFolder(), new PlainXMLView());
				
				res = res + docId+ ": xml-File repaired \n";
			}
		}
		
		try {
			try {
				final LinnkAlias la = LinnkAlias.fromStream(new FileInputStream(new java.io.File(mx.gwtutils.MxroGWTUtils.removeExtension(((NodeDocument) doc).getFile().getURI().getFile().getAbsolutePath())+".linnk")));
				if (!la.isValid()) {
					de.mxro.utils.log.UserError.singelton.log(docId+ ": Linnk Alias bad ");
					if (this.repair) {
						final LinnkAlias laNew = new LinnkAlias(doc.getFilename());
						laNew.toStream(new FileOutputStream(new java.io.File(mx.gwtutils.MxroGWTUtils.removeExtension(((NodeDocument) doc).getFile().getURI().getFile().getAbsolutePath())+".linnk")));
						de.mxro.utils.log.UserError.singelton.log(docId+ ": Linnk Alias repaired");
						res = res +docId+ ": Linnk Alias repaired\n";
					} else {
						res = res + docId+ ": Linnk Alias bad \n";
					}
				}
			} catch (final Exception e) {
				if (this.repair) {
					final LinnkAlias laNew = new LinnkAlias(doc.getFilename());
					laNew.toStream(new FileOutputStream(new java.io.File(mx.gwtutils.MxroGWTUtils.removeExtension(((NodeDocument) doc).getFile().getURI().getFile().getAbsolutePath())+".linnk")));
					de.mxro.utils.log.UserError.singelton.log(docId+ ": Linnk Alias repaired");
					res = res +docId+ ": Linnk Alias repaired\n";
				} else {
					res = res + docId+ ": Linnk Alias bad \n";
				}
			}
			
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// if (res.equals("")) return docId + ": is okay!";
		return res;
	}

}
