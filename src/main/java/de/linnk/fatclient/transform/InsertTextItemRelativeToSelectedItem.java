package de.linnk.fatclient.transform;

import de.linnk.basispack.v05.TextItemPanel;
import de.linnk.domain.InsertItem;
import de.linnk.domain.Item;
import de.linnk.domain.ItemBuilder;
import de.linnk.domain.InsertItem.Position;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.linnk.transform.GUITransformation;

public class InsertTextItemRelativeToSelectedItem extends GUITransformation {

	protected  final InsertItem.Position position;
	
	@Override
	protected boolean applyTransformation(ItemsPanel itemsPanel) {
		final Item item =   
			new ItemBuilder(itemsPanel.getDocument(), LinnkFatClient.currentUser).newTextItem("enter text here ...");
		if (! new InsertItemAndSelect(item, this.position).transform(itemsPanel))
			return false;
		
		final TextItemPanel panel = ((TextItemPanel) itemsPanel.getItemPanelFor(item).getItemPanel());
		panel.getJTextField().select(0, panel.getJTextField().getDocument().getLength());
		
		return true;

	}

	public InsertTextItemRelativeToSelectedItem(final Position position) {
		super();
		this.position = position;
	}
	
	

}
