package de.linnk.documentfilter;

import de.linnk.domain.Document;
import de.linnk.domain.Item;

public class DocumentsWithItems implements DocumentFilter {

	private final Class<? extends Item> itemClazz;
	
	public boolean acceptDocument(Document doc) {
		return doc.getItems(this.itemClazz).size() > 0;
	}

	public DocumentsWithItems(final Class<? extends Item> itemClazz) {
		super();
		this.itemClazz = itemClazz;
	}

}
