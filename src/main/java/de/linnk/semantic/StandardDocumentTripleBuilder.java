package de.linnk.semantic;

import thewebsemantic.Thing;
import thewebsemantic.vocabulary.DCTerms;
import thewebsemantic.vocabulary.Sioc;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;
import de.mxro.filesystem.Folder;
import de.mxro.jena.JenaTripleBuilder;

public class StandardDocumentTripleBuilder implements JenaTripleBuilder {
	
	
	final Document doc;
	
	public boolean writeTriples(OntModel toModel, Resource parent,
			Folder files) {
		//toModel.createIndividual(doc.getUniqueURI(), BasicOntology.Document);
		
		//Bean2RDF writer = new Bean2RDF(toModel);
		//writer.save(doc);
		
		//Thing thing = new Thing(toModel);
		//thing.
		
		Thing thing = new Thing(toModel);
		 thing.at(doc.getUniqueURI()).
		   as(DCTerms.class).title(doc.getName()).created(doc.getCreated()).
		   as(Sioc.class).has_creator(thing.at(doc.getCreator().getURI().toString()).isa(Sioc.User.class)).content("content").
		isa(NodeDocument.class);
		
//		
//		seeAlso( thing.at("http://www.mxro.de/me.rdf"))).
//		content("Content of the Document")
		;
		
		
		// thing.asResource()
		 
		return true;
	}

	public StandardDocumentTripleBuilder(Document doc) {
		super();
		this.doc = doc;
	}
	
	
	
}
