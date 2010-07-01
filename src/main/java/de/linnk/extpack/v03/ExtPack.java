package de.linnk.extpack.v03;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

public class ExtPack {
	public static void registerAllAnnotations(XStream xstream) {
		de.linnk.extpack.v02.ExtPack.registerAllAnnotations(xstream);
		
		Annotations.configureAliases(xstream, HyperlinkProxy.class);
		Annotations.configureAliases(xstream, JPEGPictureItem.class);
		Annotations.configureAliases(xstream, PublishItem.class);
		Annotations.configureAliases(xstream, NeverPublishItem.class);
		Annotations.configureAliases(xstream, PublishView.class);
	}
}
