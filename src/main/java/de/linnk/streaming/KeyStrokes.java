package de.linnk.streaming;

import java.util.HashMap;

import javax.swing.KeyStroke;

import com.thoughtworks.xstream.annotations.XStreamConverter;

import de.mxro.utils.log.UserError;



public class KeyStrokes {
	public static class Entry {
		
		@XStreamConverter(KeyStrokeConverter.class)
		final KeyStroke stroke;
		final String description;
		
		public Entry(final KeyStroke stroke, final String description) {
			super();
			this.stroke = stroke;
			this.description = description;
		}
	}
	private final HashMap<String, Entry> strokes;
	
	public void setStroke(String id, String description, KeyStroke stroke) {
		if (this.strokes.containsKey(id)) {
			this.strokes.remove(id);
		}
		this.strokes.put(id, new Entry(stroke, description));
	}
	
	public void addStroke(String id, String description, KeyStroke stroke) {
		if (this.strokes.containsKey(id))
			throw new IllegalArgumentException("KeyStrokes.addStroke: Stroke is already registred: "+id+": "+stroke);
		this.strokes.put(id, new Entry(stroke, description));
	}
	
	public KeyStroke getStroke(String id) {
		final Entry entry = this.strokes.get(id);
		if (entry == null) {
			UserError.singelton.log(this, "getStroke: couldn not find stroke: "+id, UserError.Priority.HIGH);
			return null;
		}
		final KeyStroke res = this.strokes.get(id).stroke;
		
		return res;
	}
	
	public KeyStrokes() {
		super();
		this.strokes = new HashMap<String, Entry>();
	}
	
	
}
