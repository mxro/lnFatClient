package de.linnk.core.v03;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.linnk.domain.Item;
import de.linnk.domain.User;



@XStreamAlias("v03.modifyitem")
@Deprecated
public class ModifyItem extends ItemChange  {
	
	Item olditem;
	
	public ModifyItem(Item item, Item olditem, User user, Type type) {
		super(item, user, type);
	}

}
