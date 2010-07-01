package de.linnk.extpack.v03;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.linnk.ExtendedItemBuilder;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.ItemAction;
import de.linnk.fatclient.document.ItemActions;
import de.linnk.streaming.Publisher;
import de.linnk.utils.Utils;
import de.mxro.utils.log.UserError;

public class PublishItemActions extends ItemActions<PublishItemPanel> {
	

	public PublishItemActions(final PublishItemPanel publishItem) {
		super(publishItem);
		
		this.addAction(new PublishAction(this.getPanel()));
		this.addAction(new EditPublisherAction(this.getPanel()));
		
	}
	

	
	public static class PublishAction extends ItemAction<PublishItemPanel> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent arg0) {
			if (!this.getItemPanel().getItem().getPublisher().publish()) {
				UserError.singelton.showError("Publishing failed!");
			}
		}

		public PublishAction(PublishItemPanel item) {
			super(item);
			
			this.putValue(Action.NAME, "Publish");
			this.putValue(Action.SHORT_DESCRIPTION, "Published now to given destination.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
			//this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		    //        KeyEvent.VK_RIGHT, ActionEvent.ALT_MASK+ActionEvent.CTRL_MASK));
		}
		
		
	}
	
	public static class EditPublisherAction extends ItemAction<PublishItemPanel> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent arg0) {
			Publisher publisher = this.getItemPanel().getItem().getPublisher();
			publisher = PublisherDialog.editPublisher(publisher);
			if (publisher == null) {
				UserError.singelton.showError("Publish Item could not be edited.");
				return;
			}
			final ExtendedItemBuilder ib = new ExtendedItemBuilder(this.getItemPanel().getItem().getDocument(), LinnkFatClient.currentUser);
			final Item pi = ib.newPublishItem(publisher);
			final Item newItem = Utils.replace(this.getItemPanel().getItem().getDocument(), this.getItemPanel().getItem(), pi);
			final ItemChange ic = ItemChange.newModifyItem(newItem, this.getItemPanel().getItem().getDocument().getRootItem(this.getItemPanel().getItem()), LinnkFatClient.currentUser); 
			
			
			this.getItemPanel().getItemspanel().doChange(ic);
		}

		public EditPublisherAction(PublishItemPanel item) {
			super(item);
			
			this.putValue(Action.NAME, "Edit");
			this.putValue(Action.SHORT_DESCRIPTION, "Edit the settings for publishing.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
			//this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		    //        KeyEvent.VK_RIGHT, ActionEvent.ALT_MASK+ActionEvent.CTRL_MASK));
		}
		
		
	}



	
}
