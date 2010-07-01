package de.linnk.extpack.v02;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

@Deprecated
public class ExtPack {
	public static void registerAllAnnotations(XStream xstream) {		
		Annotations.configureAliases(xstream, PublishItem.class);
	}
}
