package de.linnk.transform;

import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.fatclient.application.LinnkFatClient;
import de.mxro.utils.log.UserError;

public abstract class UpdateItems extends Transformation {

	
	protected abstract Class<? extends Item> forClass();
	
	public UpdateItems( Transformation next) {
		super(next);
	}

	@Override
	public void applyTransformation(Document doc) {
		for (final Item i: doc.getItems()) {
			if (i.getItem(this.forClass()) != null) {
				final Item newItem = de.linnk.utils.Utils.replace(doc, i.getItem(this.forClass()), this.createNewItem(doc, i.getItem(this.forClass())));
				if (newItem == null) {
					UserError.singelton.log(this, "Could not update Item "+i.getId()+ " in document "+doc.getFilename().toString(), UserError.Priority.NORMAL);
					
				}
				final ItemChange ic = ItemChange.newModifyItem(newItem,i, LinnkFatClient.currentUser);
				if (!doc.doChange(ic)) {
					UserError.singelton.log("Could not update Item "+i.getId()+ " in document "+doc.getFilename().toString(), UserError.Priority.NORMAL);
				}
			}
		}
		
	}
	
	public abstract Item createNewItem(Document doc, Item oldItem);

}
