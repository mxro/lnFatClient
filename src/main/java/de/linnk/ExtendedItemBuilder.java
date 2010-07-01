package de.linnk;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.rtf.RTFEditorKit;

import de.linnk.basispack.v05.DocumentProxy;
import de.linnk.basispack.v05.LinnkProxy;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Copyable;
import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.ItemBuilder;
import de.linnk.domain.ItemChange;
import de.linnk.domain.LinnkConstants;
import de.linnk.domain.SimpleLink;
import de.linnk.domain.User;
import de.linnk.extpack.v03.HyperlinkProxy;
import de.linnk.extpack.v03.JPEGPictureItem;
import de.linnk.extpack.v03.NeverPublishItem;
import de.linnk.extpack.v03.PublishItem;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.streaming.DocumentLoader;
import de.linnk.streaming.LinnkXStream;
import de.linnk.streaming.Publisher;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.filesystem.Folder;
import de.mxro.swing.Image;
import de.mxro.transferable.ClipboardFacade;
import de.mxro.utils.URIImpl;
import de.mxro.utils.Utils;
import de.mxro.utils.gwt.MxroGWTUtils;
import de.mxro.utils.log.UserError;

public class ExtendedItemBuilder extends ItemBuilder {

	public Item newLinnkProxy(Item item, String itemId, SimpleLink link) {
		return new LinnkProxy(this.user, itemId, this.document, item, link);
	}
	
	public Item newLinnkProxy(Item item, SimpleLink link) {
		return newLinnkProxy(item, this.document.getUniqueItemName("LinnkProxy"), link);
	}
	
	public Item newDocumentProxy(Item item, String itemId, SimpleLink link, boolean expanded) {
		return new DocumentProxy(this.user, itemId,  this.document, item, link, expanded);
	}
	
	public Item newDocumentProxy( Item item, SimpleLink link, boolean expanded) {
		return newDocumentProxy(item, this.document.getUniqueItemName("DocumentProxy"), link, expanded);
	}
	
	public ExtendedItemBuilder(Document document, User user) {
		super(document, user);
		
	}
	
	public Item newHyperlinkProxy(Item item, String hyperlink) {
		return new HyperlinkProxy(this.user, this.document.getUniqueItemName("HyperlinkProxy"), this.document, item, hyperlink);
	}
	
	public Item newNeverPublishItem() {
		return new NeverPublishItem(this.user, this.getDocument().getUniqueItemName("NeverPublishItem"), this.document);
	}


    private static boolean isURI(String text) {
        try {
            new java.net.URI(text);
            return true;
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    public Item newItemFromURI(String uri) {
        if (uri.contains("docs.google.com/Doc?id=")) {

            return newHyperlinkProxy(this.newTextItem("Google Document"), uri);
        }
         return newHyperlinkProxy(this.newTextItem(uri), uri);
    }

    public Item newItemFromPlainText(String text) {
        if (isURI(text)) {
            return newItemFromURI(text);
        }
        return newTextItem(text);
    }

	public Item newItemFromFile(java.io.File file) {
		assert file.exists() : "newItemFromFile file must exist";
		FileSystemObject fileObject;
		try {
			// try to "interprete" file
			
			/* import picture */
			final String fileExtension = Utils.getExtension(file.getPath()).toLowerCase();
			if (fileExtension.equals("jpg") ||
					fileExtension.equals("png") ||
					fileExtension.equals("bmp") ||
					fileExtension.equals("gif") ||
					fileExtension.equals("jpeg") ||
					fileExtension.equals("tiff")) {
			
				try {
					return this.newJPEGPictureItem(Image.loadBufferedImage(new FileInputStream(file)));
				} catch (final FileNotFoundException e) {
					UserError.singelton.log(e);
				}
			}
			
			/* import rtf-File */
			if (fileExtension.equals("rtf")) {
				try {
					final FileInputStream stream = new FileInputStream(file);
					final RTFEditorKit kit = new RTFEditorKit();
					final javax.swing.text.Document doc = kit.createDefaultDocument();
					kit.read(stream, doc, 0);
					final String plainText = doc.getText(0, doc.getLength());
					
					final SimpleLink rtfLink = DocumentBuilder.instance.createEmptyChildDocument((NodeDocument) this.getDocument(), LinnkFatClient.currentUser, Utils.removeExtension(file.getName()));
					final Document rtfDoc = DocumentLoader.singelton.loadFromFile(((NodeDocument) this.getDocument()).getFolder().getFile(rtfLink.toURI()));
					final ItemChange add = ItemChange.newNewItem(new ItemBuilder(rtfDoc, LinnkFatClient.currentUser).newTextItem(plainText), this.user);
					rtfDoc.doChange(add);
					Linnk.S.saveDocument(rtfDoc);
					
					
					final Item link = this.newLinnkProxy(this.newTextItem(Utils.removeExtension(file.getName())), rtfLink);
					//this.getDocument().doChange(addLink);
					return link;
				} catch (final FileNotFoundException e) {
					de.mxro.utils.log.UserError.singelton.log(e);
				} catch (final IOException e) {
					de.mxro.utils.log.UserError.singelton.log(e);
				} catch (final BadLocationException e) {
					de.mxro.utils.log.UserError.singelton.log(e);
				}
			}
			
			/* import Linnk-Document */
			if (fileExtension.equals("linnk") ||
					fileExtension.equals("xml")) {
				final Document doc = DocumentLoader.singelton.loadFromJavaFile(file);
				if (doc != null) {
					// try to import complete document structure
					try {
						final Folder newFolder = ((NodeDocument) this.getDocument()).getFolder().createFolder(((NodeDocument) this.getDocument()).getFolder().getUniqueFileName(doc.getSimpleName()));
						if (newFolder != null) {
							final Folder docFolder = newFolder.importFolder(((NodeDocument) doc).getFolder());
							if (docFolder != null) {
								final Item textItem = this.newTextItem(doc.getName());
								final SimpleLink link =  new SimpleLink(newFolder.getName()+"/"+doc.getFilename());
								final Item linnkItem = this.newDocumentProxy(textItem,link, false);
								// add links item - very senseful to do it automatically here
								//final Item links = linnkItem.getDocument().newLinksItem( User.currentUser, link);
								
								//final NewItem newItem = ItemChange.newNewItem(linnkItem.getDocument().newDependsOnItemProxy(User.currentUser, linnkItem, links), User.currentUser, ItemChange.Type.IMPLICIT);
								//linnkItem.getDocument().doChange(newItem);
								
								final Document importedDoc = DocumentLoader.singelton.loadFromFile(((NodeDocument) this.getDocument()).getFolder().getFile(link.toURI()));
								if (importedDoc != null) {
									importedDoc.setOwner(this.user, "../"+this.getDocument().getFilename());
									Linnk.S.saveDocument(importedDoc);
									
								} else {
									de.mxro.utils.log.UserError.singelton.log("imported Document could not be loader!");
								}
								return linnkItem;
							} else {
								de.mxro.utils.log.UserError.singelton.log("Error while trying to import files from: "+((NodeDocument) doc).getFolder().getURI().toString());
							}
						} else {
							de.mxro.utils.log.UserError.singelton.log("Folder "+doc.getSimpleName()+" could not be created in folder "+((NodeDocument) this.getDocument()).getFolder().getURI().toString());
						}
					} catch (final IOException e) {
						de.mxro.utils.log.UserError.singelton.log(e);
						de.mxro.utils.log.UserError.singelton.log("Error while trying to import folder for "+file.getAbsolutePath());
					}		
				} else {
					de.mxro.utils.log.UserError.singelton.log("Error while trying to import Linnk-Document: "+file.getAbsolutePath());
				}
			}
			
			// .. in case of an error just import a "raw" file
			
			final String filename = ((NodeDocument) this.getDocument()).getFolder().getUniqueFileName(MxroGWTUtils.getSimpleName(file.getName()));
			
			fileObject = ((NodeDocument) this.getDocument()).getFolder().importFile(URIImpl.fromFile(file), filename);
			if (fileObject==null)
				return null;
			
			final Item textItem = this.newTextItem(file.getName());
			return this.newHyperlinkProxy(textItem, fileObject.getName());
		} catch (final URISyntaxException e) {
			de.mxro.utils.log.UserError.singelton.showError("Error importing files: Invalid URI Syntax.", e);
		}
		return null;
		
	}
	
	public Item newPublishItem( Publisher publisher ) {
		return new PublishItem(this.user, this.getDocument().getUniqueItemName("PublishItem"), this.document, publisher);
	}
	
	public Item newJPEGPictureItem(BufferedImage image) {
		return new JPEGPictureItem(this.user, this.document.getUniqueItemName("ImageItem"), this.document, image);
		/*String newItemName = document.getUniqueItemName("Image");
		String newFileName = document.getFolder().getUniqueFileName(newItemName+".jpg");
		File newFile = document.getFolder().createFile(newFileName);
		if (newFile != null) {
			if (Image.saveImage(image, newFile.getOutputStream(), "jpg")) {
				return newJPEGPictureItem(newFileName);
			}
			
		} else {
			de.mxro.UserError.singelton.log("newJPEGPictureItem failed ");
		}
		return null;*/
	}
	
	private final static Item fromString(String s, Document newDocument) {
		final int idx = s.indexOf(LinnkConstants.ITEMIDENTIFIER);
		if (idx >= 0) {
			final String content = s.substring(idx + LinnkConstants.ITEMIDENTIFIER.length());
			final Item res = (Item) LinnkXStream.singelton.fromXML(content);
			if (!(res instanceof Copyable)) {
				de.mxro.utils.log.UserError.singelton.log("Item Class "+res.getClass().getName()+" cannot be created from a string. It must implement interface Copyable.");
			}
			res.afterFromString(newDocument);
			
			
			return res;
		}
		return null;
	}
	
	public Item newItemFromClipboard() {
		if (ClipboardFacade.getImage() != null)
			return this.newJPEGPictureItem(ClipboardFacade.getImage()); 
		
		return null;
	}
	
	public Item newItemFromString(String s) {
		final Item item = fromString(s, this.document);
		if (item == null) {
			de.mxro.utils.log.UserError.singelton.log("ItemBuilder: Could not create item from String '"+s+"'");
		}
		return item;
	}
	
	
}
