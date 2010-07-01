package de.linnk.fatclient.document.itemspanel;

import de.linnk.domain.InsertItem;
import de.linnk.domain.RemoveItem;

public class RemoveItemPanel extends ItemPanelChange<RemoveItem> {

	@Override
	public boolean doOnItemsPanel(ItemsPanel ip) {
		
		return ip.remove(this.getItemChange().getItem().getCompleteID());
		
	}

	@Override
	public boolean undoOnItemsPanel(ItemsPanel ip) {
		
		return ip.insertItem(this.getItemChange().getItem(), this.getItemChange().getItemBefore(), InsertItem.Position.AFTER);
		
	}

	public RemoveItemPanel(RemoveItem itemChange) {
		super(itemChange);
		
	}
	
	
}
