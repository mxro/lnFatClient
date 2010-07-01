package de.linnk.fatclient.document.itemspanel;

import de.linnk.domain.ExchangeItems;

public class ExchangePanels extends ItemPanelChange<ExchangeItems> {

	@Override
	public boolean doOnItemsPanel(ItemsPanel ip) {
		
			return ip.exchangeItems(this.getItemChange().getItemId1(), this.getItemChange().getItemId2());
		
	}

	@Override
	public boolean undoOnItemsPanel(ItemsPanel ip) {
		
			return ip.exchangeItems(this.getItemChange().getItemId2(), this.getItemChange().getItemId1());
		
	}

	public ExchangePanels(ExchangeItems itemChange) {
		super(itemChange);
		
	}
	

}
