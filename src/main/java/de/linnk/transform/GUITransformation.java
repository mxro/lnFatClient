package de.linnk.transform;

import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.mxro.utils.log.UserError;

public abstract class GUITransformation  {

	protected abstract boolean applyTransformation(ItemsPanel itemsPanel);
	
	public final boolean transform(ItemsPanel itemsPanel) {
		itemsPanel.updateItems();
		if (!itemsPanel.waitUntilNoChangesPending(LinnkFatClient.PENDING_WAIT)) {
			UserError.singelton.log(this, "GUITransformation.transform: Could not perform changes.", UserError.Priority.NORMAL);
		}
		return this.applyTransformation(itemsPanel);
	}

}
