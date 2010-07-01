package de.linnk.fatclient.fromnetbeans;

import java.net.URISyntaxException;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.netbeansguis.CreateURIRootDialog;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;

public class SelectURIDialog {
	
	
	public URI selectURI(NodeDocument doc) {
		 CreateURIRootDialog dialog = new CreateURIRootDialog(new javax.swing.JFrame(), true);
		 
		 String uri = doc.getUniqueURI();
		 if (doc.getUniqueURI().equals( "")) {
			 uri = doc.getFile().getURI().getFolder().toString();
		 }
		 dialog.setURIText(uri);
		 
		 dialog.setVisible(true);
		 
		 if (dialog.getReturnStatus() == CreateURIRootDialog.RET_CANCEL) {
			 return null;
		 }
		 URI root;
		 try {
			root = new URIImpl(dialog.getURIText());
		 } catch (URISyntaxException e) {
			
			return null;
		}
		
		return root;
		 
	}
}
