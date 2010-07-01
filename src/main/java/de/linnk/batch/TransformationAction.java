package de.linnk.batch;

import de.linnk.Linnk;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.streaming.views.View;
import de.linnk.transform.Transformation;
import de.mxro.utils.log.UserError;

public class TransformationAction extends Action {
	
	private final Transformation transformation;
	private final View view;
	
	@Override
	public String doOnDocument(Document doc) {
		this.transformation.transform(doc);
		if (doc != null) {
			doc.touch();
			if (Linnk.S.saveDocument(doc, ((NodeDocument) doc).getFolder(), this.view) == null) {
				UserError.singelton.log(this, "Could not save document: "+doc.getFilename(), UserError.Priority.HIGH);
				return "Could not save document: "+doc.getFilename();
			}
			return null;
		}
		UserError.singelton.log(this, "Error apply transformation: "+doc.getFilename().toString(), UserError.Priority.NORMAL);
		return "Error apply transformation: "+doc.getFilename().toString();
	}

	public TransformationAction(final Transformation transformation) {
		this(LinnkFatClient.application.getDefaultView(), transformation);
	}
	
	public TransformationAction(final View view, final Transformation transformation) {
		super();
		this.transformation = transformation;
		this.view = view;
	}

	
	
}
