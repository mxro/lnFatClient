package de.linnk.streaming;

import java.util.List;

import org.xml.sax.SAXException;

import de.linnk.Linnk;
import de.linnk.domain.Item;
import de.linnk.style.NoItemStyle;
import de.mxro.filesystem.Folder;
import de.mxro.xml.MyContentHandler;
import de.mxro.xml.style.XSLBuilder;

public class ItemsXSLBuilder {
	private final Folder folder;
	private final MyContentHandler hd;
	private final List<Item> items;
	
	
	public ItemsXSLBuilder(final Folder folder, final MyContentHandler hd, final List<Item> items) {
		super();
		this.folder = folder;
		this.hd = hd;
		this.items = items;
	}
	
	public void build() throws SAXException {
		
		for (final Item item : this.items) {
			XSLBuilder builder = Linnk.S.getXSLBuilder(item);
			
			if (builder != null) {
				final XSLBuilder itembuilder = builder;
				itembuilder.writeXSL(this.hd, this.folder, "item");
			} else {
				final XSLBuilder nostyle = new NoItemStyle(item);
				nostyle.writeXSL(this.hd, this.folder, "item");
			}
		}
	}
	
	
}
