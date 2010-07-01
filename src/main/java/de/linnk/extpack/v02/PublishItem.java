package de.linnk.extpack.v02;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.Updatable;
import de.linnk.domain.User;
import de.linnk.extpack.v03.PublishView;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.streaming.views.PlainXMLView;
import de.linnk.streaming.views.RWView;

@Deprecated
@XStreamAlias("v01.ext.publishitem")
public class PublishItem extends Item implements Updatable {
	
	public Publisher publisher;

	public Object update() {
		//return null;
		return new de.linnk.extpack.v03.PublishItem(creator, id, document, 
				new de.linnk.streaming.Publisher( new PublishView(new RWView(new PlainXMLView(), true, LinnkFatClient.application.getDefaultTemplate(), false)), publisher.deleteXML, publisher.destination, null ,publisher.isUnchanged));
	}

	public PublishItem(User creator, String id, Document document) {
		super(creator, id, document);
		
	}
	
	
	
	
}
