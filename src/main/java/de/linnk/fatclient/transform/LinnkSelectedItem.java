package de.linnk.fatclient.transform;

import de.linnk.DocumentBuilder;
import de.linnk.ExtendedItemBuilder;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.domain.SimpleLink;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.linnk.transform.GUITransformation;
import de.mxro.utils.log.UserError;

public class LinnkSelectedItem extends GUITransformation {

	@Override
	protected boolean applyTransformation(ItemsPanel itemsPanel) {
		
		Item selected = itemsPanel.getSelectedItem();
		if ( selected == null) {
			UserError.singelton.log(this, "Called and no item is selected", UserError.Priority.NORMAL);
			return false;
		}
		final String selectedID = selected.getCompleteID();
		
		itemsPanel.getItemPanelFor(itemsPanel.getDocument().getItem(selectedID)).updateItem();
		if ( !itemsPanel.waitUntilNoChangesPending(200) )
			return false;
		selected = itemsPanel.getDocument().getItem(selectedID);
		final SimpleLink simpleLink = DocumentBuilder.instance.createDocumentForItem(selected, LinnkFatClient.currentUser);
		
		if (simpleLink == null) {
			de.mxro.utils.log.UserError.singelton.log("LinnkSelectedItem.applyTransformation: " +
					"Could not create document", UserError.Priority.HIGH);
			de.mxro.utils.log.UserError.singelton.showError("Document could not be created.");
			return false;
		}
		//de.mxro.UserError.singelton.log("simpleLink created: "+simpleLink.link);
		//XMLView xmlview = new XMLView();
		 
			Item newItem;
			final ExtendedItemBuilder ib = new ExtendedItemBuilder( itemsPanel.getDocument(), LinnkFatClient.currentUser);
			newItem = ib.newDocumentProxy(selected, simpleLink, false);
			
			
			final ItemChange modifyItem = ItemChange.newModifyItem(newItem, selected, LinnkFatClient.currentUser);
			if (!itemsPanel.doChange(modifyItem)) {
				de.mxro.utils.log.UserError.singelton.log("LinnkItemAction: Could not insert link!", UserError.Priority.HIGH);
				de.mxro.utils.log.UserError.singelton.showError("Could not insert link!");
				return false;
			}
			
			itemsPanel.selectItem(newItem.getCompleteID());
			
		
		
		return true;
	}

}
