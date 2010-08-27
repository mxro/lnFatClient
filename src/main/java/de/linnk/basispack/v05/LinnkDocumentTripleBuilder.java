package de.linnk.basispack.v05;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

import thewebsemantic.Thing;
import thewebsemantic.vocabulary.DCTerms;
import thewebsemantic.vocabulary.Rdfs;
import thewebsemantic.vocabulary.Sioc;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

import de.linnk.DocumentTripleBuilder;
import de.linnk.domain.Item;
import de.linnk.domain.ProxyItem;
import de.linnk.domain.TextItem;
import de.linnk.extpack.v03.HyperlinkProxy;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.tests.BasicSemanticTests.Linnk;
import de.mxro.filesystem.Folder;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.distributedtree.Linking;
import de.mxro.utils.distributedtree.Linking.LinkVector;

public class LinnkDocumentTripleBuilder extends DocumentTripleBuilder<NodeDocument> {

	
	@SuppressWarnings("unchecked")
	private LinkVector getLinks(Object o) {
		if (!(o instanceof Linking)) return null;
		
		Linking<Object> l = (Linking<Object>) o;
		LinkVector lV = l.getLinkVector();
		return lV;
	}
	
	private <I extends ProxyItem> boolean  writeLinksToOtherDocuments(Class<I> clazz, OntModel toModel, Linnk.Document parent, Folder files) {
		List<Item> items = document.getItems();
		
		for (Item i : items) {
			String label = "";
			TextItem ti = i.getItem(TextItem.class);
			if (ti != null) label = de.mxro.utils.Utils.removeMarkup(ti.getTextData());
			
			Vector<I> pi = de.linnk.gwt.LinnkGWTUtils.getProxies(clazz, i);
			for (I lp : pi) {
				for (String uristr : getLinks(lp)) {
					URI uri = URIImpl.create(uristr);
					URI uriLink = document.getFolder().getURI().resolve(uri);
					//
					// for links to linnk documents
					if (lp instanceof LinnkProxy) {
						URI rdfLink = uriLink.changeExtension(de.linnk.domain.LinnkConstants.rdfExtension);
						parent.links_to(new Thing(toModel).at(rdfLink.toString()).isa(Linnk.Document.class).label(label));
					} 
					// for other kinds of links ...
					else {
						parent.links_to(new Thing(toModel).at(uriLink.toString()).as(Rdfs.class).label(label));
					}
					
				}
			}
		}
		
		return true;
	}
	
	@Override
	public boolean writeDocumentTriples(OntModel toModel, Resource parent,
			Folder files) {		
		// ---
		// check creator and quickfix ..
		// ---
		String creator;
		
		if (this.document.getCreator().getURI() == null) {
			creator = "";
		} else {
		
		  URI creatorURI = URIImpl.create(document.getCreator().getURI());
		  creator = creatorURI.toString();
		 
		}
		if (creator.equals("")) creator =  LinnkFatClient.currentUser.getURI().toString();
		try {
			new java.net.URI(creator);
		} catch (URISyntaxException e) {
			creator = LinnkFatClient.currentUser.getURI().toString();
		}
		
		// --
		// basic doc attibutes
		// ---
		Thing thing = new Thing(toModel);
		URI thisDocURI = URIImpl.create(document.getUniqueURI()).changeExtension(de.linnk.domain.LinnkConstants.rdfExtension);
		Linnk.Document doc = thing.at(thisDocURI.toString()).
		   as(DCTerms.class).title(document.getName()).created(document.getCreated()).
		   as(Sioc.class).has_creator(thing.at(creator.toString()).isa(Sioc.User.class)).
		   label(document.getName()).
		   seeAlso(document.getUniqueURI()).
		   //.content("content").
		   isa(Linnk.Document.class);
		
		// --
		// owner document link
		// --
		if (document.getOwnerLink() != null) {
			URI toOwnerXML = document.getFolder().getURI().resolve(document.getOwnerLink());
			URI toOwnerRDF = toOwnerXML.changeExtension(".rdf");
			doc.has_container(new Thing(toModel).at(toOwnerRDF.toString()).isa(Linnk.Document.class).label("Parent"));
		}
		
		return this.writeLinksToOtherDocuments(LinnkProxy.class, toModel, doc, files) &
		  this.writeLinksToOtherDocuments(HyperlinkProxy.class, toModel, doc, files);
		
	}

	public LinnkDocumentTripleBuilder(NodeDocument document) {
		super(document); 
	}

	
	
}
