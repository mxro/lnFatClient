package de.linnk;

import de.linnk.domain.ItemChange;


public interface ChangeHandler {
	public boolean doChange(ItemChange change);
	public boolean undoChange(ItemChange change);
}
