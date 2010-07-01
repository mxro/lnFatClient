package de.linnk.streaming;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;
import de.linnk.fatclient.application.LinnkAlias;
import de.mxro.filesystem.File;
import de.mxro.filesystem.FileSystem;
import de.mxro.filesystem.FileSystemAddressMapper;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.filesystem.Folder;
import de.mxro.filesystem.ext.LocalFileSystem;
import de.mxro.filesystem.ext.VirtualRealAddressMapper;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;



public class DocumentLoader {
	
	public NodeDocument loadForDocument(NodeDocument document, URI uri) {
		// determine file
		File file = document.getFolder().getFile(uri);
		if (file == null) {
			de.mxro.utils.log.UserError.singelton.log(this, "Link URI '"+uri+"' cannot be loaded\n" +
					" in Folder '"+uri, UserError.Priority.HIGH);
			return null;
		}
		
		NodeDocument doc = DocumentLoader.singelton.loadFromFile(file);
		if (doc == null) {
			de.mxro.utils.log.UserError.singelton.log(this, "Document '"+file.getURI()+"' cannot be loaded\n" +
					" in Folder '"+uri, UserError.Priority.HIGH);
		}
		return doc;
	}
	
	public NodeDocument loadFromFile(File file) {
		if (file == null) throw new IllegalArgumentException("file must not be null.");
		NodeDocument doc=null;	
		
		UserError.singelton.log(this, "loadFromFile: mxro File: '"+file.getURI().toString()+"'\n" +
				" in folder "+file.getOwner().getURI().toString()+"'", UserError.Priority.INFORMATION);
		
		if (de.mxro.utils.Utils.getExtension(file.getName()).equals( "xml")) {
			doc = (NodeDocument) DocumentStreamer.singelton.readFromStream(file.getInputStream() );
			if (doc == null) {
				UserError.singelton.log(this, "loadFromFile: Invalid Format for file: "+file.getURI(), UserError.Priority.HIGH);
				return null;
			}
			
			UserError.singelton.log(this, "loadFromFile: Document streamed from URI: '"+doc.getUniqueURI()+"'"
					, UserError.Priority.INFORMATION);
			
			final File ownFile = file;
			assert (ownFile != null);
			doc.setFilename(ownFile.getName());
			// check whether document needs special file system
			
			FileSystem fileSystem = file.getFileSystem();
			if (!doc.getUniqueURI().equals(ownFile.getURI().toString())) {
				fileSystem =new FileSystemAddressMapper(LocalFileSystem.singelton, 
					new VirtualRealAddressMapper(URIImpl.create(doc.getUniqueURI()).getFolder(), ownFile.getURI().getFolder()));
				UserError.singelton.log(this, "loadFromFile: creates virtual file system '"+doc.getUniqueURI()+"'"
						, UserError.Priority.INFORMATION);
			}
				
				URI folderURI = ownFile.getURI().getFolder();
				if (doc.getUniqueURI() != null && !doc.getUniqueURI().toString().equals("")) {
					folderURI = URIImpl.create(doc.getUniqueURI()).getFolder();
				}
				
				Folder documentFolder = fileSystem.getFolder(folderURI);
				if (documentFolder ==  null) {
					UserError.singelton.log(this, "loadFromFile: Folder '"+folderURI.toString()+"' cannot be found.", UserError.Priority.HIGH);
					
				}
//				Folder documentFolder = new LocalRootFolder(
//						folderURI, mapperSystem
//						);
			//}
			if (documentFolder != null) {
				doc.setFolder(documentFolder);
				UserError.singelton.log(this, "loadFromFile: Document loaded in folder "+documentFolder.getURI().toString()+"'", UserError.Priority.INFORMATION);
			}
			return doc;
		} else
		if (de.mxro.utils.Utils.getExtension(file.getName()).equals( "linnk")) {
			de.mxro.utils.log.UserError.singelton.log("DocumentLoader.loadFromFile: Tries to open alias: "+file.getName());
			final LinnkAlias la = LinnkAlias.fromStream( file.getInputStream() );
			//System.out.println(file.getOwner().getURI());
			final File destination = file.getOwner().getFile(URIImpl.create(la.getDestination()));
			if (destination == null) {
				de.mxro.utils.log.UserError.singelton.log(this, 
						"loadFromFile: Could not resolve Linnk alias: "+la.getDestination()+" in "+file.getOwner().getURI(), UserError.Priority.LOW);
				return null;
			}
			return this.loadFromFile(destination);
			//doc = la.getDocument(file.getOwner());
			
		} else {
			de.mxro.utils.log.UserError.singelton.log("Unkown extension at file name: "+file.getName());
			return null;
		}
		
		/*if (doc == null) {
			UserError.singelton.log(this, "loadFromFile: Could not load Document: "+file.getURI(), UserError.Priority.HIGH);
			return null;
		}*/	
	}
	
	public NodeDocument loadFromJavaFile(java.io.File file) {
		if (!file.exists()) {
			UserError.singelton.log(this, "DocumentLoader.loadFromJavaFile: File "+file.getAbsolutePath()+" does not exist!", UserError.Priority.HIGH);
			return null;
		}
		
//		FileSystem mapperSystem =new FileSystemAddressMapper(LocalFileSystem.singelton, 
//        	new VirtualRealAddressMapper(URI.create(doc.getUniqueURI()).getFolder(), ownFile.getURI().getFolder()));
		
		return this.loadFromFile(FileSystemObject.newLocalFile(file));
		
	}
	
	public Document loadFromFile(URI uri) {
		return this.loadFromJavaFile(uri.getFile());
	}
	
	
//	public Document loadFromLocalLink(LocalLink link) {
//		//UserError.singelton.log("DocumentLoader.loadFromLink "+link.getLocalURI().toString());
//		final URI localURI = link.getLocalURI();
//		if (localURI == null) return null;
//		return this.loadFromFile(localURI);
//	}
	
//	public Document loadFromLink(Link link) {
//		
//		
//		if (link instanceof LocalLink)
//			return this.loadFromLocalLink((LocalLink) link);
//		
//		throw new NotYetSupportedException();
//	}
	
	public static DocumentLoader singelton = new DocumentLoader();
	
}
