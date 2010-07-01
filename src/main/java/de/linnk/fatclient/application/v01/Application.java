package de.linnk.fatclient.application.v01;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

@Deprecated
public class Application {
	

public static void registerAllAnnotations(XStream xstream) {
		
		
		
		Annotations.configureAliases(xstream, Settings.class);
	}
}
