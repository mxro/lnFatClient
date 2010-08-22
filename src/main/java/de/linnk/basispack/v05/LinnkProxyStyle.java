package de.linnk.basispack.v05;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.linnk.ProxyItemStyle;
import de.mxro.filesystem.Folder;
import de.mxro.xml.MyContentHandler;

public class LinnkProxyStyle extends ProxyItemStyle<LinnkProxy> {

	public LinnkProxyStyle(LinnkProxy item) {
		super(item);
		
	}

	@Override
	public void writeItemXML(MyContentHandler hd, Folder files, String path)
			throws SAXException {
		de.linnk.streaming.XMLUtils.startDivElement(hd, "linnkproxy");
		
		hd.startElement("", "", "a", null);
		final AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", "name", "CDATA", "href");
		hd.startElement("", "", "xsl:attribute", atts);
		de.linnk.streaming.XMLUtils.writeValueOfElement(hd, "link");
		hd.endElement("", "", "xsl:attribute");
			
		de.linnk.streaming.XMLUtils.writeApplyTemplatesElement(hd, "item");
		//de.mxro.Utils.writeValueOfElement(hd, "title");
		hd.endElement("", "", "a");
		de.linnk.streaming.XMLUtils.endDivElement(hd);
	
	}

	@Override
	public boolean writeXSL(MyContentHandler hd, Folder files, String path) throws SAXException {
		super.writeXSL(hd, files, path);
		
		/*XSLBuilder builder = Linnk.S.getXSLBuilder(this.item.getItem());
		
		if (builder != null) {
			builder.writeXSL(hd, files, "item");
		}*/
		
		return true;
	}
	
	

}
