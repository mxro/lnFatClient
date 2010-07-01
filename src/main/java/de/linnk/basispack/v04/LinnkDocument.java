package de.linnk.basispack.v04;



import java.util.Date;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicitCollection;

import de.linnk.domain.Item;
import de.linnk.domain.SimpleLink;
import de.linnk.domain.TitleItem;
import de.linnk.domain.Updatable;
import de.linnk.domain.User;
import de.linnk.domain.Versions;

@XStreamAlias("v02.linnkdocument")
@Deprecated
@XStreamImplicitCollection(value="items",item="item")
/**
 * just a hull to update data
 */
public class LinnkDocument implements Updatable {
// fields from document
	public User creator;
	public Date created;
	public List<Item> items;
	public SimpleLink template;
	public Versions versions;
	public  String name;
	public String filename;
	
	/**
	 * versions are nuked during update process
	 */
	public Object update() {
		
		final de.linnk.basispack.v05.NodeDocument ld = new de.linnk.basispack.v05.NodeDocument(this.creator, null, this.name);
		ld.setVersions(versions);
		for ( final Item i : this.items ) {
			if ( !(i instanceof TitleItem))
				ld.appendItem(i);
		}
		return ld;
	}
	
	
}
