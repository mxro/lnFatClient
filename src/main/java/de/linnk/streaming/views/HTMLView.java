package de.linnk.streaming.views;

import de.linnk.domain.Document;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.streaming.activities.WriteHTML;
import de.mxro.filesystem.File;
import de.mxro.filesystem.Folder;
import de.mxro.utils.Utils;

public class HTMLView extends CompositeView {

	public HTMLView(View priorView, boolean useBackground) {
		super(priorView, useBackground);
	}

	@Override
	public boolean stepWriteView(Document toSave, Folder destinationFolder,
			LoadOnDemandDocument newDocumentInDestnationFolder) {
		
		final File htmlFile = destinationFolder.forceFile(Utils.removeExtension(toSave.getFilename())+de.linnk.domain.LinnkConstants.htmlExtension);
		
		
		WriteHTML writeHTML =new WriteHTML(newDocumentInDestnationFolder, this.getPlainView(), htmlFile, LinnkFatClient.application.getSettings().xmlDeclarationForHTMLOutput);
		writeHTML.run();
		
		return true;
	}

}
