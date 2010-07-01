package de.linnk.basispack.v05;

import javax.swing.Icon;

import de.linnk.domain.Item;
import de.linnk.fatclient.icons.Icons;

public class DocumentProxyPanel extends LinnkProxyPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Item generateChangedItem(Item holdedItem) {
		return 
			new DocumentProxy(this.getItem().getCreator(), this.getItem().getId(), this.getItem().getDocument(), holdedItem, ( this.item).getLink(), this.item.isExpanded());
	}

	@Override
	public Icon getIcon() {
		
		return Icons.getSmallIcon("DocumentProxy.png");
	}
	
	
}
