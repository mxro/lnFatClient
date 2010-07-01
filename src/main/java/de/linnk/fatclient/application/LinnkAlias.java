package de.linnk.fatclient.application;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;

import de.linnk.domain.Document;
import de.linnk.streaming.DocumentLoader;
import de.mxro.filesystem.File;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.filesystem.Folder;
import de.mxro.utils.URIImpl;
import de.mxro.utils.Validierbar;
import de.mxro.utils.log.UserError;


@XStreamAlias("linnkalias")
public class LinnkAlias implements Validierbar {
	protected final String destination;
	
	public boolean isValid() {
		return de.mxro.utils.Utils.getExtension(this.destination).equals("xml");
	}

	public LinnkAlias(final String desitnation) {
		super();
		this.destination = desitnation;
	}
	
	
	
	public String getDestination() {
		return this.destination;
	}

	public Document getDocument(Folder folder) {
		try {
			final File documentFile = folder.getFile(new URIImpl(this.destination));
			
			if (documentFile == null) {
				de.mxro.utils.log.UserError.singelton.log("LinnkAlias.getDocument: linked File '"+this.destination+"' does not exist.");
				return null;
			}
			
			return DocumentLoader.singelton.loadFromFile(documentFile);
		} catch (final URISyntaxException e) {
			de.mxro.utils.log.UserError.singelton.log(e);
			de.mxro.utils.log.UserError.singelton.log("LinnkAlias.getDocument: Invalid Uri: "+this.destination);
		}
		return null;
	}
	
	public Document getDocument(java.io.File currentDir) {
		if (!currentDir.exists()) {
			de.mxro.utils.log.UserError.singelton.showError("LinnkAlias.getDocument: Alias could not be resolved. currentDir does not exist!");
			return null;
		}
		final String dest = de.mxro.utils.Utils.removeExtension(this.destination) + ".xml";
		final java.io.File documentFile = new java.io.File(currentDir.getAbsolutePath()+"/"+dest);
		if (!documentFile.isFile()) {
			de.mxro.utils.log.UserError.singelton.showError("LinnkAlias.getDocument: Destination does not exist!");
			return null;
		}
		
		try {
			de.mxro.utils.log.UserError.singelton.log("LinnkAlias.getDocument: openDocument "+documentFile, UserError.Priority.LOW);
			final Document doc = DocumentLoader.singelton.loadFromFile(URIImpl.fromFile(documentFile));
			final File ownFile = FileSystemObject.newLocalRootFolder(URIImpl.fromFile(documentFile.getParentFile().getAbsoluteFile())).forceFile(documentFile.getName());
			assert (ownFile != null);
			doc.setFilename(ownFile.getName());
			return doc;
		//	DocumentStreamer.domDocumentStreamer.readFromStream(new java.io.FileInputStream(documentFile));
		} catch (final URISyntaxException e) {
			de.mxro.utils.log.UserError.singelton.showError("LinnkAlias: invalid URI!");
		}
		
		return null;
	}
	
	public static LinnkAlias fromStream(InputStream is) {
		final XStream xstream = new XStream(new DomDriver());
		return (LinnkAlias) xstream.fromXML(is);
	}
	
	public void toStream(OutputStream os) {
		final XStream xstream = new XStream();
		xstream.toXML(this, os);
	}
	
}
