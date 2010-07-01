package de.linnk.transform;

import de.linnk.domain.Document;
import de.mxro.utils.URI;

public class ChangeURI extends Transformation {

	private final URI newURI;
	
	@Override
	public void applyTransformation(Document doc) {
		// Change URI for document itself
		doc.setUniqueURI(newURI.toString());
		// Change URI for children
		
	}

	public ChangeURI(URI newURI, Transformation next) {
		super(next);
		this.newURI = newURI;
	}

	
	
}
