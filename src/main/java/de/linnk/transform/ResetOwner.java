package de.linnk.transform;

import de.linnk.domain.Document;
import de.linnk.fatclient.application.LinnkFatClient;

public class ResetOwner extends Transformation {

	@Override
	public void applyTransformation(Document doc) {
		doc.setOwner(LinnkFatClient.currentUser, null);
	}

	public ResetOwner(Transformation next) {
		super(next);
	
	}
	
	

}
