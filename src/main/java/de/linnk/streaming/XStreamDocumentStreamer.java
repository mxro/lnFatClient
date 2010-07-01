package de.linnk.streaming;




public abstract class XStreamDocumentStreamer extends DocumentStreamer {
	
	protected final LinnkXStream xstream;
	
	public XStreamDocumentStreamer() {
		super();
		//System.out.println("init dom driver");
		this.xstream = LinnkXStream.singelton;
		//System.out.println("inited dom driver");
		
		
	}


}
