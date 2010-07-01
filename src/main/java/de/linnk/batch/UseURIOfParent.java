package de.linnk.batch;

import de.linnk.Linnk;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;
import de.linnk.streaming.DocumentLoader;
import de.linnk.streaming.views.PlainXMLView;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;

public class UseURIOfParent extends Action {

	@Override
	public String doOnDocument(Document doc) {
		// load parent
		Document owner = DocumentLoader.singelton.loadFromFile(((NodeDocument) doc).getFolder().getFile(URIImpl.create(doc.getOwnerLink())));
		if (owner == null) {
			return "no owner for "+((NodeDocument) doc).getFile().getURI().toString();
		}
		URI ownerURI = URIImpl.create(owner.getUniqueURI());
		URI ownerURIFolder = ownerURI.getFolder();
		if (ownerURIFolder == null) {
			return "no ownerfolder for "+ownerURI.toString();
		}
		URI myFile = ((NodeDocument) doc).getFile().getURI();
		URI relativized = ((NodeDocument) owner).getFolder().getURI().relativize(myFile);
		URI newURI = ownerURIFolder.resolve(relativized);
		doc.setUniqueURI(newURI.toString());
		Linnk.S.saveDocument(doc, ((NodeDocument) doc).getFolder(), new PlainXMLView());
		
		return null;
	}

}
