package de.linnk.fatclient.transform;

import javax.swing.SwingUtilities;

import de.linnk.domain.InsertItem;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.linnk.transform.GUITransformation;

public class InsertItemAndSelect extends GUITransformation {
	
	private final Item whatItem;
	protected  final InsertItem.Position position;
	
	private static class Selector {
		
		private final Item item;
		private final ItemsPanel itemsPanel;
		
		public void doSelect() {
//			 necessary because windows focuses menu otherwise
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						Thread.sleep(200);
					} catch (final InterruptedException e) { }
					Selector.this.itemsPanel.deselectAll(Selector.this.item.getCompleteID());
					Selector.this.itemsPanel.selectItem(Selector.this.item.getCompleteID());
				}	
			});
		}

		public Selector(final Item item, final ItemsPanel itemsPanel) {
			super();
			this.item = item;
			this.itemsPanel = itemsPanel;
		}
		
		
	}
	
	@Override
	protected boolean applyTransformation(ItemsPanel itemsPanel) {
		final Item selected = itemsPanel.getSelectedItem();
		
		if (selected == null) {
			final ItemChange itemChange = ItemChange.newNewItem(
					this.whatItem, 
					LinnkFatClient.currentUser);
			new Selector(this.whatItem, itemsPanel).doSelect();
			
			return itemsPanel.doChange(itemChange);
		}
		
		final ItemChange itemChange = ItemChange.newInsertItem(
				this.whatItem, 
				selected,
				this.position,
				LinnkFatClient.currentUser,
				ItemChange.Type.REVERSIBLE);
		
		
		new Selector(this.whatItem, itemsPanel).doSelect();
		
		return itemsPanel.doChange(itemChange);
	}

	public InsertItemAndSelect(final Item whatItem, final InsertItem.Position position) {
		super();
		this.whatItem = whatItem;
		this.position = position;
	}

	
}
