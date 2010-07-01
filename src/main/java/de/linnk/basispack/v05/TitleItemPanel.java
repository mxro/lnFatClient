package de.linnk.basispack.v05;

import java.awt.Dimension;
import java.awt.event.FocusEvent;

import javax.swing.BoxLayout;
import javax.swing.JTextField;

import de.linnk.domain.Item;
import de.linnk.domain.TitleItem;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.ItemPanel;

public class TitleItemPanel extends ItemPanel<TitleItem> {

	private static final long serialVersionUID = 1L;
	private JTextField jTextFieldTitle = null;
	


    @Override
	public void save() {
		// noop
	}

	@Override
	public Item generateChangedItem() {
		
		if ((this.getItem()).getTitle().equals( this.jTextFieldTitle.getText()))
			return null;
		
		final TitleItem changedItem = ((NodeDocument) this.item.getDocument()).newTitleItem(this.getItem().getCreator(), this.jTextFieldTitle.getText());
		return changedItem;
		
	}

	@Override
	public void updatePanel() {
		this.jTextFieldTitle.setText((this.item).getTitle());
		// this.revalidate();
	}

	
	
@Override
	public void setItem(TitleItem item) {
		super.setItem(item);
		assert item instanceof TitleItem;
		this.updatePanel();
	}

/** This is the default constructor
	 */
	public TitleItemPanel() {
		super();
		
		this.initialize();
		
		
		
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(422, 31);
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.add(this.getJTextFieldTitle(), null);
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, this.getJTextFieldTitle().getPreferredSize().height+4));
		this.setMinimumSize(new Dimension(Short.MAX_VALUE, this.getJTextFieldTitle().getPreferredSize().height+4));
	}

	/**
	 * This method initializes jTextFieldTitle	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldTitle() {
		if (this.jTextFieldTitle == null) {
			this.jTextFieldTitle = new JTextField();
			this.jTextFieldTitle.setFont(LinnkFatClient.largeFont);
			
			this.jTextFieldTitle.addFocusListener(new java.awt.event.FocusAdapter() {
				@Override
				public void focusLost(java.awt.event.FocusEvent e) {
					// save changes
					TitleItemPanel.this.updateItem();
				}

				@Override
				public void focusGained(FocusEvent arg0) {
					
					super.focusGained(arg0);
				}
				
				
			});
		}
		return this.jTextFieldTitle;
	}
	
	
	
	
}  //  @jve:decl-index=0:visual-constraint="26,24"
