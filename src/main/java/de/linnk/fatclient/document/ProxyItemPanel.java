package de.linnk.fatclient.document;

import java.awt.Component;

import javax.swing.BoxLayout;

import de.linnk.domain.Item;
import de.linnk.domain.ProxyItem;
import de.mxro.swing.JMyPanel;

public abstract class ProxyItemPanel<I extends ProxyItem> extends ItemPanel<I> {
	
	public static final long serialVersionUID = 1L;
	
	public ItemPanel holdedPanel;
	public ItemPanelContainer holdedItemPanel;
	
	private JMyPanel itemPanel;
	private JMyPanel contentPanel;
	
	
	public JMyPanel getItemPanel() {
		return this.itemPanel;
	}

	public final JMyPanel getContentPanel() {
		return this.contentPanel;
	}

	protected ItemPanelContainer getHoldedItemPanel() {
		return this.holdedItemPanel;
	}

	protected void setHoldedItemPanel(ItemPanelContainer holdedItemPanel) {
		if (this.holdedItemPanel != null) {
			this.itemPanel.remove(this.holdedItemPanel);
		}
		this.itemPanel.add(holdedItemPanel);
		this.holdedItemPanel = holdedItemPanel;
	}
	
	public ItemPanelContainer getItemPanelFor(Item item) {
		if (this.getItem().getItem() == item)
			return this.getHoldedItemPanel();
		if (!(this.getHoldedItemPanel().getItemPanel() instanceof ProxyItemPanel))
			return null;
		return (((ProxyItemPanel) this.getHoldedItemPanel().getItemPanel()).getItemPanelFor(item));
	}
	
	@Override
	public void setItem(I item) {
		super.setItem(item);
	
		final ItemPanelBuilder builder = new ItemPanelBuilder();
		
		ItemPanelContainer holdedItemPanel = null;
		holdedItemPanel = builder.createPanel(((ProxyItem) item).getItem(), this.itemspanel);
		this.holdedPanel = holdedItemPanel.getItemPanel();
		holdedItemPanel.getItemPanel().setProxy(this);
		holdedItemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		holdedItemPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		holdedItemPanel.setOpaque(false);
		//holdedItemPanel.setBackground(Color.BLUE);
		
		 this.setHoldedItemPanel(holdedItemPanel);
	}
	
	public ProxyItemPanel() {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.setAlignmentY(Component.TOP_ALIGNMENT);
		
		this.itemPanel = new JMyPanel();
		this.itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.itemPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		this.itemPanel.setLayout(new BoxLayout(this.itemPanel, BoxLayout.X_AXIS));
		
		this.contentPanel = new JMyPanel();
		this.contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.contentPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		this.contentPanel.setLayout(new BoxLayout(this.contentPanel, BoxLayout.X_AXIS));
		
		
		//this.add(this.itemPanel);
		//this.add(this.contentPanel);
	}
    
	/**
	 * check, if proxy is changed and has to be rebuild when
	 * generateChangedItem is called.
	 * @return
	 */
	public abstract boolean isProxyChanged();
	
	@Override
	/**
	 * default implementation - only changed, if enclosed item is changed
	 */
	public final Item generateChangedItem() {
		final Item newItem = this.getHoldedItemPanel().updateItem();
		// System.out.println("Proxy "+this.getItem().getCompleteID()+" newItem "+newItem+" is Changed: "+isProxyChanged());
		if ( newItem == null && ! this.isProxyChanged() ) return null;
		return this.generateChangedItem(newItem);
	}
	
	public abstract Item generateChangedItem(Item holdedItem); /*{
		((ProxyItem) this.item).setItem(changedItem);
		return true;
	}*/


	@Override
	public void save() {
		this.holdedPanel.save();
	}


	@Override
	public void updatePanel() {
		this.holdedPanel.updatePanel();
	}

	@Override
	public void setSelected(boolean selected) {
		this.holdedPanel.setSelected(selected); // order is of importance !!!
		super.setSelected(selected);
	}

	@Override
	protected void afterDeselect() {
		this.holdedPanel.afterDeselect();
		super.afterDeselect();
	}

	
	
	

}
