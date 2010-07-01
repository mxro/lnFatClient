package de.linnk.transform;

import de.linnk.domain.Document;

public abstract class Transformation {
	private final Transformation next;
	
	public static final Transformation NONE = new None();
	
	public final Transformation getNext() {
		return this.next;
	}
	
	public abstract void applyTransformation(Document doc);
	
	public final void transform(Document doc) {
		this.applyTransformation(doc);
		if (this.next != null) {
			this.getNext().transform(doc);
		}
	}
	
	public Transformation(final Transformation next) {
		super();
		this.next = next;
	}

}
