package de.linnk.basispack.v05;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

import de.linnk.ItemTripleBuilder;
import de.linnk.domain.TextItem;
import de.linnk.semantic.BasicOntology;
import de.mxro.filesystem.Folder;

public class TextItemTripleBuilder extends ItemTripleBuilder<TextItem> {
	
	public TextItemTripleBuilder(TextItem item) {
		super(item);
	}

	@Override
	public boolean writeItemTriples(OntModel toModel, Resource parent,
			Folder files) {
		Individual me = toModel.createIndividual(item.getId(), BasicOntology.Text);
		me.setPropertyValue(BasicOntology.hasText, toModel.createLiteral(item.getTextData()));
		//parent.setPropertyValue(BasicOntology.composedOf, me);
		return true;
	}



	
}
