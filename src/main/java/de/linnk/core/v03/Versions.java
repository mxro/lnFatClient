package de.linnk.core.v03;

import java.util.Vector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicitCollection;




@XStreamImplicitCollection(value="changes",item="change")
@XStreamAlias("versions")
@Deprecated
public class Versions {
	private Vector<ItemChange> changes;
	public int currentVersion;

	public Versions() {
		super();
		this.changes = new Vector<ItemChange>();
	}
	
	
}
