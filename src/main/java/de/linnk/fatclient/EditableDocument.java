package de.linnk.fatclient;

import javax.swing.JMenu;

import de.linnk.fatclient.document.itemspanel.ItemsPanel;


public interface EditableDocument {
	public de.mxro.swing.ToolBar getToolBar(ItemsPanel itemsPanel);
	public void setMenu(JMenu menu, ItemsPanel itemsPanel);
}
