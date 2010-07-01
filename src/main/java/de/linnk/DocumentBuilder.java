package de.linnk;

import java.net.URISyntaxException;
import java.util.Date;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.ItemBuilder;
import de.linnk.domain.ItemChange;
import de.linnk.domain.SimpleLink;
import de.linnk.domain.TextItem;
import de.linnk.domain.User;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.streaming.DocumentLoader;
import de.linnk.streaming.views.PlainXMLView;
import de.mxro.filesystem.File;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.filesystem.Folder;
import de.mxro.filesystem.v01.IncludedRootFolder;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;


public class DocumentBuilder {
	public static Document exampleDoc; 
	
	
	static IncludedRootFolder includedRootFolder = IncludedRootFolder.createInstance();
	static Folder localRootFolder ;
	static Date now = new Date();
	public static String documentExtension = ".xml";
	
	public static DocumentBuilder instance = new DocumentBuilder();
	
	public static NodeDocument newRootDocument(URI documentURI, User creator, Folder folder) {
			
		de.mxro.utils.log.UserError.singelton.log("DocumentBuilder.newRootDocument: Create Document:\n" +
				" documentURI = '"+documentURI.toString()+"'\n" +
						" folder.getURI = '"+folder.getURI()+"'", UserError.Priority.INFORMATION);
		
			final Folder inFolder = folder;
			final Document res = DocumentBuilder.emptyDocument(de.mxro.utils.Utils.removeExtension(documentURI.getFileName()), creator, inFolder);
			res.setUniqueURI(documentURI.toString());
			final File docFile = inFolder.createFile(inFolder.getUniqueFileName(res.getSimpleName())+DocumentBuilder.documentExtension);
			
			if (docFile == null) {
				de.mxro.utils.log.UserError.singelton.log("DocumentBuilder.newRootDocument: Document "+documentURI+" could not be created.", UserError.Priority.HIGH);
				de.mxro.utils.log.UserError.singelton.showError("Error while creating document. Document file could not be created");
				return null;
			}
			de.mxro.utils.log.UserError.singelton.log("DocumentBuilder.newRootDocument: Document file created at:\n" +
					" URI = '"+docFile.getURI().toString()+"'", UserError.Priority.INFORMATION);
			
				res.setFilename(docFile.getName());
				
				if (new PlainXMLView().writeView(res, inFolder) == null) {
					de.mxro.utils.log.UserError.singelton.log("DocumentBuilder: Document "+documentURI+" could not be created. XMLView could not be written", UserError.Priority.HIGH);
					de.mxro.utils.log.UserError.singelton.showError("Error while creating document. Document file could not be created");
					return null;
				}
					
			final NodeDocument loadedDocument = DocumentLoader.singelton.loadFromFile(docFile);
			if (loadedDocument == null) {
				UserError.singelton.log("DocumentBuilder: Could not load document from file '"+docFile.getURI().toString(), UserError.Priority.HIGH);
				UserError.singelton.showError("New Document could not be loaded.");
			}
			
			return loadedDocument;
		
		
	}
	
	public static Document emptyDocument(String name, User creator, Folder inFolder) {
		final Document res = new NodeDocument(creator, inFolder, name);
		
			//res.setFile(inFolder.getFile(new URI(name+documentExtension)));
			//System.out.println(res.getFile());
			return res;
		
	}
	
	protected static Document emptyDocument() {
		
		final Document doc = new NodeDocument(LinnkFatClient.currentUser, localRootFolder, "testdokument");
		
		return doc;
	}
	
	/*public static ItemChange addTextItem(Document doc, String id, String text) {
		TextItem ti = Item.newTextItem(max, id, doc, text);
		NewItem ni = ItemChange.newNewItem(ti, max);
		doc.doChange(ni);
		return ni;
	}*/
	
	static {
		
		try {
			localRootFolder = FileSystemObject.newLocalRootFolder(URIImpl.fromFile(new java.io.File("").getAbsoluteFile()));
		} catch (final java.net.URISyntaxException e) { e.printStackTrace(); }
		
		exampleDoc = emptyDocument();
		//addTextItem(exampleDoc, "123", "Mein Text mit ܆mlauten <und> Sonderzeichen </und>");
	}
	
	public SimpleLink createEmptyChildDocument(NodeDocument forDoc, User user, String name) {
		assert !name.equals( "" );
		
		String filename = name;
		
		if (filename.length() > 16) {
			filename = filename.substring(0, 15);
		}
		String simpleName = Document.getSimpleName(filename);
		final String newFolderName = forDoc.getFolder().getUniqueFileName(simpleName);
		
		Folder newdocfolder;
		
		newdocfolder = forDoc.getFolder().createFolder(newFolderName);
		
		// UserError.singelton.log(newdocfolder.toString()); 
		if (newdocfolder == null) {
			UserError.singelton.log("DocumentBuilder.createEmptyChildDocument: Could not create folder:\n" +
					" folder '"+newFolderName+"'/n" +
					" at '"+forDoc.getFolder().getURI().toString(), UserError.Priority.HIGH);
			return null;
		}
		
		final NodeDocument doc = new NodeDocument(LinnkFatClient.currentUser, newdocfolder, name);
			
		
		try {
			doc.setUniqueURI(URIImpl.create(forDoc.getUniqueURI()).getFolder().addFolderToFolder(newFolderName).addFileToFolder(simpleName).toString()+DocumentBuilder.documentExtension);
			//System.out.println(newdocfolder.getURI().addFileToFolder(name));
		} catch (URISyntaxException e) {
			UserError.singelton.log("DocumentBuilder.createEmptyChildDocument: Illegal URI name: "+name, UserError.Priority.HIGH);
			UserError.singelton.log(e);
			return null;
		}
		
		final Item textItem = new ItemBuilder(doc, LinnkFatClient.currentUser).newTextItem("enter text here...");
		final ItemChange itemChange = ItemChange.newNewItem(textItem, LinnkFatClient.currentUser, ItemChange.Type.REVERSIBLE);
		doc.doChange(itemChange);
		
		final File docfile = newdocfolder.createFile(newdocfolder.getUniqueFileName(simpleName+documentExtension));
		if (docfile == null) {
			de.mxro.utils.log.UserError.singelton.showError("Error creating child document. File could not be created!");
			return null;
		}
		doc.setFilename(docfile.getName());
		
		doc.setOwner(user, "../"+forDoc.getFilename());
		
		//doc.save();
		final PlainXMLView xmlview = new PlainXMLView();
		xmlview.writeView(doc, doc.getFolder());
		
		de.mxro.utils.log.UserError.singelton.log(this, "Created document: uri " +doc.getUniqueURI()+
				                  " path "+newFolderName+"/"+docfile.getName()+"\n"+
		                          " absolute Path: "+docfile.getURL().toString(), UserError.Priority.INFORMATION);
		return new SimpleLink(newFolderName+"/"+docfile.getName());	
		
		
	}
	
	public SimpleLink createDocumentForItem(Item item, User user) {
		if (!(item.getDocument() instanceof NodeDocument)) {
			de.mxro.utils.log.UserError.singelton.log(this, "Can only create new document for LinnkDocuments", UserError.Priority.HIGH);
			return null;
		}
		
		if (item == null) {
			de.mxro.utils.log.UserError.singelton.log("DocumentBuilder.createDocumentForItem: Item must not be null.", UserError.Priority.HIGH);
			return null;
		}
		
		String title;
		if (item instanceof TextItem) {
			title =  de.mxro.utils.Utils.removeMarkup( ((TextItem) item).getTextData());
			if (title.length() > 80) {
				title = title.substring(0, 79);
			}
		} else {
			
			title = item.getId();
		}
		
		return DocumentBuilder.instance.createEmptyChildDocument((NodeDocument) item.getDocument(), LinnkFatClient.currentUser, title);
	}
}
