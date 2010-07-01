package de.linnk.streaming.views;

import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;

import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.TextItem;
import de.linnk.streaming.DocumentStreamer;
import de.linnk.streaming.DomDocumentStreamer;
import de.linnk.transform.ItemTransformation;
import de.mxro.filesystem.File;
import de.mxro.filesystem.Folder;
import de.mxro.string.filter.Filter;
import de.mxro.utils.URI;
import de.mxro.utils.Utils;
import de.mxro.utils.log.UserError;

public class PlainXMLView implements View {
	
	public static String bodyClass = "body";
	public static String titleClass = "title";

	private static class PrepareTextItems extends ItemTransformation<TextItem> {

		@Override
		public boolean accept(Item i) {
			return i instanceof TextItem;
		}
		
		private final static Filter CONTENT = Filter.regExBetween("<body>",
				"</body>", Filter.identity);
		
		public static final String getTextXML(String xml) {
			return "<div>" + CONTENT.perform(xml) + "</div>";
		}
		
		
		@Override
		public TextItem doOnItem(TextItem item) {
			item.setTextXML(getTextXML(item.getTextData()));
		
			return item;
		}
		
	}
	
	private final static ItemTransformation<TextItem> prepareTextItems = new PrepareTextItems();
	
	public void writeXMLContent(Document root, org.w3c.dom.Document doc) throws Exception {
		// TODO HACK to overcome problems with textXML not being updated
		de.linnk.utils.Utils.applyItemTransfomration(root, prepareTextItems);
		
		((DomDocumentStreamer) DocumentStreamer.domDocumentStreamer).writeToDocument(doc, root);
	}
	
	
	public final File writeXML(Document root, File toFile, Folder folder, URI xslFile) {
//		folder.createFile(root.getName()+".xml");
		try {
			//File docFile = root.getFile();
			final File docFile = toFile;
				
			
			if (docFile == null) {
				de.mxro.utils.log.UserError.singelton.log("XMLView - Error creating docfile", UserError.Priority.HIGH);
				return null;
			}
			
			final OutputStream os = docFile.getOutputStream();
			
			//System.out.println(new URI(root.getName()+".xml").getPath());
			
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			final DocumentBuilder db = dbf.newDocumentBuilder();
			
			final org.w3c.dom.Document doc = db.newDocument();
			
			//doc.setXmlVersion("1.0");
			//doc.setXmlStandalone(true);
			doc.appendChild(
			doc.createProcessingInstruction("xml-stylesheet", "href='"+folder.relativizeURI(xslFile)+"' type='text/xsl'"));
			
			this.writeXMLContent(root, doc);
			
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			
			final Transformer transformer = transformerFactory.newTransformer();
			final Source src = new DOMSource(doc);
			final Result res = new StreamResult(os);
			
			transformer.transform(src, res);
			
			//de.mxro.UserError.singelton.log("XMLView.writeXML: "+docFile.getURI(), UserError.Priority.LOW);
			return docFile;
		} catch (final DOMException e) {
			
			de.mxro.utils.log.UserError.singelton.log(e);
		} catch (final TransformerConfigurationException e) {
			de.mxro.utils.log.UserError.singelton.log(e);
		} catch (final ParserConfigurationException e) {
			de.mxro.utils.log.UserError.singelton.log(e);
		} catch (final TransformerFactoryConfigurationError e) {
			de.mxro.utils.log.UserError.singelton.log(e);
		} catch (final TransformerException e) {
			de.mxro.utils.log.UserError.singelton.log(e);
		} catch (final Exception e) {
			de.mxro.utils.log.UserError.singelton.log(e);
		}
		return null;
	}
	
	
	public LoadOnDemandDocument writeView(Document documentToSave, Folder destinationFolder) {
			
			File newFile = destinationFolder.forceFile(Utils.removeExtension(documentToSave.getFilename())+de.linnk.domain.LinnkConstants.xmlExtension); 
			UserError.singelton.log(this, "XML File to be saved under URI: "+newFile.getURI(), UserError.Priority.INFORMATION);
			
			URI xslURI =  newFile.getURI().changeExtension(de.linnk.domain.LinnkConstants.xslExtension);
			UserError.singelton.log(this, "XSL File to be saved under URI: "+newFile.getURI(), UserError.Priority.INFORMATION);
			
			File newXMLDocument = this.writeXML(documentToSave, newFile, destinationFolder, xslURI);
			UserError.singelton.log(this, "XML File saved under URI: "+newXMLDocument.getURI(), UserError.Priority.INFORMATION);
			
			return new LoadOnDemandDocument(newXMLDocument);
		
	}

	public View getPlainView() {
		return this;
	}
		

}
