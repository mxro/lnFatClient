package de.linnk.core.v03;

import java.net.URISyntaxException;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.mxro.utils.URI;
import de.mxro.utils.log.UserError;

@Deprecated
public class UserConverter implements Converter {

	public void marshal(Object arg0, HierarchicalStreamWriter arg1, MarshallingContext arg2) {
		String writeURI = ((User) arg0).getURI().toString();
		//System.out.println("write: "+writeURI);
		arg1.setValue(  writeURI );	
	}

	public Object unmarshal(HierarchicalStreamReader arg0, UnmarshallingContext arg1) {
		
		String value = arg0.getValue();
		
		// compatibility issues
		if (value.equals("")) {
			UserError.singelton.log(this, "unmarshal: URI for User '"+value+"' had to be reset to currentUser", UserError.Priority.LOW);
			return User.newInstance(User.currentUser);
		}
		
		
		try {
			URI uri = new de.mxro.utils.URIImpl(value);
			if (!uri.isAbsolute()) {
				UserError.singelton.log(this, "unmarshal: URI for User '"+value+"' had to be reset to currentUser", UserError.Priority.LOW);
				return User.newInstance( User.currentUser );
			}
			
			return User.newInstance(de.mxro.utils.URIImpl.create(value)); 
		} catch (URISyntaxException e) {
			// compatibility issues
			UserError.singelton.log(this, "unmarshal: URI for User '"+value+"' had to be reset to currentUser", UserError.Priority.LOW);
			return User.newInstance( User.currentUser );
		}
		
		
	}

	public boolean canConvert(Class arg0) {
		return arg0.equals( User.class);
	}

}
