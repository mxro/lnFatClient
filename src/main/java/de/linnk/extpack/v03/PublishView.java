package de.linnk.extpack.v03;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.linnk.streaming.views.RWView;

@XStreamAlias("de.linnk.streaming.PublishView")
public class PublishView {

	private final RWView xmlView;
	
	
	public final RWView getXmlView() {
		return this.xmlView;
	}


	public PublishView(final RWView xmlView) {
		super();
		this.xmlView = xmlView;
	}

	
	
}
