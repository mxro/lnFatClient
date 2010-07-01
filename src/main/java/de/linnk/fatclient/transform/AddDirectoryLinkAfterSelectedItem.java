package de.linnk.fatclient.transform;

import de.linnk.ExtendedItemBuilder;
import de.linnk.domain.InsertItem;
import de.linnk.domain.Item;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.linnk.transform.GUITransformation;

public class AddDirectoryLinkAfterSelectedItem extends GUITransformation {

	private final String directoryName;
	
	
	@Override
	protected boolean applyTransformation(ItemsPanel itemsPanel) {
		
	  final ExtendedItemBuilder ib = new ExtendedItemBuilder(itemsPanel.getDocument(), LinnkFatClient.currentUser);		
				// create directory
				if (itemsPanel.getDocument().getFolder().createFolder(this.directoryName)!=null) {
				
					final Item newItem =   
						ib.newTextItem(this.directoryName);
					final Item newHyperlinkProxy = ib.newHyperlinkProxy(newItem, this.directoryName+"/");
					return new InsertItemAndSelect(newHyperlinkProxy, InsertItem.Position.AFTER).transform(itemsPanel);
					
				} else
					return false;
		
		
	}


	public AddDirectoryLinkAfterSelectedItem(final String directoryName) {
		super();
		this.directoryName = directoryName;
	}

	
	
}
