package de.linnk.tests;



import thewebsemantic.Thing;
import thewebsemantic.vocabulary.DCTerms;
import thewebsemantic.vocabulary.Sioc;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;

import de.linnk.semantic.BasicOntology;



public class BasicSemanticTests {
	
	public void buildSimpleDocument() {
		String ns = "http://www.linnk.de/basic.rdf#";
		OntModel m = ModelFactory.createOntologyModel( 
				// create without inferecing, OWL Full - With inferecing would be OWL_MEM_MICRO_RULE_INF	
				OntModelSpec.OWL_MEM );
		m.setNsPrefix("linnk", ns);
		
		//Individual doc= BasicOntology.Document.createIndividual("http://www.linnk.de/document1.rdf");
		
		//m.createIndividual("http://www.linnk.de/document2.rdf", BasicOntology.Document);
		//m.createLiteralStatement( BasicOntology.Document, m.createProperty("rdf:ID"), "http://www.linnk.de/document1.rdf");
//		final Individual Document1 = createIndividual( "http://www.linnk.de/basic.rdf#Document1", BasicOntology.Document );
		m.createResource(BasicOntology.Document);
		
	//  m.write(System.out);
		
	}
	
	
	
	public void buildSimpleJenaBeanModel() {
		OntModel m = ModelFactory.createOntologyModel( 
				// create without inferecing, OWL Full - With inferecing would be OWL_MEM_MICRO_RULE_INF	
				OntModelSpec.OWL_MEM );
		
		
		
		Thing thing = new Thing(m);
		 thing.at("http://www.linnk.de/document1.rdf").
		 as(DCTerms.class).
		title("Document1").
		created("2009-09-07T09:33:30Z").
		isa(BasicOntology.Linnk.Document.class).
		has_creator(
		thing.at("http://www.linnk.de/mxro.rdf").isa(Sioc.User.class).
		seeAlso( thing.at("http://www.mxro.de/me.rdf"))).
		content("Content of the Document");
		 
		 RDFWriter w = m.getWriter("RDF/XML-ABBREV");            
         w.setProperty( "blockRules" , "" );
         w.write(m, System.out, null);
	}
}
