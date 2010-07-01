package de.linnk.streaming.views;

import de.linnk.domain.Document;
import de.mxro.filesystem.Folder;

/**
 * Writes no output.
 * @author mx
 *
 */
public class DummyView implements View {


	public LoadOnDemandDocument writeView(Document documentToSave, Folder destinationFolder) {
		
		return null;
	}
	public View getPlainView() {
		return this;
	}
}
