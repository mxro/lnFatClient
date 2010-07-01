package de.linnk.fatclient.document.itemspanel;

import de.linnk.domain.ModifyItem;

public class ModifyItemPanel extends ItemPanelChange<ModifyItem> {

	@Override
	public boolean doOnItemsPanel(ItemsPanel ip) {
		
		return ip.changeItem(this.getItemChange().getChangedItem(), this.getItemChange().getOldItem().getCompleteID());
		
	}

	@Override
	public boolean undoOnItemsPanel(ItemsPanel ip) {
		
		return ip.changeItem(this.getItemChange().getOldItem(), this.getItemChange().getChangedItem().getCompleteID());
		
	}

	public ModifyItemPanel(ModifyItem itemChange) {
		super(itemChange);
	}

	
	
}
