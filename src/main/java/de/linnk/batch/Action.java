package de.linnk.batch;

import de.linnk.domain.Document;

public abstract class Action {
	public abstract String doOnDocument(Document doc);
}
