package de.linnk.extpack.v03;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import de.linnk.domain.Item;
import de.linnk.fatclient.document.ItemPanel;
import de.mxro.swing.JImage;

public class JPEGPictureItemPanel extends ItemPanel<JPEGPictureItem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4702577902736506711L;

	private JImage image=null;
	

	
	protected JImage getImage() {
		if (this.image==null && this.getItem() != null) {
			this.image = new JImage((this.getItem()).getImage());
			//this.setLayout(null);
			//this.setMaximumSize(new Dimension(Short.MAX_VALUE, image.getSize().height));
			//UserError.singelton.log("set Miminum Size "+image.getSize());
			//this.setMinimumSize(image.getSize());
			//image.setMinimumSize(image.getSize());
			
			this.add(this.image);
			
			this.image.addMouseListener(new MouseListener() {
				public void mouseEntered(MouseEvent evt) {
				}
				public void mouseExited(MouseEvent evt) {
				}
				public void mouseReleased(MouseEvent evt) {
				}
				public void mouseClicked(MouseEvent evt) {
					JPEGPictureItemPanel.this.forceSelect();
				}
				public void mousePressed(MouseEvent evt) {
				}
				
			});
			// UserError.singelton.log("Jpegpictureitempanel "+ image.toString());
		}
		return this.image;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

	@Override
	public Item generateChangedItem() {
		return this.item;

	}

	@Override
	public void updatePanel() {
		this.getImage();
	}
	
	@Override
	public void setItem(JPEGPictureItem item) {
		super.setItem(item);
		
		this.updatePanel();
	}
	

}
