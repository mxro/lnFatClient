package de.linnk.transform;

import de.linnk.domain.Item;

public abstract class ItemTransformation<I extends Item> {
	public abstract I doOnItem(I item);
	
	public abstract boolean accept(Item i);
}
