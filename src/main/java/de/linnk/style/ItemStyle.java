package de.linnk.style;

import org.xml.sax.SAXException;

import de.linnk.domain.Item;
import de.linnk.streaming.LinnkXStream;
import de.mxro.filesystem.Folder;
import de.mxro.xml.MyContentHandler;
import de.mxro.xml.style.XSLBuilder;

public abstract class ItemStyle<I extends Item> extends XSLBuilder {
	
	protected final I item;
	
	public ItemStyle(final I item) {
		super();
		this.item = item;
	}

	@Override
	public  boolean writeXSL(MyContentHandler hd, Folder files, String path)
			throws SAXException {
		de.mxro.xstream.XMLUtils.startXSLTemplateElement(hd, path+"[@class='"+LinnkXStream.singelton.resolveAlias(this.item.getClass())+"' "+
				"and id='"+this.item.getId()+"']");
		
		this.writeItemXML(hd, files, "item");
		de.mxro.xstream.XMLUtils.endXSLTemplateElement(hd);
		return true;
	}
	
	public abstract void writeItemXML(MyContentHandler hd, Folder files, String path) throws SAXException;

}
