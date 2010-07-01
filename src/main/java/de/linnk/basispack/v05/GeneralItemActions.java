package de.linnk.basispack.v05;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.linnk.DocumentBuilder;
import de.linnk.ExtendedItemBuilder;
import de.linnk.Linnk;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.domain.SimpleLink;
import de.linnk.domain.TextItem;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.ItemActions;
import de.linnk.fatclient.document.ItemPanel;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.mxro.transferable.ClipboardFacade;

public class GeneralItemActions extends ItemActions<ItemPanel> {
	
	protected final ItemPanel itemContentPanel;
	protected final ExtendedItemBuilder itemBuilder;
	
	
	
	

	private class LinnkItemAction extends AbstractAction {
		public static final long serialVersionUID = 1L;
		
		protected final ItemPanel itemContentPanel;
	
		
		public void actionPerformed(ActionEvent arg0) {
			final ItemsPanel itemsPanel = this.itemContentPanel.getItemspanel();
			if (itemsPanel != null) {
				//de.mxro.UserError.singelton.log("updateItems");
				itemsPanel.updateItems();
				final Item selected = itemsPanel.getSelectedItem();
				//de.mxro.UserError.singelton.log("selected caught");
				String title;
				if (selected instanceof TextItem) {
					title = de.mxro.utils.Utils.removeMarkup(((TextItem) selected).getTextData());
					if (title.length() > 40) {
						title = title.substring(0, 39);
					}
				} else {
					if (selected == null)
						return;
					title = selected.getId();
				}
				itemsPanel.deselectAll();
				
				/*Document res = DocumentBuilder.emptyDocument(
						Document.getSimpleName(title),
						User.currentUser,
						itemsPanel.getDocument().getFolder().createFolder(Document.getSimpleName(title)));*/
				
				final SimpleLink simpleLink = DocumentBuilder.instance.createEmptyChildDocument(itemsPanel.getDocument(), LinnkFatClient.currentUser, title);
				
				//XMLView xmlview = new XMLView();
				if (simpleLink != null) {
					final Item linnkProxy = GeneralItemActions.this.itemBuilder.newLinnkProxy(selected, simpleLink);
					
					final ItemChange modifyItem = ItemChange.newModifyItem(linnkProxy, selected, LinnkFatClient.currentUser);
					this.itemContentPanel.getItemspanel().doChange(modifyItem);
					
				} else {
					de.mxro.utils.log.UserError.singelton.showError("Document could not be created.");
				}
			}
			
		}
		
		public LinnkItemAction(final ItemPanel icp) {
			super();
			this.itemContentPanel = icp;
			this.putValue(Action.NAME, "Linnk");
			this.putValue(Action.SHORT_DESCRIPTION, "Linnks this item with new Document.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		}
		
	}
	
	public AbstractAction getLinnkItemAction() {
		return new LinnkItemAction(this.itemContentPanel);
	}
	
	private class DeleteItemAction extends AbstractAction {
		public static final long serialVersionUID = 1L;
		
		protected final ItemPanel itemContentPanel;

		public void actionPerformed(ActionEvent arg0) {
			this.itemContentPanel.getItemspanel().doChange(ItemChange.newRemoveItem(this.itemContentPanel.getItem(), LinnkFatClient.currentUser));
			
		}
		
		public DeleteItemAction(final ItemPanel icp) {
			super();
			this.itemContentPanel = icp;
			this.putValue(Action.NAME, "Delete");
			this.putValue(Action.SHORT_DESCRIPTION, "Deletes selected item.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		}
		
	}

	public AbstractAction getDeleteItemAction() {
		return new DeleteItemAction(this.itemContentPanel);
	}
	
	
	private class CopyItemAction extends AbstractAction {
		public static final long serialVersionUID = 1L;
		
		protected final ItemPanel itemContentPanel;

		public void actionPerformed(ActionEvent arg0) {
			ClipboardFacade.setText(Linnk.S.itemToString(this.itemContentPanel.getItem()));
					
			
		}
		
		public CopyItemAction(final ItemPanel icp) {
			super();
			this.itemContentPanel = icp;
			this.putValue(Action.NAME, "Copy");
			this.putValue(Action.SHORT_DESCRIPTION, "Copies this item by adding it to the clipboard.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		}
		
	}

	public AbstractAction getCopyItemAction() {
		return new CopyItemAction(this.itemContentPanel);
	}


	
	public GeneralItemActions(final ItemPanel itemContentPanel) {
		super(itemContentPanel);
		this.itemContentPanel = itemContentPanel;
		this.itemBuilder = new ExtendedItemBuilder(itemContentPanel.getItem().getDocument(), LinnkFatClient.currentUser );
	}
}
