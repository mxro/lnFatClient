package de.linnk.client;

import java.io.IOException;

import de.linnk.domain.Document;
import de.linnk.streaming.DocumentLoader;
import de.mxro.filesystem.File;
import de.mxro.filesystem.Folder;
import de.mxro.utils.AsyncCallback;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.drm.Change;
import de.mxro.utils.drm.ResourceChangeCallback;
import de.mxro.utils.drm.ResourceConnection;

public class LinnkServerConnectionImpl implements LinnkServerConnection {

	final Folder root;
	final URI uri;
	
	
	public void connectResource(String uri,
			AsyncCallback<ResourceConnection<Document>> callback) {
		
		
	}

	public void createResource(String uri, Document resource,
			AsyncCallback<ResourceConnection<Document>> callback) {
		
	}

	
	public URI getRootURI() {
		
		return uri;
	}

	public LinnkServerConnectionImpl(Folder root, URI uri) {
		super();
		this.root = root;
		this.uri = uri;
	}

	
	public boolean addResourceChangeCallback(
			ResourceChangeCallback<Document> resourceChangeCallback) {
		// no callbacks for folders yet ...
		
		return true;
	}

	public boolean acceptResource(String uri) {
		return (uri.indexOf(this.getRootURI().toString()) != -1);
		
	}

	
	public void doChange(String uri, Change<Document> change,
			AsyncCallback<Boolean> callback) {
		
	}

	
	public void undoChange(String uri, Change<Document> change,
			AsyncCallback<Boolean> callback) {
		
	}

	
	public boolean commitResource(String uri) {
		
		return false;
	}
	
	

}
