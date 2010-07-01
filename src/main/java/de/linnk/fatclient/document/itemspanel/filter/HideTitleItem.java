package de.linnk.fatclient.document.itemspanel.filter;

import de.linnk.basispack.v05.TitleItemPanel;
import de.linnk.fatclient.document.ItemPanelContainer;

public class HideTitleItem extends ItemPanelFilter {

	@Override
	protected ItemPanelContainer applyThis(ItemPanelContainer ip) {
		if (ip.getItemPanel() instanceof TitleItemPanel)
			return null;;
		
		return ip;
	}

	public HideTitleItem(ItemPanelFilter next) {
		super(next);
	}
	
	

}
