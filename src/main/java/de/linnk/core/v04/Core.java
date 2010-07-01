package de.linnk.core.v04;

import com.thoughtworks.xstream.XStream;

import de.linnk.SimpleLinkConverter;
import de.linnk.basispack.v05.DocumentProxy;
import de.linnk.basispack.v05.LinnkProxy;
import de.linnk.domain.DependsOnItemProxy;
import de.linnk.domain.Document;
import de.linnk.domain.ExchangeItems;
import de.linnk.domain.InsertItem;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.domain.ModifyItem;
import de.linnk.domain.NewItem;
import de.linnk.domain.OwnerItem;
import de.linnk.domain.RemoveItem;
import de.linnk.domain.SimpleLink;
import de.linnk.domain.User;
import de.linnk.domain.Versions;
import de.mxro.xstream.NoReference;



public class Core {
	public static void registerAnnotations(XStream xstream) {
		
		//Linnk.S.setCSSBuilder(ProxyItem.class, new de.mxro.xxsl.EmptyCSSBuilder());
		
		xstream.addImplicitCollection(Document.class, "items", "item", Item.class);
		
		
		
		xstream.alias("v04.documentproxy", DocumentProxy.class);
		xstream.alias("v04.linnkproxy", LinnkProxy.class);
		
		xstream.alias("v04.owneritem", OwnerItem.class);
		
		xstream.alias("v04.exchangeitem", ExchangeItems.class);
		xstream.alias("v04.modifyitem", ModifyItem.class);
		xstream.alias("v04.newitem", NewItem.class);
		xstream.alias("v04.removeitem", RemoveItem.class);
		xstream.alias("v04.insertitem", InsertItem.class);
		
		xstream.alias("v02.dependsonitemproxy", DependsOnItemProxy.class);
		xstream.alias("v02.user", User.class);
		
		xstream.registerConverter(new SimpleLinkConverter());
		xstream.registerLocalConverter(SimpleLink.class, "link", new NoReference());
		
		xstream.addImplicitCollection(Versions.class, "changes", "change", ItemChange.class);
		xstream.alias("v04.versions", Versions.class);
		
		de.linnk.core.v03.Core.registerAnnotations(xstream);
		//Annotations.configureAliases(xstream, ExchangeItems.class);
		//Annotations.configureAliases(xstream, ModifyItem.class);
		//Annotations.configureAliases(xstream, NewItem.class);
		//Annotations.configureAliases(xstream, RemoveItem.class);
		//Annotations.configureAliases(xstream, InsertItem.class);
		//Annotations.configureAliases(xstream, Versions.class);
		//Annotations.configureAliases(xstream, Document.class);
		
	}
}
