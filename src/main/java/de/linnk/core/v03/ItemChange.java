package de.linnk.core.v03;

import java.util.Date;

import de.linnk.domain.Item;
import de.linnk.domain.User;

@Deprecated
public abstract class ItemChange {
	
	//private Boolean reversible=true;
	//public boolean implicit=false;
	
	public static enum Type {
		REVERSIBLE, 
		
		/**
		 * this is done automatically
		 */
		IMPLICIT,
		
		/**
		 * just skip it
		 */
		SKIP,
		
		/**
		 * this is a stopper
		 * 
		 */
		IRREVERSIBLE
	}
	
	public Type type;
	
	protected ItemChange(final Item item, final User user, final Type type) {
		super();
		this.item = item;
		this.user = User.newInstance(user); // this is a trick to aviod that xstream uses references
		this.date = new Date();
		this.type = type;
		
	}


	protected final Item item;
	private final User user;
	private final Date date;
	
	
}
