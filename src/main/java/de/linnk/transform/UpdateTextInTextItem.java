package de.linnk.transform;

import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.ItemBuilder;
import de.linnk.domain.TextItem;
import de.linnk.fatclient.application.LinnkFatClient;
import de.mxro.string.filter.Filter;

public class UpdateTextInTextItem extends UpdateItems {

	private final Filter filter;
	
	public UpdateTextInTextItem(final Filter filter, Transformation next) {
		super(next);
		this.filter = filter;
	}

	@Override
	public Item createNewItem(Document doc, Item oldItem) {
		final ItemBuilder ib = new ItemBuilder(doc, LinnkFatClient.currentUser); 
		return ib.newTextItemFromTextData(this.filter.perform( ((TextItem) oldItem).getTextData()));
	}

	@Override
	protected Class<? extends Item> forClass() {
		return TextItem.class;
	}

}
