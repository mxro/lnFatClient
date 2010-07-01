package de.linnk.basispack.v03;

import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.SimpleLink;
import de.linnk.domain.Updatable;
import de.linnk.domain.User;

@XStreamAlias("v03.documentproxy")
@Deprecated
public class DocumentProxy extends Item implements Updatable  {
	
	public SimpleLink link;
	public boolean expanded=false;
    public User creator;
    public String id;
    public Document document;
    public Item item;
    public Date created;
	public String completeID;
    
	public String documentData;

	public Object update() {
		
		return new de.linnk.basispack.v05.DocumentProxy( creator,  id, document,  item,  link, expanded);
	}

	public DocumentProxy(User creator, String id, Document document) {
		super(creator, id, document);
		
	}
	
	
	
}
