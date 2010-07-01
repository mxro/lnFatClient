package de.linnk.fatclient.document.itemspanel;

import de.linnk.domain.ExchangeItems;
import de.linnk.domain.InsertItem;
import de.linnk.domain.ItemChange;
import de.linnk.domain.ModifyItem;
import de.linnk.domain.NewItem;
import de.linnk.domain.RemoveItem;
import de.mxro.utils.log.UserError;

public abstract class ItemPanelChange<I extends ItemChange> {
	private final I itemChange;
	
	public final I getItemChange() {
		return this.itemChange;
	}
	
	public abstract boolean doOnItemsPanel(ItemsPanel ip);
	public abstract boolean undoOnItemsPanel(ItemsPanel ip);
	
	public static ItemPanelChange createFromItemChange(ItemChange ic) {
        if (ic instanceof ExchangeItems) return new ExchangePanels((ExchangeItems) ic);
        if (ic instanceof NewItem) return new NewItemPanel((NewItem) ic);
        if (ic instanceof RemoveItem) return new RemoveItemPanel((RemoveItem) ic);
        if (ic instanceof InsertItem) return new InsertItemPanel((InsertItem) ic);
        if (ic instanceof ModifyItem) return new ModifyItemPanel((ModifyItem) ic);
		
		UserError.singelton.log("ItemPanelChange.createFormItemChange: Class "+ic.getClass().getName()+" must implement GuiChange.", UserError.Priority.LOW);
		return null;
		
		
	}
	
	public ItemPanelChange(final I itemChange) {
		super();
		this.itemChange = itemChange;
	}
	
	
}
