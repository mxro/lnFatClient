package de.linnk.transform;

import de.linnk.domain.Item;
import de.linnk.fatclient.document.ItemPanel;
import de.linnk.fatclient.document.ItemPanelContainer;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.mxro.utils.log.UserError;

public abstract class GUIItemTransformation<I extends Item, P extends ItemPanel<I>> extends ItemTransformation<I> {

	protected abstract void doWithPanelBefore(P forPanel);
	protected abstract void doWithPanelAfterwards(P forPanel);

	public boolean transform(ItemsPanel itemsPanel, ItemPanel<I> forPanel) {
		final String itemID = forPanel.getItem().getCompleteID();
		itemsPanel.updateItems();
		this.doWithPanelBefore((P) itemsPanel.getItemPanelFor(itemsPanel.getDocument().getItem(itemID)).getItemPanel());
		// System.out.println("itemID: "+itemID);
		final Item pointedItem = itemsPanel.getDocument().getItem(itemID);
		if (pointedItem == null) {
			UserError.singelton.log(this, "transform: Could not find item with path: "+itemID, UserError.Priority.HIGH);
			return false;
		}
		final ItemPanelContainer container = itemsPanel.getItemPanelFor( pointedItem );
		if (container == null) {
			UserError.singelton.log(this, "transform: Could not find item panel for item with path: "+itemID, UserError.Priority.HIGH);
			return false;
		}
		final ItemPanel realPanel = container.getItemPanel();
		if (!de.linnk.utils.Utils.applyItemTransformation(realPanel.getItem(), itemsPanel, this))
			return false;
		this.doWithPanelAfterwards((P) itemsPanel.getItemPanelFor(itemsPanel.getDocument().getItem(itemID)).getItemPanel());
		return true;
	}
	
}
