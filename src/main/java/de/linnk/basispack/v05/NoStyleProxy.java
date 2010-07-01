package de.linnk.basispack.v05;

import de.linnk.domain.Document;
import de.linnk.domain.EasyEditItem;
import de.linnk.domain.Item;
import de.linnk.domain.ProxyItem;
import de.linnk.domain.User;
import de.mxro.utils.domain.Styled;

public class NoStyleProxy extends ProxyItem implements Styled, EasyEditItem {

	public NoStyleProxy(User creator, String id, Document document, Item item) {
		super(creator, id, document, item);
		
	}
	
}
