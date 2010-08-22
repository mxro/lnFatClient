package de.linnk.streaming;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.mxro.xml.MyContentHandler;

public class XMLUtils {
public static void insertNode(HierarchicalStreamWriter writer, org.w3c.dom.Node node, boolean addXMLNS) {
		
		final NamedNodeMap map = node.getAttributes();
		if (map.getLength() > 0) {
			for (int i=0; i<map.getLength();i++) {
				final Node attr = map.item(0);
				writer.addAttribute(attr.getNodeName(), attr.getNodeValue());
			}
		}
		
		final NodeList list = node.getChildNodes();
		if (list.getLength() > 0) {
			for (int i=0; i<list.getLength(); i++) {
				if (list.item(i).getNodeType() == Node.TEXT_NODE) {
					writer.setValue(list.item(i).getNodeValue());
					
				} else if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
					
					writer.startNode(list.item(i).getNodeName());
					if (node.getNamespaceURI() != "" && node.getNamespaceURI() != null) {
						writer.addAttribute("xmlns", node.getNamespaceURI());
					}
					if (addXMLNS) {
						writer.addAttribute("xmlns", "http://www.w3.org/1999/xhtml");
					}
					insertNode(writer, list.item(i), addXMLNS);
					writer.endNode();
				} else if (list.item(0).getNodeType() == Node.ATTRIBUTE_NODE) {
					
					writer.addAttribute(list.item(0).getNodeName(), list.item(0).getNodeValue());
				} 
				
				
				
			}
		}
		
	}
	
public static void writeCharacters(MyContentHandler hd, String characters) throws SAXException {
	hd.startCDATA();
	
	hd.characters(characters.toCharArray(), 0, characters.length());
	hd.endCDATA();
}

public static void writeValueOfElement(MyContentHandler hd, String select) throws SAXException {
	final AttributesImpl atts = new AttributesImpl();
	atts.addAttribute("", "", "select", "CDATA",select);
	hd.startElement("", "", "xsl:value-of", atts);
	hd.endElement("", "", "xsl:value-of");
}

public static void writeApplyTemplatesElement(MyContentHandler hd, String select) throws SAXException {
	final AttributesImpl atts = new AttributesImpl();
	atts.addAttribute("", "", "select", "CDATA",select);
	hd.startElement("", "", "xsl:apply-templates", atts);
	hd.endElement("", "", "xsl:apply-templates");
}

public static void writeApplyTemplatesElement(MyContentHandler hd) throws SAXException {
	hd.startElement("", "", "xsl:apply-templates", null);
	hd.endElement("", "", "xsl:apply-templates");
}

public static void writeCopyOfElement(MyContentHandler hd, String select) throws SAXException {
	final AttributesImpl atts = new AttributesImpl();
	atts.addAttribute("", "", "select", "CDATA",select);
	hd.startElement("", "", "xsl:copy-of", atts);
	hd.endElement("", "", "xsl:copy-of");
}

public static void startXSLTemplateElement(MyContentHandler hd, String match) throws SAXException {
	final AttributesImpl atts = new AttributesImpl();
	atts.addAttribute("", "", "match", "CDATA", match);
	hd.startElement("", "", "xsl:template", atts);
}

public static void endXSLTemplateElement(MyContentHandler hd) throws SAXException {
	hd.endElement("", "", "xsl:template");
}

public static void startDivElement(MyContentHandler hd, String clazz) throws SAXException {
	final AttributesImpl atts = new AttributesImpl();
	atts.addAttribute("", "", "class", "CDATA",clazz);
	hd.startElement("", "", "div", atts);
}

public static void endDivElement(MyContentHandler hd) throws SAXException {
	hd.endElement("", "", "div");
}
}
