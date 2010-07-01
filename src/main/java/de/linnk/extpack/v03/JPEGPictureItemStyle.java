package de.linnk.extpack.v03;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.style.ItemStyle;
import de.mxro.filesystem.Folder;
import de.mxro.utils.URI;
import de.mxro.xml.MyContentHandler;

public class JPEGPictureItemStyle extends ItemStyle<JPEGPictureItem> {


	public JPEGPictureItemStyle(JPEGPictureItem item) {
		super(item);
	}

	@Override
	public void writeItemXML(MyContentHandler hd, Folder files, String path)
			throws SAXException {
		
		de.mxro.xstream.XMLUtils.startDivElement(hd, "jpegpictureitem");
		
		hd.startElement("", "", "img", null);
		
		final AttributesImpl atts = new AttributesImpl();
		
		/* link image */
		//Folder images;
		
		//	images = files.getFolder(new URI("export_images"));
		///		images = files.createFolder("export_images");
		//	}
		//	final String newFileName = FileSystemObject.getSimpleName(this.item.getId())+".png";
		//	final File newFile = images.forceFile(newFileName);
			URI imageURIcomplete = this.item.getImageURI();
			URI imageURIrelToDoc = ((NodeDocument) this.item.getDocument()).getFolder().getURI().relativize(imageURIcomplete);
			
			
			// UserError.singelton.log("image "+((JPEGPictureItem) item).getImage()+" outputstream: "+newFile.getOutputStream());
			//if (Image.saveImage(((JPEGPictureItem) this.item).getImage(), newFile.getOutputStream(), "png")) {
				(this.item).freeImage();
				atts.addAttribute("", "", "name", "CDATA", "src");
				hd.startElement("", "", "xsl:attribute", atts);
				//hd.startCDATA();
				
				hd.characters((imageURIrelToDoc.toString()).toCharArray(), 0, (imageURIrelToDoc.toString()).toCharArray().length);
				//hd.endCDATA();
				hd.endElement("", "", "xsl:attribute");
		//	}
			
		
		
		
		
		
		
		hd.endElement("", "", "img");
		
		de.mxro.xstream.XMLUtils.endDivElement(hd);

	}

}
