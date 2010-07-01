package de.linnk;

import de.linnk.domain.Document;
import de.linnk.domain.ItemChange;
import de.linnk.domain.User;
import de.mxro.utils.log.UserError;



public abstract class ChangableDocument extends Document 
{
	
	private transient ChangeHandler changeHandler;
	
	
	public ChangeHandler getChangeHandler() {
		if (changeHandler == null) {
			
		  changeHandler =  new ChangeHandler() {

			public boolean doChange(ItemChange change) {
				ChangableDocument.this.touch();
				//System.out.println("LocalDocument Change: \n"+LinnkXStream.singelton.toXML(change));
				if (change.doOnResource(ChangableDocument.this)) {
					ChangableDocument.this.getVersions().addChange(change);
					return true;
				}
				UserError.singelton.log(ChangableDocument.this, "Could not apply change: "+change.getClass(), UserError.Priority.NORMAL);
				return false;
			}

			public boolean undoChange(ItemChange change) {
				ChangableDocument.this.touch();
				if (change.undoOnResource(ChangableDocument.this)) {
					ChangableDocument.this.getVersions().undo();
					return true;
				}
				return false;
			}
			
		};
		}
		return changeHandler;
	}
	/**
	 * Defines what happens if document is changed
	 * must be changed to handle if document is on a server
	 * 
	 *
	 */
	public void setChangeHandler(final ChangeHandler changeHandler) {
		this.changeHandler = changeHandler;
	}
	
	protected ChangableDocument(final User creator, 
	          
	           final String name) {
		super(creator, name);
		
	}
	
	@Override
	public boolean doChange(ItemChange change) {
		return this.getChangeHandler().doChange(change);
	}
	
	@Override
	public boolean undoChange(ItemChange change) {
		return this.getChangeHandler().undoChange(change);
	}
	
}
