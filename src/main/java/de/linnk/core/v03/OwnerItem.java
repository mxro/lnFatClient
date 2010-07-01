package de.linnk.core.v03;

import java.util.Date;

import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.SimpleLink;
import de.linnk.domain.Updatable;
import de.linnk.domain.User;

@Deprecated 
public class OwnerItem extends Item implements Updatable {

	public OwnerItem(User creator, String id, Document document) {
		super(creator, id, document);
		
	}

	public SimpleLink backlink;
	  public User creator;
	    public String id;
	    public Document document;
	    
	    public Date created;
		public String completeID;
		
	public Object update() {
		if (backlink == null) return null;
		
		return new de.linnk.domain.OwnerItem(creator, id, document, backlink.link);
	}

	
	
}
