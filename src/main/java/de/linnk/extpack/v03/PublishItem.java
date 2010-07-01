package de.linnk.extpack.v03;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Copyable;
import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.User;
import de.linnk.streaming.Publisher;
import de.mxro.utils.domain.Styled;

@XStreamAlias("v02.ext.publishitem")
public class PublishItem extends Item implements Styled, Copyable {
	
	private final Publisher publisher;
	
	public final Publisher getPublisher() {
		this.publisher.setDocument((NodeDocument) this.getDocument());
		return this.publisher;
	}


	public void afterInsert() {
		
	}

	public void afterToString() {
	
	}

	public void beforeToString() {
		
	}

	public PublishItem(User creator, String id, Document document, final Publisher publisher) {
		super(creator, id, document);
		this.publisher = publisher;
	}
	
	
}
