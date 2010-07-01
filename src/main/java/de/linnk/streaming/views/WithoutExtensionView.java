package de.linnk.streaming.views;

import de.linnk.domain.Document;
import de.mxro.filesystem.File;
import de.mxro.filesystem.Folder;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;

/**
 * Tries to delete the extension (eg ".xml" / ".html") file that is in the published folder
 * @author mx
 *
 */
public final class WithoutExtensionView extends CompositeView {
	
	private final String extension;
	
	public WithoutExtensionView(View priorView, String extension) {
		super(priorView, false);
		this.extension = extension;
	}

	@Override
	public boolean stepWriteView(Document toSave, Folder destinationFolder,
			LoadOnDemandDocument newDocumentInDestnationFolder) {
		String filename = newDocumentInDestnationFolder.getFile().getName();
		URI uri = URIImpl.create(filename).changeExtension(extension);
		File extensionFile = destinationFolder.getFile(uri);
		if (extensionFile == null) {
			de.mxro.utils.log.UserError.singelton.log(this, "stepWriteView: file not found "+uri+" in \n" +
					" folder "+destinationFolder.getURI(), UserError.Priority.NORMAL);
			return false;
		}
		return extensionFile.delete();
	}

}
