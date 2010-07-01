package de.linnk.style;

import org.xml.sax.SAXException;

import de.linnk.domain.Item;
import de.mxro.filesystem.Folder;
import de.mxro.xml.MyContentHandler;

public class NoItemStyle extends ItemStyle {
	

	public NoItemStyle(Item item) {
		super(item);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void writeItemXML(MyContentHandler hd, Folder files, String path)
			throws SAXException {
		// TODO Auto-generated method stub

	}

}
