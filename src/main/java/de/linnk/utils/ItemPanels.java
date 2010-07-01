package de.linnk.utils;

import java.util.Vector;

import de.linnk.fatclient.document.ItemPanel;

public class ItemPanels extends Vector<ItemPanel> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Items getItems() {
		final Items items = new Items();
		for (final ItemPanel i : this) {
			items.add(i.getItem());
		}
		return items;
	}
}
