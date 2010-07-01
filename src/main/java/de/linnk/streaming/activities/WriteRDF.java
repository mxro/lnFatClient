package de.linnk.streaming.activities;

import de.linnk.domain.Document;
import de.linnk.streaming.views.View;
import de.mxro.filesystem.Folder;
import de.mxro.utils.background.Activity;

public class WriteRDF implements Activity {
	
	private final Document document;
	private final View view;
	private final Folder folder;
	
	public void run() {
		view.writeView(document, folder);

	}

	public WriteRDF(Document document, View view, Folder folder) {
		super();
		this.document = document;
		this.view = view;
		this.folder = folder;
	}
	
	
	
}
