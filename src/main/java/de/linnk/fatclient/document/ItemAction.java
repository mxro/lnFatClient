package de.linnk.fatclient.document;

import javax.swing.AbstractAction;

public abstract class ItemAction<I extends ItemPanel> extends AbstractAction {

	private final I item;
	
	public final I getItemPanel() {
		return this.item;
	}


	public ItemAction(final I item) {
		super();
		this.item = item;
	}
	
	public final ItemActions<I> getActions() {
		return /*(ItemActions<I>)*/ this.getItemPanel().getActions();
	}

}
