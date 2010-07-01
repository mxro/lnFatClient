package de.linnk.basispack.v05;

import org.xml.sax.SAXException;

import de.linnk.domain.TextItem;
import de.linnk.style.ItemStyle;
import de.mxro.filesystem.Folder;
import de.mxro.xml.MyContentHandler;

public class TextItemStyle extends ItemStyle<TextItem> {

	public TextItemStyle(TextItem item) {
		super(item);		
	}

	@Override
	public void writeItemXML(MyContentHandler hd, Folder files, String path) throws SAXException {
		de.mxro.xstream.XMLUtils.startDivElement(hd, "textitem");
		
		de.mxro.xstream.XMLUtils.writeCopyOfElement(hd, "textXML/*|textXML/text()"); // otherwise firefox does not show items without any elements
		
		de.mxro.xstream.XMLUtils.endDivElement(hd);
		
	}

}
