package de.linnk.streaming.views;



public interface View {
	/**
	 * 
	 * @param documentToSave
	 * @param destinationFolder
	 * @return the file of the newly saved document
	 */
	public LoadOnDemandDocument writeView(de.linnk.domain.Document documentToSave, de.mxro.filesystem.Folder destinationFolder);
	public View getPlainView(); 
}
