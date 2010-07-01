package de.linnk.client;

import de.linnk.domain.Document;
import de.mxro.utils.AsyncCallback;
import de.mxro.utils.URI;
import de.mxro.utils.drm.ResourceChangeCallback;
import de.mxro.utils.drm.ResourceConnection;
import de.mxro.utils.drm.ResourceManager;
import de.mxro.utils.drm.ServerConnection;

public interface LinnkServerConnection extends ServerConnection<Document> {

	/**
	 * returns the root URI for which this server is responsible
	 * 
	 * @return
	 */
	public URI getRootURI();
	
	
	public void connectResource(String uri,
			AsyncCallback<ResourceConnection<Document>> callback);

	
	public void createResource(String uri, Document resource,
			AsyncCallback<ResourceConnection<Document>> callback);
	
	
	public boolean addResourceChangeCallback(ResourceChangeCallback<Document> resourceChangeCallback);

}
