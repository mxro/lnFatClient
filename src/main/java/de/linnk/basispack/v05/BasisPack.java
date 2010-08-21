package de.linnk.basispack.v05;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

import de.linnk.domain.DependsOnItemProxy;
import de.linnk.domain.TextItem;
import de.linnk.domain.TitleItem;
import de.linnk.streaming.ConvertXML;





public class BasisPack {
	public static void registerAllAnnotations(XStream xstream) {
		
		xstream.alias("v02.textitem", TextItem.class);
		xstream.alias("v01.titleitem", TitleItem.class);
		
		
		xstream.registerLocalConverter(TextItem.class, "textXML", new ConvertXML());
		
		//xstream.addImplicitCollection(ExpandedLinnk.class, "items", "item", Item.class);
		
		de.linnk.basispack.v03.BasisPack.registerAllAnnotations(xstream);
		de.linnk.basispack.v04.BasisPack.registerAllAnnotations(xstream);
		
		Annotations.configureAliases(xstream, DependsOnItemProxy.class);
		//Annotations.configureAliases(xstream, DocumentProxy.class);
		Annotations.configureAliases(xstream, LinnkProxy.class);
		
		
		Annotations.configureAliases(xstream, NoStyleProxy.class);
		Annotations.configureAliases(xstream, NodeDocument.class);
		//Annotations.configureAliases(xstream, ExpandedLinnk.class);
		//Annotations.configureAliases(xstream, OwnerItem.class);
		Annotations.configureAliases(xstream, TitleItem.class);
	}
}
