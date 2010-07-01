package de.linnk.fatclient;

import java.util.Vector;

import javax.swing.Action;
import javax.swing.JMenu;

import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.mxro.swing.actions.Actions;

public class WindowInterface extends de.mxro.swing.actions.WindowInterface {
	
	//private final DocumentPanelContainer dpContainer;
	
	
	
	/**
	 * contains refereces to selected items
	 */
	//private Vector<ItemPanelContainer> ipContainers;
	private LinnkDocumentActions linnkDocumentActions;
	private LinnkTabsActions linnkTabsActions;
	
	
	
	private JMenu documentMenu = null;
	private JMenu itemMenu = null;
	public JMenu getItemMenu() {
		return this.itemMenu;
	}

	public void setItemMenu(JMenu itemMenu) {
		this.itemMenu = itemMenu;
	}

	public void setDocumentMenu(JMenu documentMenu) {
		this.documentMenu = documentMenu;
	}

	public JMenu getDocumentMenu() {
		return this.documentMenu;
	}
	
	
	
	public final void setLinnkDocumentActions(
			LinnkDocumentActions linnkDocumentActions) {
		this.linnkDocumentActions = linnkDocumentActions;
	}

	public final LinnkDocumentActions getLinnkDocumentActions() {
		return this.linnkDocumentActions;
	}


	public WindowInterface() {
		super();
		//this.ipContainers = new Vector<ItemPanelContainer>();
		
		
	}
	
	@Override
	public void check() {
		
		for (final Actions a : this.actions) {
			a.checkEnabled();
		}
	}

	
	private ItemsPanel activeItemsPanel;
	
	public ItemsPanel getActiveItemsPanel() {
		return this.activeItemsPanel;
	}

	public void setActiveItemsPanel(ItemsPanel activeItemsPanel) {
		if (this.activeItemsPanel != null && activeItemsPanel != this.activeItemsPanel) {
			this.activeItemsPanel.deselectAll();
			this.activeItemsPanel.repaint();
		}
		this.linnkDocumentActions.setItemsPanel(activeItemsPanel);
		this.activeItemsPanel = activeItemsPanel;
	}

	public LinnkTabsActions getLinnkTabsActions() {
		return this.linnkTabsActions;
	}

	public void setLinnkTabsActions(LinnkTabsActions linnkTabsActions) {
		this.linnkTabsActions = linnkTabsActions;
	}
	
	public Vector<Action> getGlobalActions() {
		final Vector<Action> actions = new Vector<Action>();
		for (final Action a : this.getLinnkTabsActions().getActions()) {
			actions.add(a);
		}
		for (final Action a : this.getLinnkDocumentActions().getActions()) {
			actions.add(a);
		}
		return actions;
	}
}
