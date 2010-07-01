package de.linnk.core.v03;

import de.linnk.domain.Updatable;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;

@Deprecated
public class User implements Updatable {

	private final String email;
	private final URI uri;
	
	public static User currentUser = User.newInstance(URIImpl.create("http://www.linnk.de/semantic/linnkuser.rdf#linnkuser"));
	public static User dummyUser = User.newInstance(URIImpl.create("http://www.linnk.de/semantic/linnkuser.rdf#linnkuser"));
	public static User newInstance(final URI uri) {
		return new User(uri);
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public URI getURI() {
		
		return this.uri;
	}
	

	private User(final URI uri) {
		super();
		this.uri = uri;
		this.email = "";
	}
	
	public static User newInstance(final User user) {
		return new User(user.getURI());
	}
	
	public Object update() {
		return new de.linnk.domain.User(uri.toString());
	}

}
