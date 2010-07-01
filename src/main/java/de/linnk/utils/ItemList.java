package de.linnk.utils;

import java.util.List;
import java.util.Vector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicitCollection;

import de.linnk.domain.Copyable;
import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.streaming.LinnkXStream;

@XStreamImplicitCollection(value="items",item="item")
@XStreamAlias("v02.itemlist")
public class ItemList  {
	private final List<Item> items;
	
	public final static String ITEMLISTIDENTIFIER = "<? linnkItemList ?>";
	
	public final List<Item> getItems() {
		return this.items;
	}

	public ItemList() {
		super();
		this.items = new Vector<Item>();
	}

	@Override
	public String toString() {
		for (final Item i: this.items) {
			if (i instanceof Copyable) {
				((Copyable) i).beforeToString();
			} else {
				de.mxro.utils.log.UserError.singelton.log("ItemList.toString: cannot convert item: "+i.getId()+". It must implement interface copyable.");
			}
		}
		final String res = ITEMLISTIDENTIFIER + LinnkXStream.singelton.toXML(this);
		for (final Item i: this.items) {
			if (i instanceof Copyable) {
				((Copyable) i).afterToString();
			}
		}
		return res;
	}
	
	public static final ItemList fromString(String s, Document newDocument) {
		final int idx = s.indexOf(ITEMLISTIDENTIFIER);
		if (idx >= 0) {
			final String content = s.substring(idx + ITEMLISTIDENTIFIER.length());
			final ItemList res = (ItemList) LinnkXStream.singelton.fromXML(content);
			for (final Item i : res.items ) {
				i.afterFromString(newDocument);
			}
			return res;
		}
		return null;
	}
	
	
	
}
