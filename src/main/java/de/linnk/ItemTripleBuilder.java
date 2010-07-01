package de.linnk;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

import de.linnk.domain.Item;
import de.mxro.filesystem.Folder;
import de.mxro.jena.JenaTripleBuilder;

public abstract class ItemTripleBuilder<I extends Item> implements JenaTripleBuilder {

	protected final I item;
	
	final
	public boolean writeTriples(OntModel toModel, Resource parent, Folder files) {
		return writeItemTriples(toModel, parent, files);
	}
	
	public abstract boolean writeItemTriples(OntModel toModel, Resource parent, Folder files);
	
	public ItemTripleBuilder(I item) {
		super();
		this.item = item;
	}
	
	
}
