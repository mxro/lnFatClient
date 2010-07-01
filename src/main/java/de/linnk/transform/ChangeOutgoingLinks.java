package de.linnk.transform;

import de.linnk.ExtendedItemBuilder;
import de.linnk.basispack.v05.DocumentProxy;
import de.linnk.basispack.v05.LinnkProxy;
import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.domain.ProxyItem;
import de.linnk.domain.SimpleLink;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.gwt.LinnkGWTUtils;
import de.mxro.utils.Utils;

public class ChangeOutgoingLinks extends Transformation {
	
	private final String newExtension;

	@Override
	public void applyTransformation(Document doc) {
		// LinnkProxies and DocumentProxies
		for (final Item i : doc.getItems()) {
			for (final ProxyItem pi : LinnkGWTUtils.getProxies(LinnkProxy.class, i)) {
				if (i instanceof DocumentProxy) continue;
				final LinnkProxy lp = (LinnkProxy) pi;
				final Item newProxy = new ExtendedItemBuilder(doc, LinnkFatClient.currentUser).newLinnkProxy(lp.getItem(),lp.getId(), new SimpleLink(Utils.removeExtension(lp.getLink().link)+this.newExtension));
				final Item newItem = de.linnk.utils.Utils.replace(doc, lp, newProxy);
				final ItemChange ic = ItemChange.newModifyItem(newItem, i, LinnkFatClient.currentUser);
				doc.doChange(ic);
			}
			for (final ProxyItem pi : LinnkGWTUtils.getProxies(DocumentProxy.class, i)) {
				final DocumentProxy lp = (DocumentProxy) pi;
				//final Item newProxy = lp;
				//System.out.println(LinnkXStream.singelton.toXML(i));
				
				final Item newProxy = new ExtendedItemBuilder(doc, LinnkFatClient.currentUser).newDocumentProxy(lp.getItem(), lp.getId(), 
						new SimpleLink(Utils.removeExtension(lp.getLink().link)+this.newExtension), lp.isExpanded());
				
				final Item newItem = de.linnk.utils.Utils.replace(doc, lp, newProxy);
				//System.out.println(LinnkXStream.singelton.toXML(newItem));
				final ItemChange ic = ItemChange.newModifyItem(newItem, i, LinnkFatClient.currentUser);
				doc.doChange(ic);
			}
		}
		
		// OwnerItem	
		if (doc.getOwnerLink() != null) {
			final SimpleLink sl = new SimpleLink(doc.getOwnerLink());
			doc.setOwner(LinnkFatClient.currentUser, Utils.removeExtension(sl.link)+this.newExtension);
		}
		
	}

	public ChangeOutgoingLinks(final String newExtension, Transformation next) {
		super(next);
		this.newExtension = newExtension;
	}

}
