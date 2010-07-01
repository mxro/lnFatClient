package de.linnk.batch;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;
import de.linnk.streaming.views.View;

public class SaveAll extends Action {

	private final View view;
	
	@Override
	public String doOnDocument(Document doc) {
		doc.touch();
		if (this.view.writeView(doc, ((NodeDocument) doc).getFolder()) != null) {
			de.mxro.utils.log.UserError.singelton.log("saved "+doc.getFilename().toString());
			return null;
		} else {
			de.mxro.utils.log.UserError.singelton.log("error saving "+doc.getFilename().toString());
			return "error saving "+doc.getFilename().toString();
		}
			
	}

	public SaveAll(final View view) {
		super();
		this.view = view;
	}
	
}
