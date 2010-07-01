package de.linnk.documentfilter;

import de.linnk.domain.Document;
import de.linnk.extpack.v03.NeverPublishItem;

public interface DocumentFilter {
	public static DocumentFilter NOT_PUBLISHABLE = new DocumentsWithItems(NeverPublishItem.class);
	public static DocumentFilter ACCEPT_NONE = new DocumentFilter() {

		public boolean acceptDocument(Document doc) {
			return false;
		}
		
	};
	public boolean acceptDocument(Document doc);
}
