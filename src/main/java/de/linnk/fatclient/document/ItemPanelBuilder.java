package de.linnk.fatclient.document;

import java.util.HashMap;
import java.util.Map;

import de.linnk.Linnk;
import de.linnk.domain.Item;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;

public class ItemPanelBuilder {
	public static ItemPanelBuilder singelton = new ItemPanelBuilder();
	
	public final Map<Class<? extends Item>, Class<? extends ItemPanel>> panels; // that's kind of confusing ...
	
	public ItemPanelBuilder() {
		super();
		this.panels = new HashMap<Class<? extends Item>, Class<? extends ItemPanel>>();
		Linnk.S.registerEasyEditPanels(this);
	}
	
	public void registerContentItemPanel(Class<? extends Item> forClass, Class<? extends ItemPanel> panel) {
		this.panels.put(forClass, panel);
	}


	public ItemPanelContainer createPanel(Item item, ItemsPanel itemspanel) {
		
		/*if (item instanceof EasyEditItem) {
			((EasyEditItem) item).registerEasyEditPanel(this);
		}*/
		final Class<? extends ItemPanel> panelClass = this.panels.get(item.getClass());
		
		if (panelClass != null) {
			try {
				final ItemPanel panel = panelClass.newInstance();
				panel.setItemspanel(itemspanel); // must be in this order !!!!!
				panel.setItem(item);
				final ItemPanelContainer itemPanel = new ItemPanelContainer(itemspanel, panel);
				// itemPanel.setToolbar(panel.getToolbar());
				return itemPanel;
			} catch (final IllegalAccessException e) {
				de.mxro.utils.log.UserError.singelton.log(e);
			}
			catch (final InstantiationException e) {
				de.mxro.utils.log.UserError.singelton.log(e);
			}
		}
		
		
		return null;
	}
}
