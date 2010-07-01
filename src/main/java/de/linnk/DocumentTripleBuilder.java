package de.linnk;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

import de.linnk.domain.Document;
import de.mxro.filesystem.Folder;
import de.mxro.jena.JenaTripleBuilder;

public abstract class DocumentTripleBuilder<D extends Document> implements JenaTripleBuilder {
	final protected D document;
	
	final
	public boolean writeTriples(OntModel toModel, Resource parent,
			Folder files) {
		 
		return writeDocumentTriples(toModel, parent, files);
	}
	
	public abstract boolean writeDocumentTriples(OntModel toModel, Resource parent,
			Folder files) ;

	public DocumentTripleBuilder(D document) {
		super();
		this.document = document;
	}
	
	
	
}
