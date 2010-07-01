package de.linnk.transform;

import java.util.Vector;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.AlwaysPublish;
import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.ItemBuilder;
import de.linnk.domain.ItemChange;
import de.linnk.extpack.v03.HyperlinkProxy;
import de.linnk.fatclient.application.LinnkFatClient;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;

public class RemoveAllItems extends Transformation {

	@Override
	public void applyTransformation(Document doc) {
		
			final Vector<ItemChange> ics = new Vector<ItemChange>();
			for (final Item i : doc.getItems()) {
				if (!(i instanceof AlwaysPublish)) {
					if (i instanceof HyperlinkProxy) {
						final HyperlinkProxy hp = (HyperlinkProxy) i;
						final URI hyperlinkURI = URIImpl.create( hp.getHyperlink());
						if (!(hyperlinkURI.isAbsolute())) {
							final FileSystemObject file = ((NodeDocument) doc).getFolder().get(hyperlinkURI);
							if (file != null) {
								if (!file.delete()) {
									UserError.singelton.log(this, "Could not delete linked Object: "+ ((NodeDocument) doc).getFolder().getURI()+" "+hyperlinkURI, UserError.Priority.NORMAL);
								}
							} else {
								UserError.singelton.log(this, "Could not delete linked Object: "+ ((NodeDocument) doc).getFolder().getURI()+" "+hyperlinkURI, UserError.Priority.LOW);
							}
						}
					}
					final ItemChange ri = ItemChange.newRemoveItem(i, LinnkFatClient.currentUser);
					ics.add(ri);
				}
				
			}
			for (final ItemChange ic: ics) {
				doc.doChange(ic);
			}
			final ItemBuilder ib = new ItemBuilder(doc, LinnkFatClient.currentUser);
			doc.doChange( ib.newChange(ib.newTextItem("This document is not published.")) );
		
		
	}

	public RemoveAllItems(Transformation next) {
		super(next);
	}

	
}
