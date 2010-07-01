package de.linnk.fatclient.document;

import java.awt.Component;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

public abstract class ItemActions<P extends ItemPanel> {

	private final Vector<ItemAction<P>> actions;
	private final P panel;
	
	
	
	public final P getPanel() {
		return this.panel;
	}

	public final ItemAction<P> getAction(Class clazz) {
		for (final ItemAction<P> a : this.actions) {
			if (a.getClass().equals(clazz))
				return a;
		}
		return null;
	}
	
	public void addAction( ItemAction<P> action) {
		this.getActions().add(action);
	}

	
	public final Vector<ItemAction<P>> getActions() {
		return this.actions;
	}

	public ItemActions(P panel) {
		super();
		this.panel = panel;
		this.actions = new Vector<ItemAction<P>>();
		
	}
	
	public void addKeyboardShortcuts(JComponent comp) {
		de.mxro.utils.Utils.addKeyboardShortcuts(this.getActions(), comp);
	}
	
	public void addActionsToToolbar(JToolBar  toolbar) {
		for ( final AbstractAction action : this.getActions() ) {
			toolbar.add(action);
		}
	}
	private final Vector<JMenuItem> menuItems = new Vector<JMenuItem>();
	
	public void showPopup(Component arg0, int arg1, int arg2) {
		if (this.getActions().size() >0) {
			this.getPopupMenu().show(arg0, arg1, arg2);
		}
	}
	
	public JPopupMenu getPopupMenu() {
		final JPopupMenu res = new JPopupMenu();
		this.addActionsToMenu(res);
		return res;
	}
	
	public void addActionsToMenu(JPopupMenu menu) {
		if (menu == null)
			return;
		
		for ( final AbstractAction action :  this.getActions() ) {
			final JMenuItem item = new JMenuItem(action);
			this.menuItems.add(item);
			menu.add(item);
		}
	}
	public void removeActionsFromMenu(JPopupMenu menu) {
		if (menu == null)
			return;
		
		for (final JMenuItem item : this.menuItems ) {
			
			menu.remove(item);
		}
		this.menuItems.clear();
	}
}
