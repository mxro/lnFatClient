package de.linnk.streaming;

import java.io.InputStream;
import java.io.OutputStream;

import com.thoughtworks.xstream.io.xml.AbstractXmlWriter;

import de.linnk.domain.Document;


/**
 * DocumentStreamer is an abstract class to stream a Linnk Document to XML.
 * Implementations {@link de.linnk.streaming.DomDocumentStreamer DomDocumentStreamer}
 * 
 * @author Max Rohde
 *
 */
public abstract class DocumentStreamer {
	
	public static DocumentStreamer saxDocumentStreamer = new SaxDocumentStreamer();
	public static DocumentStreamer domDocumentStreamer = new DomDocumentStreamer();
	public static DocumentStreamer singelton = new DomDocumentStreamer();
	public static String enclosingNodeName = "linnkcontent";
	public static String versionAttributeName = "version";
	public static String versionAttributeValue = "1.0";
	
	protected void addNodesBeforeDocument(AbstractXmlWriter writer) {
		writer.startNode(enclosingNodeName);
		writer.addAttribute(versionAttributeName, versionAttributeValue);
	}
	
	protected void addNodesAfterDocument(AbstractXmlWriter writer) {
		writer.endNode();
	}
	
	public abstract boolean writeToStream(OutputStream stream, Document document);
	public abstract Document readFromStream(InputStream stream);

	public DocumentStreamer() {
		super();
		
	}
	
	/*public boolean saveToStream(OutputStream stream, Document document) {
		// out = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");

		
		String docString = ObjectXMLTransformer.singelton.toXML(document);
		String wholeString = docString;//xmlDeclaration + processingInstructions + docString;
		try  {
			//stream.write(wholeString.getBytes());
			ObjectXMLTransformer.singelton.toStream(document, stream);
			return true;
		} catch (Exception e) { return false; }
		
	
	}
	
	public boolean loadToStream(InputStream stream, Document document) {
		String docString = ObjectXMLTransformer.singelton.toXML(document);
		String wholeString = xmlDeclaration + processingInstructions + docString;
		try  {
			stream.write(wholeString.getBytes());
			return true;
		} catch (IOException e) { return false; }
		
		return true;
	}*/
	
	
	
	
}
