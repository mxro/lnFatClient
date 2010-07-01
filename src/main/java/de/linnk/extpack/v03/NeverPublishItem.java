package de.linnk.extpack.v03;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.User;

@XStreamAlias("v01.ext.neverpublishitem")
public class NeverPublishItem extends Item {

	public NeverPublishItem(User creator, String id, Document document) {
		super(creator, id, document);
	}
	
	
}
