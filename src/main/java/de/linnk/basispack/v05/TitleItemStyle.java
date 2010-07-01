package de.linnk.basispack.v05;

import org.xml.sax.SAXException;

import de.linnk.domain.Item;
import de.linnk.style.ItemStyle;
import de.mxro.filesystem.Folder;
import de.mxro.xml.MyContentHandler;

public class TitleItemStyle extends ItemStyle {

	

	public TitleItemStyle(Item item) {
		super(item);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void writeItemXML(MyContentHandler hd, Folder files, String path)
			throws SAXException {
		//de.mxro.Utils.startDivElement(hd, "titleitem");
		//de.mxro.Utils.writeValueOfElement(hd, "title");
		//de.mxro.Utils.endDivElement(hd);

	}

}
