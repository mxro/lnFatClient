package de.linnk.fatclient.document.itemspanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import net.iharder.dnd.FileDrop;
import de.linnk.basispack.v05.LinnkProxyPanel;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;
import de.linnk.domain.EasyEditItem;
import de.linnk.domain.InsertItem;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.domain.TitleItem;
import de.linnk.fatclient.DropTarget;
import de.linnk.fatclient.LinnkDocumentActions;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.DocumentPanel;
import de.linnk.fatclient.document.ItemPanel;
import de.linnk.fatclient.document.ItemPanelBuilder;
import de.linnk.fatclient.document.ItemPanelContainer;
import de.linnk.fatclient.document.ProxyItemPanel;
import de.linnk.fatclient.document.itemspanel.filter.Identity;
import de.linnk.fatclient.document.itemspanel.filter.ItemPanelFilter;
import de.linnk.utils.ItemPanelContainers;
import de.linnk.utils.ItemPanels;
import de.linnk.utils.Items;
import de.mxro.swing.JMyPanel;
import de.mxro.utils.Utils;
import de.mxro.utils.log.UserError;

/**
 * contains a number of items
 * 
 * @author maxrohde
 * 
 */
public class ItemsPanel extends JMyPanel {

	private static final long serialVersionUID = 1L;

	protected final NodeDocument document;

	protected final DocumentPanel documentPanel;

	protected final ItemPanelFilter filter;

	protected ItemPanel ownerItem;

	private JMyPanel jPanelItems = null;

	private boolean dragging = false;

	private Items draggingItems;

	
	
	protected Vector<ItemPanel> getItemPanels() {
		final Vector<ItemPanel> ips = new Vector<ItemPanel>();
		for (final Component c : this.jPanelItems.getComponents()) {
			if (c instanceof ItemPanelContainer) {
				ips.add(((ItemPanelContainer) c).getItemPanel());
			}
		}
		return ips;
	}

	public boolean waitUntilNoChangesPending(int maxWaitTimeInMs) {
		return this.waitUntilNoChangesPending();
	}

	public boolean waitUntilNoChangesPending() {
		boolean noPending;
		final int maxWait = 1000;
		int wait = 0;
		String pendingItem = "";
		do {
			noPending = true;
			for (final ItemPanel p : this.getItemPanels()) {
				if (p.isChangePending()) {
					noPending = false;
					pendingItem = p.getItem().getCompleteID();
				}
			}
			wait++;
		} while (noPending == true && wait < maxWait);
		if (wait > maxWait) {
			de.mxro.utils.log.UserError.singelton
					.log(
							this,
							"waitUntilNoChangesPending: had to leave before all changes where done! Item pending: "
									+ pendingItem, UserError.Priority.NORMAL);
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String res = "ItemsPanel " + this.getDocument().getName() + "\n";
		for (final Component c : this.jPanelItems.getComponents()) {
			if (c instanceof ItemPanelContainer) {
				res = res
						+ "Item: "
						+ ((ItemPanelContainer) c).getItemPanel().getItem()
								.getId();
				if (this.getDocument().getRootItem(
						((ItemPanelContainer) c).getItemPanel().getItem()) == null) {
					res = res + " not in document any more!\n";
				} else {
					res = res + "\n";
				}
			}
		}
		return res;
	}

	public void drag(Items items) {
		if (items.size() > 0) {
			for (final Item i : items) {
				this.getItemPanelFor(i).setVisible(false);
			}
			this.dragging = true;
		}
		this.draggingItems = new Items();
		this.draggingItems.addAll(items);
	}

	public void select() {
		// de.mxro.UserError.singelton.log("select itemspanel!");
		/*
		 * if (this.getDocument() instanceof EditableDocument) {
		 * this.getDocumentPanel().getHolder().setDocumentActions(((EditableDocument)
		 * this.getDocument()).getToolBar(this) ); ((EditableDocument)
		 * this.getDocument()).setMenu(this.getDocumentPanel().getHolder().getMenu(),
		 * this); }
		 */
		this.getDocumentPanel().getHolder().getWindowInterface()
				.setActiveItemsPanel(this);
		this.getDocumentPanel().getHolder().getWindowInterface().check();
	}

	public void selectFirst() {

		// boolean first = true;
		for (final Component c : this.jPanelItems.getComponents()) {
			if (c instanceof ItemPanelContainer
					&& !(((ItemPanelContainer) c).getItemPanel().getItem() instanceof TitleItem)) {
				// de.mxro.UserError.singelton.log("found "+c);
				this.forceSelect(((ItemPanelContainer) c).getItemPanel());
				this.deselectAll(((ItemPanelContainer) c).getItemPanel()
						.getItem().getCompleteID());
				return;
			}
			// first = false;
		}
	}

	public boolean selectNext() {
		
		return this.selectNext(this.getSelectedItem());
	}

	public ItemPanelContainer nextItem(Item after) {
		final int idx = this.indexOf(after);
		if (idx >= 0 && idx < this.jPanelItems.getComponentCount() - 1) {
			final ItemPanelContainer toSelect = ((ItemPanelContainer) this.jPanelItems
					.getComponent(idx + 1));
			return toSelect;
		}
		return null;
	}

	public boolean selectNext(Item after) {
		final ItemPanelContainer nextItem = this.nextItem(after);
		if (nextItem != null) {
			this.forceSelect(nextItem.getItemPanel());
			this.deselectAll(nextItem.getItemPanel().getItem().getCompleteID());
			return true;
		}
		return false;
	}

	public boolean selectLast() {
		if (this.jPanelItems.getComponentCount() > 0) {
			final ItemPanel toSelect = ((ItemPanelContainer) this.jPanelItems
					.getComponent(this.jPanelItems.getComponentCount() - 1))
					.getItemPanel();
			this.deselectAll(toSelect.getItem().getCompleteID());
			if (toSelect instanceof LinnkProxyPanel) {
				final ItemsPanel itemsPanel = ((LinnkProxyPanel) toSelect)
						.getMyItemsPanel();
				if (itemsPanel != null)
					return itemsPanel.selectLast();
			}
			this.forceSelect(toSelect);
			return true;
		}
		return true;
	}

	public ItemPanelContainer previousItem(Item before) {
		final int idx = this.indexOf(this.getSelectedItem());
		if (idx > 0)
			return ((ItemPanelContainer) this.jPanelItems.getComponent(idx - 1));
		return null;
	}

	public boolean selectPrevious() {

		final ItemPanelContainer toSelectContainer = this.previousItem(this
				.getSelectedItem());
		if (toSelectContainer != null) {
			final ItemPanel toSelect = toSelectContainer.getItemPanel();
			if (toSelect instanceof LinnkProxyPanel) {
				final ItemsPanel itemPanels = ((LinnkProxyPanel) toSelect)
						.getMyItemsPanel();
				if (itemPanels != null)
					return itemPanels.selectLast();
			}
			this.deselectAll(toSelect.getItem().getCompleteID());
			this.forceSelect(toSelect);
			return true;
		}
		return false;
	}

	public boolean moveUp() {
		final int idx = this.indexOf(this.getSelectedItem());
		if (idx >= 1) {
			final Item selected = this.getSelectedItem();

			final Item toChangeItem = ((ItemPanelContainer) this.jPanelItems
					.getComponent(idx - 1)).getItemPanel().getItem();
			// this.exchangeItems(toChangeItem, selected);
			this.getItemPanelFor(selected).updateItem();
			this.getItemPanelFor(toChangeItem).updateItem();
			if (!this.waitUntilNoChangesPending(200))
				return false;
			final ItemChange exchangeItems = ItemChange.newExchangeItems(
					toChangeItem, selected, LinnkFatClient.currentUser);
			this.doChange(exchangeItems);

			this.waitUntilNoChangesPending(200);
			this.getItemPanelFor(selected).updatePanel();
			this.getItemPanelFor(toChangeItem).updatePanel();
			this.selectItem(selected.getCompleteID());

			return true;
		}
		return false;
	}

	public boolean moveDown() {
		final int idx = this.indexOf(this.getSelectedItem());
		if (idx >= 0 && idx < this.jPanelItems.getComponentCount() - 1) {
			final Item selected = this.getSelectedItem();

			final Item toChangeItem = ((ItemPanelContainer) this.jPanelItems
					.getComponent(idx + 1)).getItemPanel().getItem();
			// this.exchangeItems(toChangeItem, selected);
			this.getItemPanelFor(selected).updateItem();
			this.getItemPanelFor(toChangeItem).updateItem();
			if (!this.waitUntilNoChangesPending(200))
				return false;
			final ItemChange exchangeItems = ItemChange.newExchangeItems(
					toChangeItem, selected, LinnkFatClient.currentUser);
			this.doChange(exchangeItems);

			this.waitUntilNoChangesPending(200);
			this.getItemPanelFor(selected).updatePanel();
			this.getItemPanelFor(toChangeItem).updatePanel();
			this.selectItem(selected.getCompleteID());
			return true;
		}
		return false;
	}

	public boolean selectItem(String itemID) {
		final Item item = this.getDocument().getItem(itemID);
		if (item == null) {
			UserError.singelton.log(this, "selectItem: itemID not found: "
					+ itemID, UserError.Priority.NORMAL);
			return false;
		}
		return this.selectItem(item);

	}

	private boolean selectItem(Item item) {
		final int idx = this.indexOf(item);
		if (idx >= 0) {
			this.forceSelect(((ItemPanelContainer) this.jPanelItems
					.getComponent(idx)).getItemPanel());
			return true;
		} else
			return false;

	}

	public void deselect() {
		this.deselectAll();
	}

	public void deselectAll() {
		this.deselectAll(null);
	}

	public void deselectAll(String insteadofID) {
		if (insteadofID == null)
			return;
		final Item insteadof = this.getDocument().getItem(insteadofID);
		if (insteadof == null) {
			de.mxro.utils.log.UserError.singelton
					.log(this, "deselectAll: insteadof " + insteadofID
							+ "item not found", UserError.Priority.NORMAL);

		}
		ItemPanelContainer insteadofContainer = null;
		if (insteadof != null) {
			insteadofContainer = this.getItemPanelFor(this.getDocument()
					.getRootItem(insteadof));
		}
		final Component[] comps = this.jPanelItems.getComponents();
		if (comps.length > 0) {
			for (final Component element : comps) {
				if (element instanceof ItemPanelContainer
						&& !((insteadof != null) && ((ItemPanelContainer) element)
								.getItemPanel().getItem().getId().equals(
										insteadof.getId()))) {
					((ItemPanelContainer) element).setSelected(false);
				}
			}
		}
		if (insteadofContainer != null) {
			insteadofContainer.setSelected(true);
		}
	}

	public void forceSelect(ItemPanel icp) {
		// this.deselectAll();
		final int idx = this.indexOf(icp.getItem());
		if (idx >= 0) {
			final ItemPanelContainer toSelect = ((ItemPanelContainer) this.jPanelItems
					.getComponent(idx));
			toSelect.setSelected(true);
			this.scrollRectToVisible(toSelect.getBounds());
		}
		this.select();
	}

	public void forceDeselect(ItemPanel icp) {
		final int idx = this.indexOf(icp.getItem());
		if (idx >= 0) {
			((ItemPanelContainer) this.jPanelItems.getComponent(idx))
					.setSelected(false);
		}
	}

	public void save() {
		final Component[] comps = this.jPanelItems.getComponents();
		if (comps.length > 0) {
			for (final Component element : comps) {
				if (element instanceof ItemPanelContainer) {
					((ItemPanelContainer) element).updateItem();
					((ItemPanelContainer) element).save();
				}
			}
		}
	}

	public int indexOf(Item item) {
		final Component[] comps = this.jPanelItems.getComponents();
		if (comps.length > 0) {
			for (int i = 0; i < comps.length; i++) {
				if (comps[i] instanceof ItemPanelContainer) {
					if (((ItemPanelContainer) comps[i]).getItemPanel()
							.getItem().getId().equals(item.getId()))
						return i;
				}
			}
		}
		return -1;
	}

	public ItemPanelContainer getItemPanelForm(String itemId) {
		final Component[] comps = this.jPanelItems.getComponents();
		if (comps.length > 0) {
			for (final Component element : comps) {
				if (element instanceof ItemPanelContainer) {
					Item i = ((ItemPanelContainer) element).getItemPanel()
							.getItem();
					if (i.getId().equals(itemId)) {
						return (ItemPanelContainer) element;
					}
				}
			}
		}
		return null;
	}

	public ItemPanelContainer getItemPanelFor(Item item) {
		final Item normalizedItem = this.getDocument().getItem(
				item.getCompleteID());
		if (normalizedItem == null)
			return null;
		if (this.indexOf(normalizedItem) >= 0)
			return (ItemPanelContainer) this.getJPanelItems().getComponent(
					this.indexOf(item));
		if (this.getDocument().getRootItem(item) != null) {
			if (this.getDocument().getRootItem(item) == item) {
				UserError.singelton.log(this, "getItemPanelFor: item "
						+ item.getId() + " was not found in ItemsPanel!",
						UserError.Priority.HIGH);
				return null;
			}

			final ItemPanel rootPanel = this.getItemPanelFor(
					this.getDocument().getRootItem(item)).getItemPanel();
			if (!(rootPanel instanceof ProxyItemPanel)) {
				UserError.singelton
						.log(
								this,
								"getItemPanelFor: item should not be the root if not found directly",
								UserError.Priority.HIGH);
				return null;
			}
			return ((ProxyItemPanel) rootPanel).getItemPanelFor(item);
		}
		return null;
	}

	public ItemPanelContainers getSelectedItemPanelContainers() {
		final Component[] comps = this.jPanelItems.getComponents();
		final ItemPanelContainers containers = new ItemPanelContainers();
		if (comps.length > 0) {
			for (final Component element : comps) {
				if (element instanceof ItemPanelContainer) {
					if (((ItemPanelContainer) element).isSelected()) {
						containers.add(((ItemPanelContainer) element));
					}

				}
			}
		}
		return containers;
	}

	public ItemPanelContainer getItemPanelContainer() {
		if (this.getSelectedItemPanelContainers().size() == 1)
			return this.getSelectedItemPanelContainers().firstElement();
		return null;
	}

	public ItemPanels getSelectedItemPanels() {
		final ItemPanelContainers selected = this
				.getSelectedItemPanelContainers();
		return selected.getItemPanels();
	}

	public ItemPanel getSelectedItemPanel() {
		if (this.getSelectedItemPanels().size() == 1)
			return this.getSelectedItemPanels().firstElement();
		return null;

	}

	public Items getSelectedItems() {
		return this.getSelectedItemPanels().getItems();
	}

	public Item getSelectedItem() {
		if (this.getSelectedItems().size() == 1)
			return this.getSelectedItems().firstElement();
		return null;
	}

	public NodeDocument getDocument() {
		return this.document;
	}

	boolean remove(String itemID) {
		Item item = this.getDocument().getItem(itemID);
		if (item == null) {
			item = this.getItemPanelForm(itemID).getItemPanel().getItem();
		}
		if (item == null) {
			UserError.singelton.log(this,
					"remove: Could not find Item to delete: " + itemID,
					UserError.Priority.NORMAL);
			return false;
		}
		return this.remove(item);
	}

	private boolean remove(Item item) {
		if (this.indexOf(item) > -1) {
			this.jPanelItems.remove(this.indexOf(item));
			this.revalidate();
			this.checkEmpty();
			return true;
		}
		if (item instanceof EasyEditItem) {
			UserError.singelton.log(
					"ItemsPanel.deleteItemPanel: Could not find Item to delete: "
							+ item.getId(), UserError.Priority.HIGH);
		}
		return true;
	}

	private static class SelectionGlassPane extends JComponent {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Point p1;

		public Point p2;

		public Point offset;

		@Override
		protected void paintComponent(Graphics g) {

			g.setColor(Color.BLACK);
			if (g instanceof Graphics2D) {
				((Graphics2D) g).setStroke(new BasicStroke(1f,
						BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f,
						new float[] { 2f }, 0f));
			}
			final Rectangle rect = Utils.normalizeRectangle(this.p1.x
					+ this.offset.x, this.p1.y + this.offset.y, this.p2.x
					- this.p1.x, this.p2.y - this.p1.y);
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
			// System.out.println("draw: "+(p1.x+offset.x) +" "+
			// (p1.y+offset.y)+" "+ (p2.x-p1.x)+" "+ (p2.y-p1.y));

		}

		public SelectionGlassPane() {
			super();

		}

	}

	private final SelectionGlassPane selectionGlassPane = new SelectionGlassPane();;

	private class ChildrenMouseInput implements MouseInputListener {
		private boolean selecting = false;

		private Point selectionStart;

		public void mouseDragged(MouseEvent arg0) {
			final Point containerPoint = SwingUtilities.convertPoint(arg0
					.getComponent(), new Point(arg0.getX(), arg0.getY()),
					ItemsPanel.this);

			// System.out.println("here!");

			if (!this.selecting) {
				this.selecting = true;
				this.selectionStart = containerPoint;
				ItemsPanel.this.selectionGlassPane.offset = SwingUtilities
						.convertPoint(ItemsPanel.this, 0, 0, ItemsPanel.this
								.getRootPane());
				// System.out.println("offset; "+selectionGlassPane.offset);
				ItemsPanel.this.selectionGlassPane.setVisible(true);

			}
			// TODO Auto-generated method stub
			if (ItemsPanel.this.dragging)
				return;

			ItemsPanel.this.selectionGlassPane.setVisible(true);
			ItemsPanel.this.selectionGlassPane.p1 = this.selectionStart;
			ItemsPanel.this.selectionGlassPane.p2 = containerPoint;
			ItemsPanel.this.selectionGlassPane.validate();
			ItemsPanel.this.selectionGlassPane.addMouseListener(this);
			ItemsPanel.this.getRootPane().setGlassPane(
					ItemsPanel.this.selectionGlassPane);

			// System.out.println("mousemotion: "+arg0.getX()+" :
			// "+arg0.getY());
			// System.out.println("motionrel rel: "+containerPoint.x+" :
			// "+containerPoint.y);
			// System.out.println(ItemsPanel.this.getComponentAt(containerPoint));
			// System.out.println((SwingUtilities.getDeepestComponentAt(ItemsPanel.this,
			// containerPoint.x, containerPoint.y)));

			// Component comp =
			// (SwingUtilities.getDeepestComponentAt(ItemsPanel.this,
			// containerPoint.x, containerPoint.y));

			/*
			 * if (comp instanceof ItemPanelContainer) {
			 * //System.out.println("found ItemPanel :"+((ItemPanelContainer)
			 * comp).getItemPanel().getItem().getId()+" "+arg0.getX()+" :
			 * "+arg0.getY());
			 * 
			 * ((ItemPanelContainer) comp).mouseDragged(arg0); return; }
			 * 
			 * for (Component c : ItemsPanel.this.jPanelItems.getComponents()) {
			 * if (c instanceof ItemPanelContainer) { if
			 * (SwingUtilities.isDescendingFrom(comp, c)) {
			 * ((ItemPanelContainer) c).mouseDragged(arg0); return; } } }
			 */
		}

		public void mouseMoved(MouseEvent arg0) {
		}

		public void mouseClicked(MouseEvent arg0) {
			ItemsPanel.this.getDocumentPanel().getHolder().getWindowInterface()
					.setActiveItemsPanel(ItemsPanel.this);
		}

		public void mouseEntered(MouseEvent arg0) {
		}

		public void mouseExited(MouseEvent arg0) {
		}

		public void mousePressed(MouseEvent arg0) {
		}

		public void mouseReleased(MouseEvent arg0) {
			this.selecting = false;

			ItemsPanel.this.selectionGlassPane.setVisible(false);

			final Point containerPoint = SwingUtilities.convertPoint(arg0
					.getComponent(), new Point(arg0.getX(), arg0.getY()),
					ItemsPanel.this);
			if (containerPoint == null || this.selectionStart == null)
				return;

			final Vector<Component> comps = Utils.getComponentsInArea(
					ItemsPanel.this.getJPanelItems(), this.selectionStart,
					containerPoint);

			// System.out.println( selectionStart+ " : "+ containerPoint);
			boolean selected = false;
			for (final Component c : comps) {
				if (c instanceof ItemPanelContainer) {
					if (!selected) {
						ItemsPanel.this.deselectAll();
						selected = true;
					}
					ItemsPanel.this.forceSelect(((ItemPanelContainer) c)
							.getItemPanel());

				}
			}
		}

	}

	private JLabel EMPTY_LABEL;

	public void checkEmpty() {
		if (this.EMPTY_LABEL != null
				&& this.jPanelItems.getComponentCount() == 2) {
			this.jPanelItems.remove(this.EMPTY_LABEL);
			this.EMPTY_LABEL = null;
		}
		if (this.jPanelItems.getComponentCount() == 0
				&& this.EMPTY_LABEL == null) {
			this.EMPTY_LABEL = new JLabel(
					"Click to add text item or press ctrl+t.");
			this.EMPTY_LABEL.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent arg0) {
					ItemsPanel.this.select();
					ItemsPanel.this
							.getDocumentPanel()
							.getHolder()
							.getWindowInterface()
							.getLinnkDocumentActions()
							.getAction(
									LinnkDocumentActions.NewTextItemAction.class)
							.actionPerformed(null);
				}

				public void mouseEntered(MouseEvent arg0) {
				}

				public void mouseExited(MouseEvent arg0) {
				}

				public void mousePressed(MouseEvent arg0) {
				}

				public void mouseReleased(MouseEvent arg0) {
				}

			});
			this.jPanelItems.add(this.EMPTY_LABEL);
			this.EMPTY_LABEL.setVisible(true);
		}
	}

	ItemPanelContainer createPanel(Item forItem) {
		ItemPanelContainer ip = ItemPanelBuilder.singelton.createPanel(forItem,
				this);
		if (ip != null) {
			ip = this.filter.apply(ip);
			if (ip != null) {
				ip.updatePanel();
				de.mxro.utils.Utils.addMouseInputListenerToAllChildComponents(ip,
						new ChildrenMouseInput());

				ip.setSelected(false);
			}
		}
		return ip;
	}

	boolean appendItemPanel(Item item) {
		return insertItemPanel(item, null) != null;
	}

	ItemPanelContainer insertItemPanel(Item item, Integer index) {
		final ItemPanelContainer ip = this.createPanel(item);
		if (ip != null) {
			if (index == null) {
				this.jPanelItems.add(ip, null);
			} else {
				this.jPanelItems.add(ip, index.intValue());
			}
			ip.updatePanel();
		} // else {
		// UserError.singelton.log("ItemsPanel.addItem: Could not create
		// container for: "+item.getClass().getName(), UserError.Priority.LOW);
		this.checkEmpty();
		return ip;
	}

	public boolean changeItem(Item newitem, String oldItemID) {
		final Item oldItem = this.getDocument().getItem(oldItemID);
		if (oldItem == null) {
			de.mxro.utils.log.UserError.singelton.log(this,
					"changeItem: item to change not found " + oldItemID,
					UserError.Priority.NORMAL);
			return false;
		}
		return this.changeItem(newitem, oldItem);
	}

	private boolean changeItem(Item newitem, Item olditem) {

        // if item is not displayed by ItemsPanel, ignore operation
		if (!(newitem instanceof EasyEditItem)) {
			return true;
		}
		
		final int oldindex = this.indexOf(olditem);

		if (oldindex > -1) {

			final ItemPanelContainer ip2 = this.getItemPanelFor(olditem);
			if (ip2 == null) {
				UserError.singelton
						.log(
								this,
								"changeItem: ItemPanel for to change item could not be found!",
								UserError.Priority.NORMAL);
				return false;
			}
			// if ( ip2 != null) {
			// ip2.getItemPanel().outdated();
			// }
			this.getJPanelItems().remove(oldindex);

			final ItemPanelContainer ip = this.insertItemPanel(newitem,
					oldindex);
			if (ip == null) {
				UserError.singelton.log(this,
						"changeItem: newly added item could not be found!",
						UserError.Priority.NORMAL);
				return false;
			}

			// this.getJPanelItems().add(ip, oldindex);
			// System.out.println("added:
			// "+ip.getItemPanel().getItem().getId());
			// if the panel was selected befor, it should remain so ...
			// System.out.println("added panel for "+newitem.getCompleteID());
			if (ip2 != null) {
				ip.setSelected(ip2.isSelected());
			}
			ip.updatePanel();
			return true;
		}

		de.mxro.utils.log.UserError.singelton.log(
				"warning: called ItemsPanel.changeItem with invalid olditem",
				UserError.Priority.NORMAL);
		return false;
		// }

		// de.mxro.UserError.singelton.log("warning: called
		// ItemsPanel.changeItem with invalid newItem
		// "+newitem.getClass().getName(), UserError.Priority.NORMAL);
		// return true;
	}

	boolean exchangeItems(String item1ID, String item2ID) {
		final Item item1 = this.getDocument().getItem(item1ID);
		final Item item2 = this.getDocument().getItem(item2ID);
		if (item1 == null || item2 == null) {
			de.mxro.utils.log.UserError.singelton.log(this,
					"exchangeItems: one of items " + item1ID + ", " + item2ID
							+ " nor found", UserError.Priority.NORMAL);
		}
		return this.exchangeItems(item1, item2);
	}

	private boolean exchangeItems(Item item1, Item item2) {
		final int index1 = this.indexOf(item1);
		final int index2 = this.indexOf(item2);
		if (!(index1 >= 0 && index2 >= 0)) {
			UserError.singelton.log(
					"ItemsPanel.exchange Items: items not found",
					UserError.Priority.HIGH);
			return false;
		}
		final Component comp1 = this.getJPanelItems().getComponent(index1);
		final Component comp2 = this.getJPanelItems().getComponent(index2);

		this.getJPanelItems().remove(comp2);
		this.getJPanelItems().add(comp2, index1);
		this.getJPanelItems().remove(comp1);
		this.getJPanelItems().add(comp1, index2);
		return true;
	}

	boolean insertItem(Item newitem, String relativeItemID,
			InsertItem.Position position) {
		final Item relativeItem = this.getDocument().getItem(relativeItemID);
		if (relativeItem == null) {
			de.mxro.utils.log.UserError.singelton.log(this, "insertItem: relative Item "
					+ relativeItemID + "not found", UserError.Priority.NORMAL);
			return false;
		}
		return this.insertItem(newitem, relativeItem, position);
	}

	private boolean insertItem(Item newitem, Item relativeItem,
			InsertItem.Position position) {
		final ItemPanelContainer ip = this.createPanel(newitem);
		if (ip != null) {
			final int relativeIndex = this.indexOf(relativeItem);
			if (relativeIndex == -1) {
				de.mxro.utils.log.UserError.singelton.log(
						"ItemsPanel.insertItem: relative Item not found",
						UserError.Priority.NORMAL);
				return false;
			}
			int newIndex;
			if (position.equals(InsertItem.Position.AFTER)) {
				newIndex = relativeIndex + 1;
			} else {
				newIndex = relativeIndex;
			}

			this.getJPanelItems().add(ip, newIndex);
			ip.updatePanel();
			// this.updatePanels();
			return true;

		}

		// de.mxro.UserError.singelton.log("warning: called
		// ItemsPanel.changeItem with invalid newItem
		// "+newitem.getClass().getName(), UserError.Priority.NORMAL);
		return true;
	}

	public boolean doChange(ItemChange ic) {

		
			final ItemPanelChange ipc = ItemPanelChange.createFromItemChange(ic);
			if (ipc != null && !ipc.doOnItemsPanel(this)) {
				UserError.singelton.log(this,
						"doChange: Could not apply change on items panel : "
								+ ipc.getClass(), UserError.Priority.NORMAL);
				return false;
			}
			this.validate();
		

		if (!this.getDocument().doChange(ic)) {
			UserError.singelton.log(this,
					"doChange: Could not apply change on document: "
							+ ic.getClass(), UserError.Priority.NORMAL);
			return false;
		}

		return true;
	}

	public ItemsPanel(Document document, DocumentPanel documentPanel) {
		this(document, documentPanel, new Identity());
	}

	/**
	 * This is the default constructor
	 */
	public ItemsPanel(Document document, DocumentPanel documentPanel,
			ItemPanelFilter filter) {
		super();
		this.initialize();
		this.filter = filter;
		this.document = (NodeDocument) document;
		this.documentPanel = documentPanel;

		this.getDocumentPanel().getHolder().getWindowInterface()
				.getLinnkDocumentActions().addKeyboardShortcuts(this);
		// this.setMinimumSize(new Dimension(0, 130));
		// this.setPreferredSize(new Dimension(0, 130));
		final List<Item> items = document.getItems();
		if (items.size() > 0) {
			for (int i = 0; i < items.size(); i++) {
				this.appendItemPanel(items.get(i));

			}
		}

		// updatePanels();
		// new FileDrop(this,
		// this.getDocumentPanel().getHolder().getWindowInterface().getLinnkDocumentActions().getFileDropListener());
		if (this.getDocumentPanel().getHolder().getFileDropTargetComponent() != null) {
			/*new FileDrop(this.getDocumentPanel().getHolder()
					.getFileDropTargetComponent(), this.getDocumentPanel()
					.getHolder().getWindowInterface().getLinnkDocumentActions()
					.getFileDropListener());*/
            // TODO
            DropTarget target = 
                    new DropTarget(this.getDocumentPanel().getHolder().getWindowInterface().getLinnkDocumentActions(),
                    this.getDocumentPanel().getHolder()
					.getFileDropTargetComponent());

		}
		this.select();
		this.checkEmpty();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(new Dimension(443, 68));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.add(this.getJPanelItems(), null);

	}

	/**
	 * This method initializes jPanelItems
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelItems() {
		if (this.jPanelItems == null) {
			this.jPanelItems = new JMyPanel();
			this.jPanelItems.setLayout(new BoxLayout(this.getJPanelItems(),
					BoxLayout.Y_AXIS));
		}
		return this.jPanelItems;
	}

	public DocumentPanel getDocumentPanel() {
		return this.documentPanel;
	}

	public void updateItems() {
		for (final ItemPanel p : this.getItemPanels()) {
			p.updateItem();
		}

	}

	public void updatePanels() {
		final Component[] comps = this.jPanelItems.getComponents();
		if (comps.length > 0) {
			for (final Component element : comps) {
				if (element instanceof ItemPanelContainer) {
					((ItemPanelContainer) element).updatePanel();
				}
			}
		}
	}

	public ItemPanel getOwnerItem() {
		return this.ownerItem;
	}

	public void setOwnerItem(ItemPanel ownerItem) {
		this.ownerItem = ownerItem;
	}

} // @jve:decl-index=0:visual-constraint="65,83"
