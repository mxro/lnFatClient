package de.linnk.fatclient.transform;

import java.util.Vector;

import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.ItemPanelContainer;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.linnk.transform.GUITransformation;

public class DeleteSelectedItems extends GUITransformation {

	@Override
	protected boolean applyTransformation(ItemsPanel itemsPanel) {
		final Vector<ItemChange> changes = new Vector<ItemChange>();
		ItemPanelContainer toSelect=null;
		for (final Item selectedItem : itemsPanel.getSelectedItems()) {
			final String selectedItemId = selectedItem.getCompleteID();
			itemsPanel.getItemPanelFor(selectedItem).getItemPanel().updateItem();
			itemsPanel.waitUntilNoChangesPending(LinnkFatClient.PENDING_WAIT);
			
			final Item selectedItemNew = itemsPanel.getDocument().getItem(selectedItemId);
			
			toSelect = itemsPanel.nextItem(selectedItemNew);
			if (toSelect==null) {
				toSelect = itemsPanel.previousItem(selectedItemNew);
			}
			
			final ItemChange ri = ItemChange.newRemoveItem(selectedItem, LinnkFatClient.currentUser);
			changes.add(ri);
		}
		
		for (final ItemChange ic: changes) {
			itemsPanel.doChange(ic);
		}
		
		if (toSelect != null) {
			itemsPanel.forceSelect(toSelect.getItemPanel());
			itemsPanel.deselectAll(toSelect.getItemPanel().getItem().getCompleteID());
		}
		return true;
	}

}
