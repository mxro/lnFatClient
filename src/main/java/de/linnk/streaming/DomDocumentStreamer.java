package de.linnk.streaming;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.thoughtworks.xstream.io.xml.DomReader;
import com.thoughtworks.xstream.io.xml.DomWriter;

import de.linnk.domain.Document;
import de.mxro.utils.log.UserError;




public class DomDocumentStreamer extends XStreamDocumentStreamer {

	@Override
	public Document readFromStream(InputStream stream) {
		
		try {
			/*DOMParser parser = new DOMParser();
			// Validierung deaktivieren
			
			parser.setFeature("http://xml.org/sax/features/validation",false);
			parser.parse(new InputSource(stream));
			org.w3c.dom.Document document = parser.getDocument();
			//org.w3c.dom.Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();*/
			
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			
			final DocumentBuilder db = dbf.newDocumentBuilder();
			final org.w3c.dom.Document document = db.parse(stream);
			
			
			//org.w3c.dom.Element rootElement = (org.w3c.dom.Element) document.getElementsByTagName("rootNode").item(1);
			final org.w3c.dom.Element rootElement = (org.w3c.dom.Element) document.getElementsByTagName(DocumentStreamer.enclosingNodeName).item(0);
			final org.w3c.dom.Node elem = (rootElement.getFirstChild());
			//org.w3c.dom.Node elem = rootElement.getElementsByTagName(de.mxro.xxsl.Utils.resolveAlias(LinnkDocument.class) ).item(0);
			
			if (!(elem instanceof org.w3c.dom.Element)) {
				UserError.singelton.log(this, "readFromStream: Invalid XML document!", UserError.Priority.HIGH);
				return null;
			}
			
			final DomReader reader = new DomReader((org.w3c.dom.Element) elem);
			final Document doc = (Document) this.xstream.unmarshal(reader);
			doc.updateItems();
			de.linnk.utils.Utils.setDocumentForItems(doc);
			
			return doc;
			

		} catch (final Exception e) { 
			de.mxro.utils.log.UserError.singelton.log(e);
			de.mxro.utils.log.UserError.singelton.log("DomDocumentStreamer: Error while reading document from stream");
			return null; 
		}
		
	}

	public final static TransformerFactory transformerFactory;
	public static Transformer transformer;
	
	static {
		transformerFactory = TransformerFactory.newInstance();
		try {
			transformer = transformerFactory.newTransformer();
		} catch (final Exception e) { e.printStackTrace();  }
	}
	
	public boolean writeToDocument(org.w3c.dom.Document doc, Document document) throws Exception {
		final DomWriter writer = new DomWriter(doc);
		
		this.addNodesBeforeDocument(writer);
		de.linnk.utils.Utils.removeDocumentForItems(document);
		this.xstream.marshal(document, writer);
		de.linnk.utils.Utils.setDocumentForItems(document);
		this.addNodesAfterDocument(writer);
		return true;
	}
	
	@Override
	public boolean writeToStream(OutputStream stream, Document document) {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			final DocumentBuilder db = dbf.newDocumentBuilder();
			
			final org.w3c.dom.Document doc = db.newDocument();
			
			//doc.setXmlVersion("1.0");
			//doc.setXmlStandalone(true);
			
			
			this.writeToDocument(doc, document);
			
			final Source src = new DOMSource(doc);
			final Result res = new StreamResult(stream);
			
			transformer.transform(src, res);
			return true;
		} catch (final Exception e) { e.printStackTrace(); }
		
		
		return false;
	}

}
