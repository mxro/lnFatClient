package de.linnk.fatclient.document;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;

import de.linnk.domain.Item;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.mxro.swing.JIconButton;
import de.mxro.swing.JMyPanel;

public class ItemPanelContainer extends JMyPanel {

	private static final long serialVersionUID = 1L;
	private JMyPanel jPanelContent = null;
	protected JIconButton iconButton; 
	
	protected final ItemsPanel itemspanel;
	protected final ItemPanel contentPanel;
	protected boolean selected;
	
	public void updatePanel() {
		this.contentPanel.updatePanel();
	}
	public final Item updateItem() {
		return this.contentPanel.updateItem();
	}
	public  void save() { // just necessary for document items, but ...
		this.contentPanel.save();
	}
	

	public ItemsPanel getItemspanel() {
		return this.itemspanel;
	}
	public ItemPanel getItemPanel() {
		return this.contentPanel;
	}
	
	/**
	 * This is the default constructor
	 */
	/*private ItemPanel() {
		super();
		initialize();
	}*/
	
	public ItemPanelContainer (ItemsPanel itemspanel, ItemPanel contentPanel) {
		super();
		
		this.itemspanel = itemspanel;
		this.setOpaque(true);
		this.setBackground(Color.WHITE);
		this.contentPanel = contentPanel;
		this.initialize();
	}

	
	
	/*private class MouseAd implements MouseMotionListener {

		public void mouseDragged(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
			Point containerPoint = SwingUtilities.convertPoint(
                    arg0.getComponent(),
                    new Point(arg0.getX(), arg0.getY()),
                    ItemPanelContainer.this);
			//System.out.println("dragged: "+arg0.getX()+" : "+arg0.getY());
			//System.out.println("dragged rel: "+containerPoint.x+" : "+containerPoint.y);
			//System.out.println(ItemPanelContainer.this.getComponentAt(containerPoint));
			//System.out.println((SwingUtilities.getDeepestComponentAt(ItemPanelContainer.this, arg0.getX(), arg0.getY()).getName()));
			if (ItemPanelContainer.this.getComponentAt(containerPoint) == null) {
				//System.out.println("dragged: "+arg0.getX()+" : "+arg0.getY());
			}
			
		}

		public void mouseMoved(MouseEvent arg0) {
			//System.out.println("move: "+arg0.getX()+" : "+arg0.getY());
			
		}
		
	}*/
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(192, 95);
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.setAlignmentY(Component.TOP_ALIGNMENT);
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		
		//ItemPanelContainer.SELECTED_COLOR = Linnk.application.getSettings().selectedColor;
		
		//this.add(this.getJPanelBorder(), null);
		
		if (this.contentPanel != null) {
			this.iconButton = new JIconButton(this.contentPanel.getIcon());
			
			//iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.X_AXIS));
			//iconPanel.setSize(30, 30);
			this.iconButton.setPreferredSize(new Dimension(20, 20));
			this.iconButton.setMaximumSize(new Dimension(20, 20));
			this.iconButton.setMinimumSize(new Dimension(20, 20));
			this.iconButton.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.iconButton.setAlignmentY(Component.TOP_ALIGNMENT);
			
			this.iconButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					if (ItemPanelContainer.this.getItemPanel().getActions() == null)
						return;
					
					ItemPanelContainer.this.getItemPanel().getActions().showPopup(
							ItemPanelContainer.this, ItemPanelContainer.this.getX(), ItemPanelContainer.this.getY());
					
				}
				
			});
			
			this.contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.contentPanel.setAlignmentY(Component.TOP_ALIGNMENT);
			//this.contentPanel.setBackground(Color.BLUE);
			
			
			this.getJPanelContent().add(this.contentPanel);
			
			this.add(this.iconButton);
			this.add(this.getJPanelContent(), null);
			
		}
		
		//de.mxro.Utils.addMouseMotionListenerToAllChildComponents(this, new MouseAd());
	}

	
	
	public void mouseDragged(MouseEvent arg0) {
		//this.getItemPanel().forceSelect();
		//this.getItemspanel().selectItem(this.getContentPanel().getItem());
	}
	
	/**
	 * This method initializes jPanelContent	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelContent() {
		if (this.jPanelContent == null) {
			this.jPanelContent = new JMyPanel();
			this.jPanelContent.setLayout(new BoxLayout(this.getJPanelContent(), BoxLayout.X_AXIS));
			this.jPanelContent.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.jPanelContent.setAlignmentY(Component.TOP_ALIGNMENT);
		}
		return this.jPanelContent;
	}

	

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ItemPanelContainer &&
		((ItemPanelContainer) obj).getItemPanel().equals(this.getItemPanel());
	}
	
	public boolean isSelected() {
		return this.selected;
	}
	
	
	
	// old Border: new DotDashedBorder(DotDashedBorder.DASH_SHORT, 1, Color.BLACK, Color.WHITE)
	// public static final Border STANDARD_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0), BorderFactory.createCompoundBorder(new DotDashedBorder(DotDashedBorder.DASH_SHORT, 1, Color.BLACK, Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(3, 3, 3, 3)));
	 public static final Border SELECTED_BORDER = 
		 BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0), 
				 BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(
                         3, 3, 3, 3, LinnkFatClient.application.getSettings().selectedColor), 
						 BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), 
								 BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(),//BorderFactory.createLineBorder(Color.LIGHT_GRAY),
										 BorderFactory.createEmptyBorder(1, 1, 1, 1)))));
	
	 public static final Border STANDARD_BORDER = 
		 BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0), 
				 BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3), 
						 BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), 
								 BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(),//BorderFactory.createLineBorder(Color.LIGHT_GRAY),
										 BorderFactory.createEmptyBorder(1, 1, 1, 1)))));
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		if (this.contentPanel != null) {
			this.contentPanel.setSelected(selected);
		}
		if (selected) {
			
			this.setBorder(SELECTED_BORDER);
			this.setBackground(LinnkFatClient.application.getSettings().selectedColor);
		} else {
			this.setBorder(STANDARD_BORDER);
			this.setBackground(Color.WHITE);
		}
	}
	
	
	
	

}  //  @jve:decl-index=0:visual-constraint="104,74"
