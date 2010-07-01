package de.linnk.fatclient.transfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class ItemsTransferHandler extends TransferHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
		for (final DataFlavor df : arg1) {
			if (!(df instanceof ItemsDataFlavor))
				return false;
		}
		return arg1.length > 0;
	}

	@Override
	public void exportAsDrag(JComponent arg0, InputEvent arg1, int arg2) {
		System.out.println("ItemsTransferHandler: not suppoerted");
		super.exportAsDrag(arg0, arg1, arg2);
	}

	@Override
	public boolean importData(JComponent arg0, Transferable arg1) {
		
		return super.importData(arg0, arg1);
	}
	
}
