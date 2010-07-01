package de.linnk.streaming.activities;

import java.io.OutputStream;

import de.linnk.fatclient.application.LinnkAlias;
import de.mxro.filesystem.File;
import de.mxro.utils.background.Activity;

public class WriteLinnkAlias implements Activity {

	private final File file;
	private final String destination;
	
	public void run() {
		final LinnkAlias la = new LinnkAlias(this.destination);
		final OutputStream laos = this.file.getOutputStream();
		la.toStream(laos);
	}

	public WriteLinnkAlias(final File file, final String destination) {
		super();
		this.file = file;
		this.destination = destination;
	}

	
	
}
