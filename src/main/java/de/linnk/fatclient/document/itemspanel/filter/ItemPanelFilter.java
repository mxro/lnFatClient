package de.linnk.fatclient.document.itemspanel.filter;

import de.linnk.fatclient.document.ItemPanelContainer;

public abstract class ItemPanelFilter {
	private final ItemPanelFilter next;
	protected abstract ItemPanelContainer applyThis(ItemPanelContainer ip);
	public ItemPanelContainer apply(ItemPanelContainer ip) {
		return this.next.apply(this.applyThis(ip));
	}
	
	public ItemPanelFilter(final ItemPanelFilter next) {
		super();
		this.next = next;
	}
	
}
