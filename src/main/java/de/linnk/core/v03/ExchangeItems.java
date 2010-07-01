package de.linnk.core.v03;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.linnk.domain.Item;
import de.linnk.domain.User;

@XStreamAlias("v03.exchangeitem")
@Deprecated
public class ExchangeItems extends ItemChange  {
	
	Item item2;
	
	public ExchangeItems(Item item, User user, Type type, final Item item2) {
		super(item, user, type);
	}
	
}
