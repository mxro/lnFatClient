package de.linnk.streaming.views;

import de.linnk.domain.Document;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.streaming.activities.WriteLinnkAlias;
import de.mxro.filesystem.File;
import de.mxro.filesystem.Folder;
import de.mxro.utils.Utils;

/**
 * Writes a .linnk file in the destination folder
 * 
 * @author mx
 *
 */
public class LinnkAliasView extends CompositeView {

	@Override
	public boolean stepWriteView(Document toSave, Folder destinationFolder,
			LoadOnDemandDocument newDocumentInDestnationFolder) {
		final File linnkFile = destinationFolder.forceFile(Utils.removeExtension(toSave.getFilename())+de.linnk.domain.LinnkConstants.linnkExtension);
		WriteLinnkAlias writeLinnkAlias = new WriteLinnkAlias(linnkFile, newDocumentInDestnationFolder.getFile().getName());
		
		if (useBackground) {
			LinnkFatClient.application.getBackgroundProcess().addActivity(writeLinnkAlias);
			return true;
		}
		
		writeLinnkAlias.run();
		return true;
	}

	public LinnkAliasView(View priorView, boolean useBackground) {
		super(priorView, useBackground);
	}
	
	
}
