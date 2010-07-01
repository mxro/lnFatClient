package de.linnk.fatclient.transfer;

import java.awt.event.InputEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import de.linnk.fatclient.document.ItemPanelContainer;
import de.mxro.utils.log.UserError;

public class ContainerTransferHandler extends TransferHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void exportAsDrag(JComponent comp, InputEvent e, int action) {
		if (!(comp instanceof ItemPanelContainer)) {
			UserError.singelton.log("ContainerTransferHandler: not supported!", UserError.Priority.HIGH);
			return;
		}
		
		super.exportAsDrag(comp, e, action);
	}
	
}
