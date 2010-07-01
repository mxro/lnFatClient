package de.linnk.fatclient.document.itemspanel;

import de.linnk.domain.InsertItem;

public class InsertItemPanel extends ItemPanelChange<InsertItem> {

	@Override
	public boolean doOnItemsPanel(ItemsPanel ip) {
		return ip.insertItem(this.getItemChange().getItem(), this.getItemChange().getRelativeItemID(),	this.getItemChange().getPosition());
	}

	@Override
	public boolean undoOnItemsPanel(ItemsPanel ip) {
		return ip.remove(this.getItemChange().getItem().getCompleteID());
	}

	public InsertItemPanel(InsertItem itemChange) {
		super(itemChange);
	}

	
	
}
