package de.linnk.extpack.v03;

import java.awt.Component;
import java.awt.Cursor;

import javax.swing.BoxLayout;
import javax.swing.Icon;

import de.linnk.domain.Item;
import de.linnk.fatclient.document.ProxyItemPanel;
import de.linnk.fatclient.icons.Icons;
import de.mxro.swing.JIconButton;
import de.mxro.swing.JMyPanel;


public class HyperlinkProxyPanel extends ProxyItemPanel<HyperlinkProxy>  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//private JLink openLink = null;

	
	
	@Override
	public Item generateChangedItem(Item holdedItem) {
		return 
		//	super.changeEnclosedItemInDocument(changedItem) &
			new HyperlinkProxy(this.getItem().getCreator(), this.getItem().getId(), this.getItem().getDocument(), holdedItem, (this.item).getHyperlink());
	}

	
	public HyperlinkProxyPanel() {
		super();
		this.setActions(new HyperlinkProxyActions(this));
		
		final JMyPanel panel = new JMyPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		//this.add(this.getActionsPanel());
		panel.add(this.getItemPanel());

		final JIconButton button = new JIconButton(Icons.getSmallIcon("green_arrow_right.gif"));
		button.setAlignmentX(Component.LEFT_ALIGNMENT);
		button.setAlignmentY(Component.TOP_ALIGNMENT);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.addActionListener(this.getActions().getAction(HyperlinkProxyActions.OpenLinkAction.class));
		panel.add(button);
		
		this.add(panel);
		
		
	}
	
	
	@Override
	public Icon getIcon() {
		return Icons.getSmallIcon("paper-clip2.gif");
	}


	@Override
	public boolean isProxyChanged() {
		
		return false;
	}

	
	
}
