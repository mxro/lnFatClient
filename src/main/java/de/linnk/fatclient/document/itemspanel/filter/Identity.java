package de.linnk.fatclient.document.itemspanel.filter;

import de.linnk.fatclient.document.ItemPanelContainer;

public class Identity extends ItemPanelFilter {

	@Override
	protected ItemPanelContainer applyThis(ItemPanelContainer ip) {
		return ip;
	}

	
	
	@Override
	public ItemPanelContainer apply(ItemPanelContainer ip) {
		return ip;
	}



	public Identity() {
		super(null);
	}
	

}
