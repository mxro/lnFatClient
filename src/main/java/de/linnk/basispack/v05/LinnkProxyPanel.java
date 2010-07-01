package de.linnk.basispack.v05;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.border.Border;

import de.linnk.Linnk;
import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.ProxyItemPanel;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.linnk.fatclient.document.itemspanel.filter.HideTitleItem;
import de.linnk.fatclient.document.itemspanel.filter.Identity;
import de.linnk.fatclient.icons.Icons;
import de.linnk.streaming.DocumentLoader;
import de.linnk.transform.GUIItemTransformation;
import de.mxro.swing.JIconButton;
import de.mxro.swing.JMyPanel;
import de.mxro.utils.log.UserError;

public class LinnkProxyPanel extends ProxyItemPanel<LinnkProxy> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//private ItemEditBar itemEditBar;
	
	private JMyPanel jPanelItems = null;
	//private JMyPanel jPanelHeading = null;
	//private JMyPanel jPanelContent = null;
	//private JMyPanel jHoldedItemPanelHolder = null;
	//private JLink followLink = null;
	//private JLink expandLink = null;
	//private JLink collapseLink = null;
	private Document holdedDocument=null;
	
	private ItemsPanel myItemsPanel = null;
	
	
	protected Document getHoldedDocument() {
		return this.holdedDocument;
	}

	
	
	@Override
	/**
	 * not yet possible to change
	 */
	public boolean isProxyChanged() {
		return false;
	}



	protected void setHoldedDocument(Document holdedDocument) {
		
		if (holdedDocument != null) {
			final ItemsPanel ip = new ItemsPanel(holdedDocument, this.getItemspanel().getDocumentPanel(), new HideTitleItem(new Identity()));
		    ip.setOwnerItem(this);
			// ip.addToolbar();
			this.setMyItemsPanel(ip);
		} else {
			if (this.holdedDocument != null) {
				this.getMyItemsPanel().save();
				Linnk.S.saveDocument(this.getHoldedDocument());
				
				this.setMyItemsPanel(null);
			}
		}
		this.holdedDocument = holdedDocument;
	}

	

	protected void setMyItemsPanel(ItemsPanel myItemsPanel) {
		if (this.myItemsPanel != null) {
			this.jPanelItems.remove(this.myItemsPanel);
		}
		this.myItemsPanel = myItemsPanel;
		if (myItemsPanel != null) {
			this.jPanelItems.add(this.myItemsPanel);
			this.jPanelItems.setVisible(true);
			//this.revalidate();
		} else {
			this.jPanelItems.setVisible(false);
		}
	}

	public ItemsPanel getMyItemsPanel() {
		return this.myItemsPanel;
	}
	
	@Override
	public void save() {
		
		if (this.myItemsPanel != null) {
			this.myItemsPanel.save();
			if (this.myItemsPanel.getDocument().isAltered()) {
				this.getItemspanel().getDocument().touch();
			}
			Linnk.S.saveDocument(this.myItemsPanel.getDocument());
			this.holdedDocument = this.myItemsPanel.getDocument();
		}
		super.save();
	}
	
	
	private static final Border ITEMS_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0), BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.gray, 1), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
	/**
	 * This method initializes jPanelItems	
	 * 	
	 * @return javax.swing.JMyPanel	
	 */
	private JMyPanel getJMyPanelItems() {
		if (this.jPanelItems == null) {
			this.jPanelItems = new JMyPanel();
			this.jPanelItems.setLayout(new BoxLayout(this.getJMyPanelItems(), BoxLayout.Y_AXIS));
			this.jPanelItems.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.jPanelItems.setBorder(ITEMS_BORDER);
			this.jPanelItems.setAlignmentY(Component.TOP_ALIGNMENT);
			this.jPanelItems.setVisible(false);
		}
		return this.jPanelItems;
	}
	
	
	@Override
	public Icon getIcon() {
		return Icons.getSmallIcon("LinnkProxy.png");
	}


	private JMyPanel jPanelContent;
	
	private JMyPanel getJMyPanelContent() {
		if (this.jPanelContent == null) {
			this.jPanelContent = new JMyPanel();
			this.jPanelContent.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.jPanelContent.setAlignmentY(Component.TOP_ALIGNMENT);
			this.jPanelContent.setLayout(new BoxLayout(this.jPanelContent, BoxLayout.X_AXIS));
			/*try {
				ImageIcon icon = new ImageIcon(this.getClass().getResource("plus_expand.gif").toURI().toURL());
				JIconButton button = new JIconButton(icon);
				button.setAlignmentX(Component.LEFT_ALIGNMENT);
				button.setAlignmentY(Component.TOP_ALIGNMENT);
				button.setCursor(new Cursor(Cursor.HAND_CURSOR));
				button.addActionListener(((LinnkProxyActions) this.getActions()).getExpandAction());
				this.jPanelContent.add(button);
			} catch (MalformedURLException e) {
				de.mxro.UserError.singelton.log(e);
				
			} catch (URISyntaxException e) {
				de.mxro.UserError.singelton.log(e);
			}*/
			this.jPanelContent.add(this.getItemPanel());
			
				
			
			final JIconButton button = new JIconButton(Icons.getSmallIcon("green_arrow_right.gif"));
			button.setAlignmentX(Component.LEFT_ALIGNMENT);
			button.setAlignmentY(Component.TOP_ALIGNMENT);
			button.setCursor(new Cursor(Cursor.HAND_CURSOR));
			button.setMaximumSize(new Dimension(20,20));
			button.setPreferredSize(new Dimension(20,20));
			//button.setBackground(Color.RED);
			//button.setOpaque(true);
			//this.setBackground(Color.BLUE);
			button.addActionListener(this.getActions().getAction(LinnkProxyActions.FollowLinkAction.class));
			this.jPanelContent.add(button);
			
			
			
		}
		
		return this.jPanelContent;
	}
	
	

	public LinnkProxyPanel() {
		super();
		this.setActions(new LinnkProxyActions(this));
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.setAlignmentY(Component.TOP_ALIGNMENT);
		
		this.add(this.getJMyPanelContent(), null);
		this.getContentPanel().add(this.getJMyPanelItems(), null);
		this.add(this.getContentPanel());
		
		
	}

	@Override
	public Item generateChangedItem(Item holdedItem) {
		return	new LinnkProxy(this.getItem().getCreator(), this.getItem().getId(), this.getItem().getDocument(), holdedItem, ( this.item).getLink(), this.item.isExpanded());
	}

	@Override
	public void updatePanel() {
		super.updatePanel();
		
		if (this.getItem().isExpanded()) {
			final Document doc = DocumentLoader.singelton.loadForDocument(this.getDocument(), this.getItem().getLink().toURI());
			this.setHoldedDocument(doc);
			
		}
	}
	
	public static class ExpandOrCollapseTransformation extends GUIItemTransformation<LinnkProxy, LinnkProxyPanel> {
		
		
		
		@Override
		protected void doWithPanelAfterwards(LinnkProxyPanel forPanel) {
			
			if (forPanel.myItemsPanel == null) {
				forPanel.getItemspanel().selectItem(forPanel.getItem().getCompleteID());
			} else {
				forPanel.myItemsPanel.selectFirst();
			}
		}

		@Override
		protected void doWithPanelBefore(LinnkProxyPanel forPanel) {
			if (forPanel.myItemsPanel != null) {
				forPanel.myItemsPanel.save();
			}
			if (forPanel.holdedDocument != null) {
				Linnk.S.saveDocument(forPanel.holdedDocument);
				
			}
		}

		@Override
		public boolean accept(Item i) {
			return i instanceof LinnkProxy;
		}
		
		@Override
		public LinnkProxy doOnItem(LinnkProxy item) {
			final LinnkProxy old = item;
			
			
			final boolean newExpanded = !item.isExpanded();
			if (old instanceof DocumentProxy)
				return new DocumentProxy(old.getCreator(), old.getId(), old.getDocument(), old.getItem(), old.getLink(), newExpanded);
			else
				return new LinnkProxy(old.getCreator(), old.getId(), old.getDocument(), old.getItem(), old.getLink(), newExpanded);
			
		}
		
	}
	
	public boolean expandOrCollapse() {
		final ItemsPanel itemsPanel = this.getItemspanel();
		final LinnkProxy old = this.getItem();
		
		if (this.getHoldedDocument() == null) {
			// Expand ...
			
			LinnkProxy proxy; 
			if (old instanceof DocumentProxy) {
				proxy = new DocumentProxy(old.getCreator(), old.getId(), old.getDocument(), old.getItem(), old.getLink(), true);
			} else {
				proxy = new LinnkProxy(old.getCreator(), old.getId(), old.getDocument(), old.getItem(), old.getLink(), true);
			}
			
			final Item newItem = de.linnk.utils.Utils.replace(this.getItem().getDocument(), old, proxy);
			final Item root = this.getItem().getDocument().getRootItem(old);
			final ItemChange newItemChange = ItemChange.newModifyItem(newItem, root, LinnkFatClient.currentUser);
			itemsPanel.doChange(newItemChange);
			((LinnkProxyPanel) itemsPanel.getItemPanelFor(newItem).getItemPanel()).getMyItemsPanel().selectFirst();
			
			de.mxro.utils.log.UserError.singelton.log("LinnkProxyActions: Expanded Document: "+proxy.getLink().link, UserError.Priority.INFORMATION);
		} else {
			// collapse
			
			this.getMyItemsPanel().save();
			Linnk.S.saveDocument(this.getHoldedDocument());
			
			LinnkProxy proxy; 
			if (old instanceof DocumentProxy) {
				proxy = new DocumentProxy(old.getCreator(), old.getId(), old.getDocument(), old.getItem(), old.getLink(), false);
			} else {
				proxy = new LinnkProxy(old.getCreator(), old.getId(), old.getDocument(), old.getItem(), old.getLink(), false);
			}
			
			final Item newItem = de.linnk.utils.Utils.replace(this.getItem().getDocument(), old, proxy);
			final Item root = this.getItem().getDocument().getRootItem(old);
			final ItemChange newItemChange = ItemChange.newModifyItem(newItem, root, LinnkFatClient.currentUser);
			itemsPanel.doChange(newItemChange);
			itemsPanel.selectItem(newItem.getCompleteID());
			de.mxro.utils.log.UserError.singelton.log("LinnkProxyActions: Collapsed Document: "+proxy.getLink().link, UserError.Priority.INFORMATION);
		}
		return true;
	}
	
	
}
