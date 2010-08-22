package de.linnk.basispack.v05;

import org.xml.sax.SAXException;

import de.linnk.DocumentStyle;
import de.linnk.domain.Document;
import de.linnk.streaming.DocumentStreamer;
import de.linnk.streaming.LinnkXStream;
import de.linnk.streaming.XMLUtils;
import de.mxro.filesystem.Folder;
import de.mxro.xml.MyContentHandler;

public class DefaultDocumentStyle extends DocumentStyle {

	public String creatorClass = "creator";
	public String createdClass = "created";
	
	
	
	public DefaultDocumentStyle(Document doc) {
		super(doc);
		
	}



	@Override
	public boolean writeXSL(MyContentHandler hd, Folder files, String path) throws SAXException {
		
		XMLUtils.startXSLTemplateElement(hd, DocumentStreamer.enclosingNodeName+"/"+LinnkXStream.singelton.resolveAlias(this.doc.getClass()));
				//de.mxro.xstream.Utils.resolveAlias(this.doc.getClass()) );
		de.linnk.streaming.XMLUtils.startDivElement(hd, "document");
		XMLUtils.writeApplyTemplatesElement(hd);
		de.linnk.streaming.XMLUtils.endDivElement(hd);
		XMLUtils.endXSLTemplateElement(hd);
		
		de.linnk.streaming.XMLUtils.startXSLTemplateElement(hd, "uri");
		de.linnk.streaming.XMLUtils.endXSLTemplateElement(hd);
		
		de.linnk.streaming.XMLUtils.startXSLTemplateElement(hd, "creator");
		/*Utils.assertField("creator", this.doc.getClass());
		de.mxro.Utils.startDivElement(hd, this.creatorClass);
		de.mxro.Utils.writeValueOfElement(hd, "self::*");
		de.mxro.Utils.endDivElement(hd);*/
		de.linnk.streaming.XMLUtils.endXSLTemplateElement(hd);
		
		de.linnk.streaming.XMLUtils.startXSLTemplateElement(hd, "created");
		/*Utils.assertField("created", this.doc.getClass());
		de.mxro.Utils.startDivElement(hd, this.createdClass);
		de.mxro.Utils.writeValueOfElement(hd, "self::*");
		de.mxro.Utils.endDivElement(hd);*/
		de.linnk.streaming.XMLUtils.endXSLTemplateElement(hd);	
		
		de.linnk.streaming.XMLUtils.startXSLTemplateElement(hd, "versions");
		de.linnk.streaming.XMLUtils.endXSLTemplateElement(hd);
		
		/*de.mxro.Utils.startXSLTemplateElement(hd, "folder");
		de.mxro.Utils.endXSLTemplateElement(hd);
		
		de.mxro.Utils.startXSLTemplateElement(hd, "file");
		de.mxro.Utils.endXSLTemplateElement(hd);*/
		
		de.linnk.streaming.XMLUtils.startXSLTemplateElement(hd, "name");
		de.linnk.streaming.XMLUtils.endXSLTemplateElement(hd);
		
		de.linnk.streaming.XMLUtils.startXSLTemplateElement(hd, "filename");
		de.linnk.streaming.XMLUtils.endXSLTemplateElement(hd);
		
		
		// de.mxro.Utils.writeApplyTemplatesElement(hd);
		
		return true;
	}

	
}
