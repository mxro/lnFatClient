package de.linnk.utils;

import java.util.Vector;

import de.linnk.fatclient.document.ItemPanelContainer;

public class ItemPanelContainers extends Vector<ItemPanelContainer>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ItemPanels getItemPanels() {
		final ItemPanels panels = new ItemPanels();
		for (final ItemPanelContainer c : this) {
			panels.add(c.getItemPanel());
		}
		return panels;
	}
}
