package de.linnk.fatclient.transform;

import de.linnk.basispack.v05.TextItemPanel;
import de.linnk.domain.Item;
import de.linnk.domain.ItemBuilder;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.linnk.transform.GUITransformation;

public class InsertTextItemAfterSelectedItem extends GUITransformation {

	@Override
	public boolean applyTransformation(ItemsPanel itemsPanel) {
		final Item item =   
			new ItemBuilder(itemsPanel.getDocument(), LinnkFatClient.currentUser).newTextItem("enter text here ...");
		if (! new InsertAfterSelectedItemAndSelect(item).transform(itemsPanel))
			return false;
		
		final TextItemPanel panel = ((TextItemPanel) itemsPanel.getItemPanelFor(item).getItemPanel());
		panel.getJTextField().select(0, panel.getJTextField().getDocument().getLength());
		
		return true;

	}
	
	

}
