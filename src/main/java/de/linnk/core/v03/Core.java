package de.linnk.core.v03;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;





@Deprecated
public class Core {
	
	/**
	 * hulls for Objects to hold old data
	 * @param xstream
	 */
	public static void registerAnnotations(XStream xstream) {
		xstream.alias("v01.user", User.class);
		xstream.registerConverter(new UserConverter());
		
		
		
		xstream.alias("de.linnk.core.v03.OwnerItem", OwnerItem.class);
		xstream.alias("v03.owneritem", OwnerItem.class);
		//de.linnk.maintenance.m01.v02.Core.registerAnnotations(xstream);
		Annotations.configureAliases(xstream, ExchangeItems.class);
		Annotations.configureAliases(xstream, ModifyItem.class);
		Annotations.configureAliases(xstream, NewItem.class);
		Annotations.configureAliases(xstream, RemoveItem.class);
		//Annotations.configureAliases(xstream, Document.class);
		
	}
}
