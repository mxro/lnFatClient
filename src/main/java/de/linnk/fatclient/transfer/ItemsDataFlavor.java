package de.linnk.fatclient.transfer;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.linnk.domain.Item;

public class ItemsDataFlavor extends DataFlavor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ItemsDataFlavor() {
		super(Item.class, "Items");
	}

	@Override
	public synchronized void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		super.readExternal(arg0);
	}

	@Override
	public synchronized void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		super.writeExternal(arg0);
	}
	
}
