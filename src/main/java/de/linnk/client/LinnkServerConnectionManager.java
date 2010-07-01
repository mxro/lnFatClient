package de.linnk.client;

import java.net.URI;
import java.util.Vector;

import de.linnk.domain.Document;
import de.mxro.utils.AsyncCallback;
import de.mxro.utils.drm.ConnectionManager;
import de.mxro.utils.drm.ResourceConnection;

public class LinnkServerConnectionManager implements ConnectionManager<Document> {

	private final Vector<LinnkServerConnection> servers;
	
	public void registerServer(LinnkServerConnection server) {
		servers.add(server);
	}
	
	
	public void connectResource(String foruri, AsyncCallback<ResourceConnection<Document>> callback) {
		
		for (LinnkServerConnection server : servers) {
			if (server.acceptResource(foruri)) {
		
				server.connectResource(foruri, callback);
				return;
			}
		}
		
	}

	public LinnkServerConnectionManager() {
		super();
		servers = new Vector<LinnkServerConnection>();
	}

	
	public void createResource(String foruri, Document resource,
			AsyncCallback<ResourceConnection<Document>> callback) {
		
		for (LinnkServerConnection server : servers) {
			if (server.acceptResource(foruri)) {
				server.createResource(foruri, resource, callback);
				return;
			}
		}
	}
	
	
}
