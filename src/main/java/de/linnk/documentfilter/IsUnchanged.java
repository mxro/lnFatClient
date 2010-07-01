package de.linnk.documentfilter;


import java.util.HashMap;
import java.util.Map;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;


public class IsUnchanged implements DocumentFilter {
	private final Map<String, Integer> lastChanged;
	private final boolean active;
	
	public boolean acceptDocument(Document doc) {
		final Integer lastVersion = this.lastChanged.get(((NodeDocument) doc).getFile().getURI().toString());
		if (lastVersion == null) {
			this.lastChanged.put(((NodeDocument) doc).getFile().getURI().toString(), Integer.valueOf(doc.getVersions().getCurrentVersion()));
			return false;
		} 
		
		if (lastVersion.equals(Integer.valueOf( doc.getVersions().getCurrentVersion())))
			return this.active;
		this.lastChanged.remove(((NodeDocument) doc).getFile().getURI().toString());
		this.lastChanged.put(((NodeDocument) doc).getFile().getURI().toString(), Integer.valueOf(doc.getVersions().getCurrentVersion()));
		return false;
	}

	public Map<String, Integer> getLastChanged() {
		return this.lastChanged;
	}

	public IsUnchanged(Map<String, Integer> lastChanged, boolean active) {
		super();
		if (lastChanged == null) {
			this.lastChanged = new HashMap<String, Integer>();
		} else {
			this.lastChanged = lastChanged;
		}
		this.active = active;
	}


	public boolean isActive() {
		return this.active;
	}
	
	
}
