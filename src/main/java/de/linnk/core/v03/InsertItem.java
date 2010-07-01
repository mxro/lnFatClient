package de.linnk.core.v03;

import de.linnk.domain.Item;
import de.linnk.domain.User;
import de.linnk.domain.InsertItem.Position;

@Deprecated
public class InsertItem extends ItemChange  {
    
	String position;
	Item relativeItem;
	
	public InsertItem(Item item, User user, Type type, final Item relativeItem, final Position position) {
		super(item, user, type);
	}
	
	
	
}
