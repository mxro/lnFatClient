package de.linnk.basispack.v05;

import de.linnk.domain.Item;
import de.linnk.fatclient.document.ProxyItemPanel;


public class NoStyleProxyPanel extends ProxyItemPanel {
	
	public static final long serialVersionUID = 1L;
	
	
	
	@Override
	/**
	 * there is nothing to change
	 */
	public boolean isProxyChanged() {
		return false;
	}

	@Override
	public Item generateChangedItem(Item holdedItem) {
		return
			//super.changeEnclosedItemInDocument(changedItem) &
			new NoStyleProxy(this.getItem().getCreator(), this.getItem().getId(), this.getItem().getDocument(), holdedItem);
	}

	
}
