package de.linnk.streaming;

import de.linnk.domain.Updatable;
import de.mxro.utils.log.UserError;

public class Updater {
    
	
	
	/**
	 * if given object can be updated an updated version
	 * will be the result
	 * 
	 * @param o
	 * @return
	 */
	public Object update(Object o) {
		
		if (o instanceof Updatable) {
			UserError.singelton.log(this, "update: updates "+o.getClass(), UserError.Priority.INFORMATION);
			return this.update(((Updatable) o).update() );
		}
		// otherwise do nothing
		return o;
	}
}
