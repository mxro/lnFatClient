package de.linnk.streaming.views;


import thewebsemantic.vocabulary.Sioc;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;
import de.linnk.domain.LinnkConstants;
import de.mxro.filesystem.File;
import de.mxro.filesystem.Folder;
import de.mxro.jena.JenaTripleBuilder;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;

public class RDFView extends CompositeView {

	@Override
	public boolean stepWriteView(Document toSave, Folder destinationFolder, LoadOnDemandDocument newDocumentInDestnationFolder) {
		if (!(newDocumentInDestnationFolder.getDocument() instanceof NodeDocument)) {
			UserError.singelton.log(this, "writeView: no LinnkDocument '"+toSave.getUniqueURI()+"'", UserError.Priority.HIGH);
			return false;
		}
		NodeDocument document = (NodeDocument) newDocumentInDestnationFolder.getDocument(); 
		
		//String ns = "http://www.linnk.de/basic.rdf#";
		OntModel m = ModelFactory.createOntologyModel( 
				// create without inferecing, OWL Full - With inferecing would be OWL_MEM_MICRO_RULE_INF	
				OntModelSpec.OWL_MEM );
		m.setNsPrefix("linnk", "http://www.linnk.de/basic.rdf#");
        m.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
        m.setNsPrefix("sioc", Sioc.NS);


		JenaTripleBuilder builder = document.getTripleBuilder();
        builder.writeTriples(m, null, destinationFolder);
		
		File file = destinationFolder.forceFile(URIImpl.create(document.getFile().getName()).changeExtension(LinnkConstants.rdfExtension).toString());
		if (file == null) return false;
		
		RDFWriter w = m.getWriter("RDF/XML-ABBREV");            
        w.setProperty( "blockRules" , "" );
        w.write(m, file.getOutputStream(), null);
       // w.write(m, System.out, null);
		
		return true;
	}

	public RDFView(View priorView, boolean useBackground) {
		super(priorView, useBackground);
	}

	

	
	
}
