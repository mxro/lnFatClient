package de.linnk.style;

import de.linnk.DocumentBuilder;
import de.linnk.domain.Document;
import de.linnk.streaming.views.PlainXMLView;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.filesystem.Folder;


public class Tests {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ByteArrayOutputStream css = new ByteArrayOutputStream();
		
		StreamResult streamResult = new StreamResult(bs);
		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
	    try {
			TransformerHandler hd = tf.newTransformerHandler();
			hd.setResult(streamResult);
			Document doc = DocumentBuilder.exampleDoc;
			DocumentStyleWriter ds = new DefaultDocumentStyleWriter( doc);
			
			ds.writeXSL(hd);
			ds.writeCSS(css);
	    } catch (TransformerConfigurationException e) {
	    	UserError.singelton.showError(e.getLocalizedMessage());
	    	e.printStackTrace();
	    	throw new SAXException("TransformerConfigurationException");
	    }
		
		
		System.out.println(bs.toString());*/
		final Document doc = DocumentBuilder.exampleDoc;
		final Folder currentDir = FileSystemObject.currentDir();
		final PlainXMLView view = new PlainXMLView();
		view.writeView(doc, currentDir);
		// System.out.println("with Css:");
		// System.out.println(css.toString());
	}

}
