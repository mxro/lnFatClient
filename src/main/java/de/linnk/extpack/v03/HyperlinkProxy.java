package de.linnk.extpack.v03;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.linnk.ExtendedItemBuilder;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;
import de.linnk.domain.EasyEditItem;
import de.linnk.domain.Item;
import de.linnk.domain.ProxyItem;
import de.linnk.domain.User;
import de.linnk.fatclient.application.LinnkFatClient;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.distributedtree.Linking;
import de.mxro.utils.log.UserError;

@XStreamAlias("v01.ext.hyperlinkproxy")
public class HyperlinkProxy extends ProxyItem implements de.mxro.utils.domain.Styled, EasyEditItem, Linking<HyperlinkProxy> {
	
	private String hyperlink;
	private boolean isLinkOwner = true;
	
	public HyperlinkProxy(User creator, String id, Document document, Item item, String hyperlink) {
		super(creator, id, document, item);
		this.hyperlink = hyperlink;
	}

	public String getHyperlink() {
		return this.hyperlink;
	}


	@Override
	public void afterToString() {
		super.afterToString();
		
		this.hyperlink = ((NodeDocument) this.getDocument()).getFolder().relativizeURI(URIImpl.create(this.hyperlink)).toString();
		
	}

	@Override
	public void beforeToString() {
		super.beforeToString();
		final URI newURI = URIImpl.create(this.hyperlink);
		if (newURI == null) return;
		this.hyperlink = ((NodeDocument) this.getDocument()).getFolder().resolveURI(newURI).toString();
		
	}

	public de.mxro.utils.distributedtree.Linking.LinkVector getLinkVector() {
		de.mxro.utils.distributedtree.Linking.LinkVector lV = new de.mxro.utils.distributedtree.Linking.LinkVector();
		lV.add(this.getHyperlink());
		return lV;
		
	}

	public HyperlinkProxy newInstanceFromLinkVector(
			de.mxro.utils.distributedtree.Linking.LinkVector vector) {
		if (!this.getClass().equals(HyperlinkProxy.class)) {
			UserError.singelton.log(this, "newInstanceFromLinkVector: has to be implemented for class "+this.getClass(), UserError.Priority.HIGH);
			return null;
		}
		
		return (HyperlinkProxy) new ExtendedItemBuilder(this.getDocument(), LinnkFatClient.currentUser).newHyperlinkProxy(this.getItem(), vector.firstElement().toString());
	}

	public boolean isLinkOwner() {
		return true;
	}
	
	
	
}
