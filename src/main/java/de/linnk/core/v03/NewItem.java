package de.linnk.core.v03;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.linnk.domain.Item;
import de.linnk.domain.User;



@XStreamAlias("v03.newitem")
@Deprecated
public class NewItem extends ItemChange  {

	public NewItem( Item item, User user, Type type) {
		super( item, user, type);
		// de.mxro.UserError.singelton.log("created new item"+this.getItem().getId());
	}
	
	
	
}
