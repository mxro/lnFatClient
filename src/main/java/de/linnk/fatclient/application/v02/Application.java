package de.linnk.fatclient.application.v02;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

public class Application {
	public static void registerAllAnnotations(XStream xstream) {
		
		de.linnk.fatclient.application.v01.Application.registerAllAnnotations(xstream);
		
		Annotations.configureAliases(xstream, Settings.class);
	}
}
