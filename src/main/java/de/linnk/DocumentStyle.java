package de.linnk;

import org.xml.sax.SAXException;

import de.linnk.domain.Document;
import de.mxro.filesystem.Folder;
import de.mxro.xml.MyContentHandler;
import de.mxro.xml.style.XSLBuilder;

public abstract class DocumentStyle extends XSLBuilder {

	@Override
	public abstract boolean writeXSL(MyContentHandler hd, Folder files, String path) throws SAXException;
	
	
	public final Document doc;
	
	
	public DocumentStyle(final Document doc) {
		super();
		this.doc = doc;
	}
	
	
}
