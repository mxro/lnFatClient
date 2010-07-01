package de.linnk.fatclient.document.itemspanel;

import de.linnk.domain.NewItem;

public class NewItemPanel extends ItemPanelChange<NewItem> {

	@Override
	public boolean doOnItemsPanel(ItemsPanel ip) {
	
		if (!ip.appendItemPanel(this.getItemChange().getItem()))
			return false;
		
		return true;
		
	}

	@Override
	public boolean undoOnItemsPanel(ItemsPanel ip) {
		
		if (!ip.remove(this.getItemChange().getItem().getCompleteID()))
			return false;
		return true;
		
	}

	public NewItemPanel(NewItem ic) {
		super(ic);
		
	}

	
	
}
