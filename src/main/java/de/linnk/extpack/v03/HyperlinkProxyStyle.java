package de.linnk.extpack.v03;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.linnk.ProxyItemStyle;
import de.mxro.filesystem.Folder;
import de.mxro.xml.MyContentHandler;

public class HyperlinkProxyStyle extends ProxyItemStyle<HyperlinkProxy> {

	
	
	public HyperlinkProxyStyle(HyperlinkProxy item) {
		super(item);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void writeItemXML(MyContentHandler hd, Folder files, String path)
			throws SAXException {	
		String hyperlink = null;
		// try to copy files if link is relative
//		final HyperlinkProxy hp = (HyperlinkProxy) this.item;
//		try {
//			final URI hyperlinkURI = URI.create(hp.getHyperlink());
//			
//			if (!hyperlinkURI.isAbsolute() && this.item.getDocument().getFolder() != null) {
//				
//				
//				final FileSystemObject fso = this.item.getDocument().getFolder().get(hyperlinkURI);
//				//System.out.println("fso: "+fso.getURI().getPath()+" fso name "+fso.getName());
//				if (fso != null) {
//					final FileSystemObject file = files.importObject(fso, true);
//					
//					if (file != null) {
//						hyperlink = file.getName();
//					} else {
//						UserError.singelton.log("Could not import: "+fso.getURI(), UserError.Priority.HIGH);
//					}
//				} else {
//					UserError.singelton.log("HyperlinkStyle: Could not find linked object : '"+hyperlinkURI+"' resolved: "+this.item.getDocument().getFolder().resolveURI(hyperlinkURI), UserError.Priority.LOW);
//				}
//			}
//		} catch (final IOException e) {
//			UserError.singelton.log(e);
//		}
		
		de.mxro.xstream.XMLUtils.startDivElement(hd, "hyperlinkproxy");
		
		hd.startElement("", "", "a", null);
		final AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", "name", "CDATA", "href");
		hd.startElement("", "", "xsl:attribute", atts);
		
		if (hyperlink == null) {
			de.mxro.xstream.XMLUtils.writeValueOfElement(hd, "hyperlink");
		} else {
			de.mxro.xstream.XMLUtils.writeCharacters(hd, hyperlink);
		}
		hd.endElement("", "", "xsl:attribute");
		
		
		
		de.mxro.xstream.XMLUtils.writeApplyTemplatesElement(hd, "item");
		//de.mxro.Utils.writeValueOfElement(hd, "title");
		hd.endElement("", "", "a");
		de.mxro.xstream.XMLUtils.endDivElement(hd);
		
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
