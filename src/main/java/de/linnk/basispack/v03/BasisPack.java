package de.linnk.basispack.v03;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

@Deprecated
public class BasisPack {
	public static void registerAllAnnotations(XStream xstream) {
		
		//de.linnk.maintenance.m01.v02.BasisPack.registerAllAnnotations(xstream);
	
		Annotations.configureAliases(xstream, LinnkProxy.class);
		Annotations.configureAliases(xstream, DocumentProxy.class);
		Annotations.configureAliases(xstream, LinnkDocument.class);
		
	}
}
