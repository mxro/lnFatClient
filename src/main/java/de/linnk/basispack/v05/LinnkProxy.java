package de.linnk.basispack.v05;

import de.linnk.ExtendedItemBuilder;
import de.linnk.domain.Document;
import de.linnk.domain.EasyEditItem;
import de.linnk.domain.Item;
import de.linnk.domain.ProxyItem;
import de.linnk.domain.SimpleLink;
import de.linnk.domain.User;
import de.mxro.utils.URIImpl;
import de.mxro.utils.distributedtree.Linking;
import de.mxro.utils.domain.Styled;
import de.mxro.utils.log.UserError;


public class LinnkProxy extends ProxyItem implements Styled, EasyEditItem, Linking<LinnkProxy> {
	
	protected SimpleLink link;
	
	private boolean expanded=false;
	
	public SimpleLink getLink() {
		return this.link;
	}

	public LinnkProxy(User creator, String id, Document document, Item item, SimpleLink link) {
		this(creator, id, document, item, link, false);
	}
	
	public LinnkProxy(User creator, String id, Document document, Item item, SimpleLink link, boolean expanded) {
		super(creator, id, document, item);
		this.link = new SimpleLink(link);
		this.expanded = expanded;
	}

	

	@Override
	public void afterToString() {
		super.afterToString();
		this.link = SimpleLink.fromURI(((NodeDocument) this.getDocument()).getFolder().relativizeURI(this.link.toURI()));
	}

	@Override
	public void beforeToString() {
		this.link = SimpleLink.fromURI(( (NodeDocument) this.getDocument()).getFolder().resolveURI(this.link.toURI()));
		super.beforeToString();
	}
	
	

	/*public Item createExpandedLinnk() {
		return new ExpandedLinnk(this.creator, this.getId(), this.getDocument(), this);
	}*/

	public boolean isExpanded() {
		return this.expanded;
	}

	public de.mxro.utils.distributedtree.Linking.LinkVector getLinkVector() {
		de.mxro.utils.distributedtree.Linking.LinkVector lV = new de.mxro.utils.distributedtree.Linking.LinkVector();
		lV.add(this.getLink().toURI().toString());
		return lV;
	}

	public LinnkProxy newInstanceFromLinkVector(
			de.mxro.utils.distributedtree.Linking.LinkVector vector) {
		if (!this.getClass().equals(LinnkProxy.class)) {
			UserError.singelton.log(this, "newInstanceFromLinkVector: has to be implemented for class "+this.getClass(), UserError.Priority.HIGH);
			return null;
		}
		
		return (LinnkProxy) new ExtendedItemBuilder(this.getDocument(), this.getCreator()).newLinnkProxy((this).getItem(), SimpleLink.fromURI(URIImpl.create(vector.firstElement())));
	}

	public boolean isLinkOwner() {
		return false;
	}
	
	
	
}
