package de.linnk.fatclient.icons;

import javax.swing.ImageIcon;

public class Icons extends de.mxro.swing.Icons {
	public final static Icons singelton = new Icons();
	
	public static ImageIcon getLargeIcon(String name) {
		
		return singelton.getIcon(name, 48, 48, true);
	}
	
	public static ImageIcon getSmallIcon(String name) {
		return singelton.getIcon(name, 14, 14, true);
	}
	
}
