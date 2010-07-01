package de.linnk.fatclient.document;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.Icon;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.mxro.swing.JMyPanel;
import de.mxro.utils.log.UserError;

/**
 * 
 * content of an item, has to be extended to display specific items
 * 
 * @author maxrohde
 *
 */
public abstract class ItemPanel<I extends Item> extends JMyPanel {

	private static final long serialVersionUID = 1L;
	
	protected I item;
	protected ItemsPanel itemspanel;
	
	protected ProxyItemPanel proxy=null;
	protected boolean selected=false;
	
	/**
	 * is set to true if panel was changed and change was sent to
	 * document. this property can not be set to false again
	 * the change in the document should lead to the creation of
	 * a new item.
	 */
	protected boolean changePending=false;
	protected ItemActions<? extends ItemPanel<I>> actions;
	
	
	
	
	protected final java.awt.event.FocusAdapter focusListener;
	
	protected java.awt.event.FocusAdapter getFocusListener() {
		return this.focusListener;
	}
	public abstract void updatePanel();
	
	/**
	 * call this method if the item has to be altered in any way so that
	 * a new panel has to be created
	 */
	public void outdated() {
		//System.out.println("outdated "+this.getItem().getId());
		this.changePending = true;
	}
	
	public NodeDocument getDocument() {
		return (NodeDocument) this.getItem().getDocument();
	}
	
	public boolean isChangePending() {
		return this.changePending;
	}
	/**
	 * if changes where made on panel, this method creates an item that
	 * represents the new item.
	 * if the panel was left unchanged, this method returns null.
	 * @return
	 */
	public abstract Item generateChangedItem() ;
	
	public final Item updateItem() {
		// if change is pendeing, return changed item
		if (this.changePending) {
			UserError.singelton.log(this, "updateItem: Could not apply change for "+this.getItem().getCompleteID()+
					" because another change is pending!", UserError.Priority.LOW); 
			return this.getItem();
		}
		
		// if item wasn't changed return null
		final Item newItem = this.generateChangedItem();
		if ( newItem == null )
			return null;
		
		this.outdated();
		if (!this.changeItemInDocument(newItem)) {
			UserError.singelton.log(this, "updateItem: ItemChange could not be applied for "+
					this.getItem().getCompleteID()+"!", UserError.Priority.NORMAL);
			//this.changePending = false;
		}
		return newItem;
	}
	public abstract void save();

	
	public Icon getIcon() {
		return null;
	}
	
	public final void setActions(ItemActions<? extends ItemPanel<I> > actions) {
		if (actions != null) {
			actions.addKeyboardShortcuts(this);
		}
		this.actions = actions;
	}
	
	public final ItemActions<? extends ItemPanel<I>> getActions() {
		return this.actions;
	}
	
	public I getItem() {
		return this.item;
	}
	public ProxyItemPanel getProxy() {
		return this.proxy;
	}
	public void setProxy(ProxyItemPanel proxy) {
		this.proxy = proxy;
	}
	public void setItem(I item) {
		this.item = item;
	}
	
	public void setItemspanel(ItemsPanel itemspanel) {
		this.itemspanel = itemspanel;
	}
	
	public ItemsPanel getItemspanel() {
		return this.itemspanel;
	}
	/**
	 * This is the default constructor
	 */
	/*public ItemPanel() {
		super();
		initialize();
	}*/
	
	public ItemPanel() {
		this.initialize();	
		
		this.focusListener = new java.awt.event.FocusAdapter() {
			@Override
			public void focusGained(java.awt.event.FocusEvent e) {
				//ItemPanel.this.getItemspanel().deselectAll();
				ItemPanel.this.forceSelect();
				//de.mxro.UserError.singelton.log("focusGained "+this);
			}
		};
		this.addFocusListener(this.focusListener);
	}
	

	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	//	this.setSize(664, 160);
		this.setFocusCycleRoot(true);
		//this.setOpaque(false);
		//this.setBackground(Color.BLUE);
		//this.setAlignmentX(Component.LEFT_ALIGNMENT);
		//this.setAlignmentY(Component.TOP_ALIGNMENT);
	}
	
	public Container parent;
	
	protected boolean changeItemInDocument(Item changedItem) {
		if (this.getProxy() == null)
			return this.getItemspanel().doChange(
					ItemChange.newModifyItem(changedItem, this.item, LinnkFatClient.currentUser)); 
		
		return this.getProxy().changeItemInDocument(this.getProxy().generateChangedItem(changedItem));
		
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ItemPanel))
			return false;
		return (((ItemPanel) obj).getItem().equals(this.getItem()));
	}
	
	public final void forceSelect() {
		if (this.getProxy() == null) {
			this.getItemspanel().forceSelect(this);
		} else {
			this.getProxy().forceSelect();
		}
	}
	
	protected final void forceDeselect() {
		if (this.getProxy() == null) {
			this.getItemspanel().forceDeselect(this);
		} else {
			this.getProxy().forceDeselect();
		}
	}
	
	protected void afterSelect() {
		
	}
	
	
	protected void afterDeselect() {
		
	}
	
	
	
	public final boolean isSelected() {
		return this.selected;
	}
	public void setSelected(boolean selected) {
		
		
		if (this.selected && !selected) {
			this.afterDeselect();
		}
		if (!this.selected && selected) {
			this.afterSelect();
		}
		
		this.selected = selected;
	}
	
	
	
}  //  @jve:decl-index=0:visual-constraint="54,53"
