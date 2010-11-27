package de.linnk.extpack.v03;

import java.awt.image.BufferedImage;
import java.net.URISyntaxException;

import mx.gwtutils.MxroGWTUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Copyable;
import de.linnk.domain.Document;
import de.linnk.domain.EasyEditItem;
import de.linnk.domain.Item;
import de.linnk.domain.User;
import de.mxro.filesystem.File;
import de.mxro.filesystem.v01.IncludedFile;
import de.mxro.filesystem.v01.IncludedRootFolder;
import de.mxro.swing.Image;
import de.mxro.utils.URI;
import de.mxro.utils.domain.Styled;
import de.mxro.utils.log.UserError;

@XStreamAlias("v01.ext.jpegpictureitem")
public class JPEGPictureItem extends Item implements Styled, EasyEditItem, Copyable {

	private IncludedFile imageData;
	private transient boolean pngCreated = false;
	
	transient private BufferedImage bufferedImage=null;
	
	public JPEGPictureItem(User creator, String id, Document document, BufferedImage image) {
		super(creator, id, document);
		this.bufferedImage = image;
		try {
			this.imageData = new IncludedFile("image", IncludedRootFolder.createInstance());
		} catch (URISyntaxException e) {
			UserError.singelton.log(e);
		}
		de.mxro.swing.Image.saveImage(this.bufferedImage, this.imageData.getOutputStream(), "png");
		// bufferedImage.copyData(image.getRaster());
	}
	
	/**
	 * release the memory for the image ...
	 *
	 */
	public void freeImage() {
		this.bufferedImage = null;
	}
	
	public BufferedImage getImage() {
		if (this.bufferedImage == null) {
			try {
				this.bufferedImage = de.mxro.swing.Image.loadBufferedImage(this.imageData.getInputStream());
			} catch (java.lang.OutOfMemoryError e) {
				this.bufferedImage = new BufferedImage(0,0,BufferedImage.TYPE_INT_RGB);
				UserError.singelton.log(e);
				UserError.singelton.log("JPEGPictureItem: Not enough java heap space.", UserError.Priority.HIGH);
			}
		}
		return this.bufferedImage;
		
	}
	
	/** uri to the picture as png file. relative to the document that the document
	 * that the item is contained in.
	 * 
	 * if the png file does not exists it is created when the method is called.
	 * 
	 * @return
	 */
	public URI getImageURI() {
		
		File imageFile = ((NodeDocument) this.getDocument()).getDocAttachmentsFolder().forceFile(MxroGWTUtils.getSimpleName(this.getId())+".png");
	
		if (pngCreated) {
			return imageFile.getURI();
		}
		
		if (!Image.saveImage((this).getImage(), imageFile.getOutputStream(), "png")) {
			return null;
		}
		
		return imageFile.getURI();
	}
	

	public void afterInsert() {
		
		
	}

	public void afterToString() {
		
		
	}

	public void beforeToString() {
		
		
	}
	
	
}
