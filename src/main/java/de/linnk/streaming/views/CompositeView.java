package de.linnk.streaming.views;

import de.linnk.domain.Document;
import de.linnk.fatclient.application.LinnkFatClient;
import de.mxro.filesystem.Folder;
import de.mxro.utils.background.Activity;

public abstract class CompositeView implements View {
	
	private final View priorView;
	protected final boolean useBackground;
	
	private class WriteViewActivity implements Activity {
		
		private final Document toSave;
		private final Folder destinationFolder;
		private final LoadOnDemandDocument newDocumentInDestnationFolder;
		
		public void run() {
			CompositeView.this.stepWriteView(toSave, destinationFolder, newDocumentInDestnationFolder);
			
		}

		public WriteViewActivity(Document toSave, Folder destinationFolder,
				LoadOnDemandDocument newDocumentInDestnationFolder) {
			super();
			this.toSave = toSave;
			this.destinationFolder = destinationFolder;
			this.newDocumentInDestnationFolder = newDocumentInDestnationFolder;
		}
		
		
		
	}
	
	public final LoadOnDemandDocument writeView(Document documentToSave, Folder destinationFolder) {
		LoadOnDemandDocument newDocumentInDestinationFolder = this.getPriorView().writeView(documentToSave, destinationFolder);
		if (newDocumentInDestinationFolder == null) {
			return null;
		}
			
		if (!useBackground) {
			this.stepWriteView(documentToSave, destinationFolder, newDocumentInDestinationFolder);
			return newDocumentInDestinationFolder;
		} 
		
		LinnkFatClient.application.getBackgroundProcess().addActivity(new WriteViewActivity(documentToSave, destinationFolder, newDocumentInDestinationFolder));
		
		return newDocumentInDestinationFolder;
		
		
	}
	
	public abstract boolean stepWriteView(Document toSave, Folder destinationFolder, LoadOnDemandDocument newDocumentInDestnationFolder);
	
	
	public CompositeView(View priorView, boolean useBackground) {
		super();
		this.useBackground = useBackground;
		this.priorView = priorView;
	}

	public View getPriorView() {
		return priorView;
	}

	public View getPlainView() {
		return this.getPriorView().getPlainView();
	}
	
	
}
