package de.linnk.streaming.views;

import de.linnk.domain.Document;
import de.linnk.streaming.DocumentLoader;
import de.mxro.filesystem.File;

public class LoadOnDemandDocument {
	private final File file;
	private Document document;
	
	
	public LoadOnDemandDocument(File file) {
		super();
		this.file = file;
		this.document = null;
	}
	
	public Document getDocument() {
		if (this.document != null) return document;
		
		this.document = DocumentLoader.singelton.loadFromFile(file);
		return document;
	}

	public File getFile() {
		return file;
	}
	
}
