package de.linnk.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.thoughtworks.xstream.io.xml.SaxWriter;

import de.linnk.domain.Document;


public class SaxDocumentStreamer extends XStreamDocumentStreamer {
	
	public final OutputFormat outputformat ;
	
	public SaxDocumentStreamer() {
		super();
		this.outputformat = new OutputFormat("XML","UTF-8",true); // encoding works ! (?)
		this.outputformat.setIndent(1);
		this.outputformat.setIndenting(true);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public Document readFromStream(InputStream stream) {
		throw new RuntimeException("not yet supported");
		//return null;
	}

	@Override
	public boolean writeToStream(OutputStream stream, Document document) {
		final XMLSerializer serializer = new XMLSerializer(stream,this.outputformat);
		final SaxWriter writer = new SaxWriter();
		try {
			writer.setContentHandler(serializer.asContentHandler());
			this.addNodesBeforeDocument(writer);
			this.xstream.marshal(document, writer);
			this.addNodesAfterDocument(writer);
			return true;
		} catch (final IOException e) {
			de.mxro.utils.log.UserError.singelton.log(e);
			de.mxro.utils.log.UserError.singelton.log("Error while trying to write Document to stream: "+document.getFilename().toString());
			return false;
		}
	}
	
	
}
