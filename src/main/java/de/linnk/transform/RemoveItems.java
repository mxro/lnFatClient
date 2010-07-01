package de.linnk.transform;

import java.util.Vector;

import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.fatclient.application.LinnkFatClient;

public class RemoveItems extends Transformation {

	private final Class<? extends Item> clazz;
	
	@Override
	public void applyTransformation(Document doc) {
		
		final Vector<ItemChange> changes = new Vector<ItemChange>();
		for (final Item i : doc.getItems()) {
			if (i.getItem(this.clazz) != null) {
				final ItemChange ic = ItemChange.newRemoveItem(i, LinnkFatClient.currentUser);
				changes.add(ic);
				System.out.println(doc.getFilename()+": remove: "+i.getId());
			}
		}
		for (final ItemChange ic : changes) {
			doc.doChange(ic);
			
		}
	}

	public RemoveItems(final Class<? extends Item> clazz, Transformation next) {
		super(next);
		this.clazz = clazz;
	}
	
}
