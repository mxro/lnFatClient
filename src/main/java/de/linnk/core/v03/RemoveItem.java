package de.linnk.core.v03;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.linnk.domain.Item;
import de.linnk.domain.User;


@XStreamAlias("v03.removeitem")
@Deprecated
public class RemoveItem extends ItemChange {

	public RemoveItem(Item item, User user, Type type) {
		super(item, user, type);
		
	}

	
	
}
