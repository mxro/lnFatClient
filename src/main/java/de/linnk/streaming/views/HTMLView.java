package de.linnk.streaming.views;

import mx.gwtutils.MxroGWTUtils;
import de.linnk.domain.Document;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.streaming.activities.WriteHTML;
import de.mxro.filesystem.File;
import de.mxro.filesystem.Folder;

public class HTMLView extends CompositeView {

	public HTMLView(View priorView, boolean useBackground) {
		super(priorView, useBackground);
	}

	@Override
	public boolean stepWriteView(Document toSave, Folder destinationFolder,
			LoadOnDemandDocument newDocumentInDestnationFolder) {
		
		final File htmlFile = destinationFolder.forceFile(MxroGWTUtils.removeExtension(toSave.getFilename())+de.linnk.domain.LinnkConstants.htmlExtension);
		
		
		WriteHTML writeHTML =new WriteHTML(newDocumentInDestnationFolder, this.getPlainView(), htmlFile, LinnkFatClient.application.getSettings().xmlDeclarationForHTMLOutput);
		writeHTML.run();
		
		return true;
	}

}
