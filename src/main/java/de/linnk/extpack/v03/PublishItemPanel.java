package de.linnk.extpack.v03;

import javax.swing.JButton;
import javax.swing.JLabel;

import de.linnk.domain.Item;
import de.linnk.fatclient.document.ItemPanel;

public class PublishItemPanel extends ItemPanel<PublishItem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	


	@Override
	public void save() {
		
	}

	@Override
	public Item generateChangedItem() {
		return this.getItem();
	}

	@Override
	public void updatePanel() {
		
	}

	public PublishItemPanel() {
		super();
		this.setActions(new PublishItemActions(this));
		
		this.add(new JLabel("Publisher"));
		
		final JButton publishButton = new JButton(this.getActions().getAction(PublishItemActions.PublishAction.class));
		final JButton editButton = new JButton(this.getActions().getAction(PublishItemActions.EditPublisherAction.class)); 
		this.add(publishButton);
		this.add(editButton);
		
	
	}
	
	

}
